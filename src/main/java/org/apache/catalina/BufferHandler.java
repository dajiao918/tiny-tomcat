package org.apache.catalina;

import java.nio.ByteBuffer;

/**
 * @author: Mr.Yu
 * @create: 2022-04-01 19:33
 **/
public interface BufferHandler {

     ByteBuffer getReadBuffer();
     ByteBuffer getWriteBuffer();

}