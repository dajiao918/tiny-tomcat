package org.apache.catalina.coyote;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.ProtocolHandler;
import org.apache.catalina.connector.endpoint.AbstractEndpoint;
import org.apache.catalina.connector.endpoint.nio.NioChannel;
import org.apache.catalina.connector.endpoint.nio.NioEndPoint;
import org.apache.catalina.connector.endpoint.nio.SocketState;

import java.io.IOException;

/**
 * @author: Mr.Yu
 * @create: 2022-03-30 19:32
 **/
@Slf4j
public class HttpNioProtocol implements ProtocolHandler {

    private AbstractEndpoint endpoint;
    private Adapter adapter;
    private final String prefix = "http-nio";

    public HttpNioProtocol() {
        HttpConnectionHandler handler = new HttpConnectionHandler();
        endpoint = new NioEndPoint();
        endpoint.setHandler(handler);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void init() throws IOException {
        log.info("HttpNioProtocol init");
        endpoint.setName(prefix + "-");
        endpoint.init();
    }

    @Override
    public void stop() {
        try {
            endpoint.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() throws IOException {
        log.info("HttpNioProtocol start");
        endpoint.start();
    }

    @Override
    public void pause() {
        endpoint.pause();
    }

    @Override
    public void destroy() {
        //
    }

    public class HttpConnectionHandler implements NioEndPoint.Handler{

        @Override
        public SocketState process(NioChannel channel) throws IOException {

            HttpNioProcessor processor = new HttpNioProcessor(endpoint);
            processor.setAdapter(adapter);

            SocketState state = processor.process(channel);
            if (state == SocketState.LONG) {
                log.info("反注册事件");
                channel.getPoller().register(channel);
            }
            return state;
        }
    }

}
