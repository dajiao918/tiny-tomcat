package org.apache.catalina.connector.endpoint.nio;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.BufferHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author: Mr.Yu
 * @create: 2022-04-01 19:27
 **/
@Slf4j
public class NioChannel implements Channel {

    protected SocketChannel sc;

    protected int interestOps;

    protected BufferHandler bufferHandler;

    protected NioEndPoint.Poller poller;

    protected long lastAccess;

    private int timeout = 20000;

    public NioChannel(SocketChannel sc, BufferHandler handler, NioEndPoint.Poller poller) {
        this.sc = sc;
        this.bufferHandler = handler;
        this.poller = poller;
    }

    public void interestOps(int interestOps) {
        this.interestOps = interestOps;
    }

    public int interestOps() {
        return interestOps;
    }

    public NioEndPoint.Poller getPoller() {
        return poller;
    }

    public void access() {
        lastAccess = System.currentTimeMillis();
    }

    public SocketChannel getChannel() {
        return sc;
    }

    public BufferHandler getBufferHandler() {
        return bufferHandler;
    }

    public int read(ByteBuffer buffer) throws IOException {
        return sc.read(buffer);
    }

    public void write(ByteBuffer buffer) throws IOException {
        sc.write(buffer);
    }

    @Override
    public boolean isOpen() {
        return sc.isOpen();
    }

    @Override
    public void close() {
        SelectionKey key = sc.keyFor(poller.getSelector());
        if (key != null && key.isValid())
            key.cancel();
        try {
            sc.socket().close();
            if (sc.isOpen()) {
                sc.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTimeOut() {
        return timeout;
    }
}
