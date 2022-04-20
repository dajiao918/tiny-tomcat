package org.apache.catalina.coyote;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * @author: Mr.Yu
 * @create: 2022-04-17 17:10
 **/
public class CoyoteOutputStream extends ServletOutputStream {

    private static final int DEFAULT_SIZE = 1024*4;

    private final int size;

    private final int start;

    private int end;

    private byte[] body;

    private final InternalOutputNioBuffer outputNioBuffer;
    private final ResponseFacade response;
    private boolean close = false;

    public CoyoteOutputStream(InternalOutputNioBuffer buffer, ResponseFacade response) {
        this(buffer,DEFAULT_SIZE,response);
    }

    public CoyoteOutputStream(InternalOutputNioBuffer buffer,int size, ResponseFacade response) {
        this.outputNioBuffer = buffer;
        if (size < 1024) {
            size = 1024;
        }
        this.size = size;
        this.start = this.end = 0;
        this.body = new byte[size];
        this.response = response;
    }

    @Override
    public void write(int b) throws IOException {
        if (close)
            return;
        append((byte) b);
    }

    private void append(byte b) {
        if (end >= size) {
            this.outputNioBuffer.doWrite(body, start,end);
            end = start;
        }
        body[end++] = b;
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (close)
            return;
        write(b,0,b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (close)
            return;
        if (len >= size) {
            this.outputNioBuffer.doWrite(b,off,len);
        } else if (end + len >= size) {
            this.outputNioBuffer.doWrite(body, start,end);
            end = start;
            body = b;
            end = len - off - 1;
        } else {
            for (int i = 0; i < len; i++) {
                body[end++] = b[i];
            }
        }
    }

    @Override
    public void flush() throws IOException {
        if (close)
            return;
        this.outputNioBuffer.doWrite(body, start, end);
        this.outputNioBuffer.flush();
    }
    // 当用户调用close之后，数据流不再支持写出数据和读入数据

    @Override
    public void close() throws IOException {
        if (close)
            return;
        response.getRequest().getInputStream().close();
        this.outputNioBuffer.finish();
        close = true;
    }
}
