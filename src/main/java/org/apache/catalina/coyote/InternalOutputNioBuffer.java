package org.apache.catalina.coyote;

import org.apache.catalina.connector.endpoint.nio.NioChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author: Mr.Yu
 * @create: 2022-04-01 21:14
 **/
public class InternalOutputNioBuffer implements OutputBuffer{

    private final Response response;

    private NioChannel channel;

    private boolean commit;
    private boolean chunk = false;
    private boolean finished = false;


    public InternalOutputNioBuffer(Response response) {
        this.response = response;
    }

    @Override
    public void init(NioChannel channel) {
        this.channel = channel;
        commit = false;
    }

    @Override
    public void doWrite(byte[] b, int off, int len) {
        if (finished)
            return;
        ByteBuffer buffer = channel.getBufferHandler().getWriteBuffer();
        if (len > buffer.capacity()) {
            ByteBuffer temp = ByteBuffer.allocate(len);
            temp.put(b, off, len);
            flushBuffer(temp);
        } else if (buffer.position() == buffer.capacity() ||
                buffer.remaining() < len) {
            flushBuffer(buffer);
            buffer.clear();
            buffer.put(b,off,len);
        } else {
            buffer.put(b, off, len);
        }
    }

    private void flushBuffer(ByteBuffer buffer) {
        if (!commit) {
            if (response.getContentLength() == -1) {
                chunk = true;
                HttpHeader header = new HttpHeader();
                header.nameStr = "Transfer-Encoding";
                header.valueStr = "chunked";
                response.addHeader(header);
            }
            ByteBuffer buildHeader = response.buildHeader();
            writeToSocket(buildHeader);
            commit = true;
            response.setCommitted(true);
        }
        writeToSocket(buffer);
        if (chunk) {
            writeCRLF();
        }
    }

    private void writeToSocket(ByteBuffer buffer) {
        try {
            buffer.flip();
            if (buffer.limit() == 0)
                return;
//            int oldLimit = buffer.limit();
//            System.out.print(Charset.defaultCharset().decode(buffer).toString());
//            buffer.limit(oldLimit);
//            buffer.position(0);
            // 应该在selector绑定的情况下再进行读写
            channel.write(buffer);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCRLF() {
        ByteBuffer buffer = channel.getBufferHandler().getWriteBuffer();
        buffer.clear();
        buffer.put((byte) '\r');
        buffer.put((byte) '\n');
        writeToSocket(buffer);
    }

    @Override
    public void flush() {

        if (finished) {
            return;
        }

        ByteBuffer buffer = channel.getBufferHandler().getWriteBuffer();
        ByteBuffer buildHeader = null;
        if (!commit) {
            if (response.getContentLength() == -1) {
                response.setContentLength(buffer.position());
            }
            buildHeader = response.buildHeader();
            commit = true;
            response.setCommitted(true);
        }
        if (buildHeader == null) {
            writeToSocket(buffer);
        } else {
            writeToSocket(buildHeader);
            writeToSocket(buffer);
        }
    }

    @Override
    public void finish() {
        if (!finished) {
            flush();
        }
        finished = true;
    }

    @Override
    public void recycle() {
        response.recycle();
        commit = false;
        chunk = false;
        finished = false;
    }

    @Override
    public boolean isChunk() {
        return chunk;
    }
}
