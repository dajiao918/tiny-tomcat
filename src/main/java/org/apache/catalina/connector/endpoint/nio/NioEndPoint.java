package org.apache.catalina.connector.endpoint.nio;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.BufferHandler;
import org.apache.catalina.connector.endpoint.AbstractEndpoint;
import org.apache.catalina.connector.endpoint.Acceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: Mr.Yu
 * @create: 2022-03-30 16:27
 **/
@Slf4j
public class NioEndPoint extends AbstractEndpoint {

    private ServerSocketChannel serverSocket;

    private Poller poller;

    private Executor executor;

    @Override
    protected void bind() throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port), backlog);
        serverSocket.configureBlocking(true);
        serverSocket.socket().setSoTimeout(timeout);
    }

    @Override
    protected void startInternal() throws IOException {
        poller = new Poller();
        Thread thread = new Thread(poller,"poller");
        thread.start();
        executor = createExecitor();
        acceptorStart();
    }

    private void acceptorStart() {
        Acceptor acceptor = new Acceptor(this);
        Thread thread = new Thread(acceptor, "acceptor");
        thread.start();
    }

    @Override
    public SocketChannel socketAccept() throws IOException {
        SocketChannel accept = serverSocket.accept();
        accept.configureBlocking(false);
        return accept;
    }

    @Override
    public boolean setSocketOPtions(SocketChannel channel) throws IOException {
        NioBufferHandler handler = new NioBufferHandler(readBufferSize, writeBufferSize);
        NioChannel nioChannel = new NioChannel(channel, handler, poller);
        poller.register(nioChannel);
        return true;
    }

    public void processKey(NioChannel channel) {
        SocketProcessor processor = new SocketProcessor();
        processor.setChannel(channel);
        channel.access();
//        log.info(channel + " access " + channel.lastAccess);
        try {
            executor.execute(processor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        close = true;
        poller.getSelector().wakeup();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected class PollerEvent implements Runnable{

        protected NioChannel channel;

        public PollerEvent(NioChannel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            try {
                channel.getChannel().register(poller.getSelector(), SelectionKey.OP_READ,channel);
            } catch (ClosedChannelException e) {
                close = true;
                e.printStackTrace();
            }
        }
    }

    public class Poller implements Runnable {

        private Selector selector;
        private long nextExpiration = 0;
        private LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
        private final AtomicLong wakeupCounter = new AtomicLong();
        private long selectorTimeout = 1000;

        public Poller() throws IOException {
            synchronized (Poller.class) {
                selector = Selector.open();
            }
        }

        @Override
        public void run() {
            log.info("poller begin do event....");
            while (!close) {
                events();
                try {
                    int keyCount = 0;
                    if (wakeupCounter.getAndSet(-1) > 0) {
                        keyCount = selector.selectNow();
                    } else {
                        keyCount = selector.select(selectorTimeout);
                    }
                    wakeupCounter.set(0);

                    if (close) {
                        break;
                    }

                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (keyCount > 0 && iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            try {
//                                log.info("readable event " + keyCount);
                                NioChannel channel = (NioChannel) key.attachment();
                                reg(key,channel);
                                if(channel != null) {
                                    processKey(channel);
                                }
                            } catch (Exception e) {
                                key.cancel();
                                log.info(key.channel().toString() + " compuse close");
                            }
                        }
                    }
                    // 超时channel处理
                    timeout();
                } catch (IOException e) {
                    e.printStackTrace();
                    close = true;
                }
            }
            log.info("poller thread died");
        }

        private void reg(SelectionKey key, NioChannel channel) {
            // 取消关注 选择的事件
            int ops = (~key.readyOps()) & key.interestOps();
            unregister(key,channel,ops);
        }

        private void unregister(SelectionKey key, NioChannel channel, int ops) {
            key.interestOps(ops);
            channel.interestOps(ops);
        }

        private void timeout() {
            long now = System.currentTimeMillis();
            if (now < nextExpiration) {
                return;
            }
            Set<SelectionKey> keys = selector.keys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                NioChannel channel = (NioChannel) key.attachment();
                if (channel == null) {
                    key.cancel();
                } else if ((channel.interestOps()&SelectionKey.OP_READ) == SelectionKey.OP_READ&&
                        channel.getTimeOut() > 0) {
                    long delta = now - channel.lastAccess;
                    long timeOut = channel.getTimeOut();
                    if (delta > timeOut) {
                        log.info("channel超时，清除channel及socket " + channel);
                        channel.close();
                    }
                }
            }
            nextExpiration = System.currentTimeMillis() + 1000;
        }

        private boolean events() {
            Runnable event = null;
            boolean result = false;
            for (int i = 0, size = tasks.size(); i < size && (event = tasks.poll()) != null ; i ++) {
                result = true;
                // 注册socket
                event.run();
            }
            return result;
        }

        public void register(NioChannel channel) {
            channel.interestOps(SelectionKey.OP_READ);
            PollerEvent event = new PollerEvent(channel);
            this.tasks.offer(event);
            if (wakeupCounter.incrementAndGet() == 0)
                selector.wakeup();
        }

        public Selector getSelector() {
            return selector;
        }

        public void closeChannel(NioChannel channel) {
            // TODO
        }
    }

    public class NioBufferHandler implements BufferHandler{

        protected ByteBuffer readBuffer;
        protected ByteBuffer writeBuffer;

        public NioBufferHandler(int readBufferSize, int writeBufferSize) {
            readBuffer = ByteBuffer.allocate(readBufferSize);
            writeBuffer = ByteBuffer.allocate(writeBufferSize);
        }

        @Override
        public ByteBuffer getReadBuffer() {
            return readBuffer;
        }

        @Override
        public ByteBuffer getWriteBuffer() {
            return writeBuffer;
        }
    }

    public interface Handler {
        SocketState process(NioChannel channel) throws IOException;
    }

    public class SocketProcessor implements Runnable {

        NioChannel channel;

        public void setChannel(NioChannel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            try {
                SocketState state = handler.process(channel);
                if (state == SocketState.CLOSE)
                    channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
