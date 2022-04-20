package org.apache.catalina;

import org.apache.catalina.coyote.Adapter;

import java.io.IOException;

/**
 * @author: Mr.Yu
 * @create: 2022-03-30 15:38
 **/
public interface ProtocolHandler {

    void setAdapter(Adapter adapter);

    void init() throws IOException;

    void stop();

    void start() throws IOException;
}
