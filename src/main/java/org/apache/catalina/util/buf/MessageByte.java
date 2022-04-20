package org.apache.catalina.util.buf;

import java.nio.ByteBuffer;

/**
 * @author: Mr.Yu
 * @create: 2022-04-02 09:04
 **/
public class MessageByte extends AbstractMessageByte{

    // 记录request（如方法，协议，uri）某一项的字节流，在需要时在进行解码
    protected byte[] buf;
    // 开始处
    protected int start;
    // 结束处
    protected int end;

    public void setBytes(byte[] buf, int start, int len) {
        this.buf = buf;
        this.start = start;
        this.end = start + len;
    }

    @Override
    public ByteBuffer getBuffer() {
        ByteBuffer wrap = ByteBuffer.wrap(buf, start, end - start);
        return wrap;
    }
}
