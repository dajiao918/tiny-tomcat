package org.apache.catalina.coyote;

import org.apache.catalina.connector.endpoint.nio.NioChannel;

import java.io.IOException;

/**
 * @author: Mr.Yu
 * @create: 2022-04-01 21:11
 **/
public interface InputBuffer {


    void init(NioChannel channel) throws IOException;

    void close();

    void recycle();
}