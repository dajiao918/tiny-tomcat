import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author: Mr.Yu
 * @create: 2022-04-02 11:08
 **/
@Slf4j
public class TestSelector {

    public static void main(String[] args) throws IOException {
        System.out.println(System.getProperty("user.dir"));
    }

    public class Poller implements Runnable{
        Selector selector;
        ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

        public Poller() throws IOException {
            selector = Selector.open();
        }

        public void register(SocketChannel channel) {
            tasks.add(()->{
                try {
                    channel.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup();
        }

        @Override
        public void run() {
            while (true) {
                Runnable runnable = tasks.poll();
                if (runnable != null) {
                    runnable.run();
                }
                try {
                    selector.select();
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            SocketChannel sc = (SocketChannel) key.channel();
                            log.info("remoteAddr: "+sc.socket().getInetAddress().getHostAddress());
                            log.info("localName: "+sc.socket().getLocalAddress().getHostName());
                            log.info("remoteHost: "+sc.socket().getInetAddress().getHostName());
                            log.info("localAddr: "+sc.socket().getLocalAddress().getHostAddress());
                            log.info("remotePort: "+sc.socket().getPort());
                            log.info("localPort: "+sc.socket().getLocalPort());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
