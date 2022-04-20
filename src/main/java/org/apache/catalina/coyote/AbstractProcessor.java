package org.apache.catalina.coyote;

import lombok.Data;
import org.apache.catalina.connector.endpoint.AbstractEndpoint;
import org.apache.catalina.connector.endpoint.nio.NioChannel;
import org.apache.catalina.connector.endpoint.nio.SocketState;

import java.io.IOException;

/**
 * @author: Mr.Yu
 * @create: 2022-04-01 21:01
 **/
@Data
public abstract class AbstractProcessor {

    protected Request request;

    protected Response response;

    protected InternalInputNioBuffer inputBuffer;

    protected OutputBuffer outputBuffer;

    protected Adapter adapter;

    protected AbstractEndpoint endpoint;

    protected NioChannel nioChannel;

    public abstract SocketState process(NioChannel channel) throws IOException;

}
