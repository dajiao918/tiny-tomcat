package org.apache.catalina.connector.endpoint;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * 连接器，处理socket连接
 * @author: Mr.Yu
 * @create: 2022-03-30 16:18
 **/
@Slf4j
public class Acceptor implements Runnable{

    AbstractEndpoint endpoint;

    public Acceptor(AbstractEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void run() {
        log.info("socket accept...");
        try {
            while (!endpoint.isClose()) {
                SocketChannel channel = null;
                try {
                    channel = endpoint.socketAccept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (channel != null && !endpoint.setSocketOPtions(channel)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            endpoint.stop();
        }
        log.info("acceptor thread died");
    }
}
