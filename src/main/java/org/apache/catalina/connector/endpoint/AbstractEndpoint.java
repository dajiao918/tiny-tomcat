package org.apache.catalina.connector.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.endpoint.nio.NioEndPoint;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    // 监听客户端请求的端口
    protected int port = 8080;

    // 操作系统底层的阻塞连接队列
    protected int backlog;

    // accept方法持续时间，过期则阻塞
    protected int timeout = 20000;

    protected int readBufferSize = 1024 * 4;

    protected int writeBufferSize = 1024 * 4;

    protected volatile boolean close = false;

    protected NioEndPoint.Handler handler;

    protected int coreThreadSize = 20;
    protected int maxThreadSize = 20;
    protected int blockingThreadSize = 20;

    public void setHandler(NioEndPoint.Handler handler) {
        this.handler = handler;
    }

    public boolean isClose() {
        return close;
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

    protected Executor createExecitor() {
        return new ThreadPoolExecutor(coreThreadSize, maxThreadSize,
                6, TimeUnit.SECONDS,new ArrayBlockingQueue<>(blockingThreadSize));
    }

    protected abstract void bind() throws IOException;

    protected abstract void startInternal() throws IOException;

    public abstract SocketChannel socketAccept() throws IOException;

    /**
     * 注册channel到poller中
     * @param channel
     * @return boolean
     * */
    public abstract boolean setSocketOPtions(SocketChannel channel) throws IOException;

    public abstract void stop();
}
