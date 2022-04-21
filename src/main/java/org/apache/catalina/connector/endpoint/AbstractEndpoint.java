package org.apache.catalina.connector.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.endpoint.nio.NioEndPoint;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

/**
 * 负责创建线程池
 * 开启acceptor来接收socket连接
 * 创建poller对象处理socket事件
 * 创建processor处理http解析，生成request、response对象
 * 调用adapter将请求发送到容器
 * @author: Mr.Yu
 * @create: 2022-03-30 15:44
 **/
@Slf4j
public abstract class AbstractEndpoint {

    protected boolean bind = false;

    protected volatile boolean running = false;

    protected volatile boolean paused = false;

    protected Acceptor acceptor;

    protected final CountDownLatch acceptorLatch = new CountDownLatch(1);

    protected final CountDownLatch pollerLatch = new CountDownLatch(1);

    // 监听客户端请求的端口
    protected int port = 8080;

    // 操作系统底层的阻塞连接队列
    protected int backlog;

    // accept方法持续时间，过期则阻塞
    protected int timeout = 20000;

    protected int readBufferSize = 1024 * 4;

    protected int writeBufferSize = 1024 * 4;

    protected NioEndPoint.Handler handler;

    protected String name;

    protected int coreThreadSize = 20;
    protected int maxThreadSize = 20;
    protected int blockingThreadSize = 20;
    private InetSocketAddress address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHandler(NioEndPoint.Handler handler) {
        this.handler = handler;
    }

    public void init() throws IOException {
        log.info("endpoint init");
        if (!bind) {
            bind = true;
            bind();
        }
    }

    public void start() throws IOException {
        log.info("endpoint start");
        if (!bind) {
            bind();
        }
        startInternal();
    }

    protected Executor createExecutor() {
        ExecutorThreadFactory threadFactory =
                new ExecutorThreadFactory(getName() + "exec-", true);
        return new ThreadPoolExecutor(coreThreadSize, maxThreadSize,
                6, TimeUnit.SECONDS,new ArrayBlockingQueue<>(blockingThreadSize), threadFactory);
    }

    protected abstract void bind() throws IOException;

    protected abstract void unbind() ;

    protected abstract void startInternal() throws IOException;

    public abstract SocketChannel socketAccept() throws IOException;

    /**
     * 注册channel到poller中
     * @param channel
     * @return boolean
     * */
    public abstract boolean setSocketOPtions(SocketChannel channel) throws IOException;

    public void stop() {
        stopInternal();
        if (bind) {
            unbind();
            bind = false;
        }
    }

    public abstract void stopInternal();

    public void pause() {
        if (running && !paused) {
            paused = true;
            // 解锁阻塞的acceptor
            unlockAcceptor();
        }
    }

    protected void unlockAcceptor() {
        // 如果acceptor已经停止了，不需要唤醒
        if (acceptor.getState() == Acceptor.AcceptorState.PAUSED) {
            return;
        }
        Socket socket = null;
        try {
            if (address == null) {
                address = new InetSocketAddress("localhost", port);
            }
            socket = new Socket();
            // 连接acceptor，达到唤醒的目的
            socket.connect(address);

            int waitTime = 1000;
            // 等待socket连接
            while (waitTime > 0 &&
                    acceptor.getState() == Acceptor.AcceptorState.RUNNING) {
                Thread.sleep(5);
                waitTime -= 5;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRunning() {
        return running;
    }
}
