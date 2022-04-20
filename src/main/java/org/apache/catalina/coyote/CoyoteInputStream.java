package org.apache.catalina.coyote;

import javax.servlet.ServletInputStream;
import java.io.IOException;

/**
 * @author: Mr.Yu
 * @create: 2022-04-17 22:00
 **/
public class CoyoteInputStream extends ServletInputStream {

    InternalInputNioBuffer internalInputNioBuffer;

    private byte[] buf;

    private int pos;

    private int end;

    private boolean close = false;

    public CoyoteInputStream(InternalInputNioBuffer buffer) {
        this.internalInputNioBuffer = buffer;
        loadStream();
    }

    private void loadStream() {
        try {
            buf = internalInputNioBuffer.getBody();
            end = buf.length;
            pos = 0;
        } catch (IOException e) {
            // 抛出异常证明socket已经读完了
            end = 0;
            pos = 0;
            close = true;
        }
    }

    @Override
    public int read() throws IOException {
        if (close)
            return -1;
        if (pos == end) {
            loadStream();
        }
        if (pos == end) {
            return -1;
        }
        return buf[pos++];
    }

    @Override
    public int available() throws IOException {
        if (close)
            return 0;
        return end;
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (close)
            return -1;
        return read(b,0, b.length);
    }

    // 简单实现读数据
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (close)
            return -1;
        if (pos == end) {
            loadStream();
        }
        if (pos == end) {
            return -1;
        }
        int localLen = end - pos;
        if (len > localLen) {
            System.arraycopy(buf,pos,b,off, localLen);
            loadStream();
            int min = Math.min(len - localLen, end - pos);
            System.arraycopy(buf,pos,b,localLen, min);
            return min+localLen;
        } else {
            System.arraycopy(buf,pos,b,off, len);
            return len;
        }
    }

    @Override
    public void close() throws IOException {
        if (close)
            return;
        close = true;
        internalInputNioBuffer.close();
    }
}
