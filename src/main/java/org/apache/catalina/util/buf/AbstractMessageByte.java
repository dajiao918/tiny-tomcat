package org.apache.catalina.util.buf;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author: Mr.Yu
 * @create: 2022-04-02 09:16
 **/
public abstract class AbstractMessageByte {

    protected Charset charset = StandardCharsets.UTF_8;


    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String decode() {
        ByteBuffer buffer = getBuffer();
        return charset.decode(buffer).toString();
    }

    public abstract void setBytes(byte[] buf, int start, int len);

    public abstract ByteBuffer getBuffer();

}
