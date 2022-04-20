package org.apache.catalina.coyote;

import org.apache.catalina.connector.endpoint.nio.NioChannel;

/**
 * @author: Mr.Yu
 * @create: 2022-04-01 21:11
 **/
public interface OutputBuffer {
    void init(NioChannel channel);

    void doWrite(byte[] b, int off, int len);

    void flush();

    void finish();

    void recycle();

    boolean isChunk();

    void writeCRLF();
}