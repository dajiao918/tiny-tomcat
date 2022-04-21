package org.apache.catalina.connector.endpoint;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * 连接器，处理socket连接
 * @author: Mr.Yu
 * @create: 2022-03-30 16:18
 **/
@Slf4j
public class Acceptor implements Runnable{

    private final AbstractEndpoint endpoint;

    private AcceptorState state = AcceptorState.NEW;

    private final CountDownLatch latch;

    public Acceptor(AbstractEndpoint endpoint, CountDownLatch latch) {
        this.endpoint = endpoint;
        this.latch = latch;
    }

    public AcceptorState getState() {
        return state;
    }

    @Override
    public void run() {
        log.info("socket accept...");
        try {
            while (endpoint.isRunning()) {
                // 检测endpoint是否停止
                while (endpoint.isPaused() && endpoint.isRunning()) {
                    state = AcceptorState.PAUSED;
                    try {
                        // 等待countdown
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!endpoint.isRunning()) {
                    break;
                }
                state = AcceptorState.RUNNING;
                SocketChannel channel = null;
                try {
                    channel = endpoint.socketAccept();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                if (!endpoint.isPaused() && endpoint.isRunning()) {
                    if (!endpoint.setSocketOPtions(channel)) {
                        closeChannel(channel);
                        break;
                    }
                } else {
                    closeChannel(channel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            endpoint.stop();
        }
        state = AcceptorState.ENDED;
        log.info("acceptor thread died");
    }

    private void closeChannel(SocketChannel channel) {
        try {
            channel.socket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum AcceptorState {
        NEW,RUNNING,PAUSED,ENDED
    }

}
