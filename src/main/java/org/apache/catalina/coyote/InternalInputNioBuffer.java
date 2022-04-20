package org.apache.catalina.coyote;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.endpoint.nio.NioChannel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author: Mr.Yu
 * @create: 2022-04-01 21:13
 **/
@Slf4j
public class InternalInputNioBuffer implements InputBuffer{
    private static final byte CF = (byte) '\r';
    private static final byte LF = (byte) '\n';
    private static final byte SP = (byte) ' ';
    private static final byte QU = (byte) '?';

    private final Request request;

    private NioChannel channel;

    private boolean parseRequestLine;
    private boolean parseRequestHeader;

    private final HttpRequestLine requestLine;

    private byte[] buf;

    private int pos;

    private int lastValid;

    private int end;
    private boolean close = false;


    public NioChannel getChannel() {
        return channel;
    }

    public byte[] getBody() throws IOException {
        if (close)
            return null;
        if (end == lastValid) {
            if (!fill()) {
                return new byte[0];
            }
        }
        int len = lastValid - end;
        byte[] bytes = null;
        if (len >= 0) {
            bytes = new byte[len];
            System.arraycopy(buf, end, bytes, 0, len);
        }
        end = lastValid;
        return bytes == null ? new byte[0] : bytes;
    }

    public void setChannel(NioChannel channel) {
        this.channel = channel;
    }

    public HttpRequestLine getRequestLine(){
        return requestLine;
    }

    public InternalInputNioBuffer(Request request) {
        this.request = request;
        buf = new byte[1024 * 4];
        pos = 0;
        lastValid = 0;
        end = 0;

        requestLine = new HttpRequestLine();
        parseRequestLine = true;
        parseRequestHeader = true;
    }

    @Override
    public void init(NioChannel channel) {
        this.channel = channel;
    }

    @Override
    public void close() {
        close = true;
    }

    @Override
    public void recycle() {
        request.recycle();
        requestLine.recycle();

        pos = 0;
        lastValid = 0;
        end = 0;
        parseRequestLine = true;
        parseRequestHeader = true;
        close = false;
    }

    // 解析形如 GET /index.jsp HTTP/1.1\r\n
    public boolean parseRequestLine() throws IOException {
        if (!parseRequestLine) {
            return false;
        }

        boolean space = false;
        int methodStart = 0;
        int uriStart = 0;
        int protocolStart = 0;
        // 解析method
        int maxRead = requestLine.method.length;
        while (!space) {
            if (pos >= lastValid) {
                if (!fill()) {
                    return false;
                }
            }
            if (methodStart >= maxRead) {
                if (maxRead*2 < HttpRequestLine.MAX_METHOD_SIZE) {
                    byte[] method = new byte[maxRead*2];
                    System.arraycopy(requestLine.method, 0, method, 0, methodStart);
                    requestLine.method = method;
                    maxRead = requestLine.method.length;
                } else {
                    throw new RuntimeException("method too large");
                }
            }
            if (buf[pos] == SP) {
                requestLine.methodEnd = methodStart;
                space = true;
            } else {
                requestLine.method[methodStart] = buf[pos];
                methodStart ++;
            }
            pos ++;
        }
        space = false;
        // 解析uri
        maxRead = requestLine.uri.length;
        while (!space) {
            if (pos >= lastValid) {
                if (!fill()) {
                    return false;
                }
            }
            if (uriStart >= maxRead) {
                if (maxRead*2 < HttpRequestLine.MAX_URI_SIZE)  {
                    byte[] uri = new byte[maxRead*2];
                    System.arraycopy(requestLine.uri, 0, uri, 0, uriStart);
                    requestLine.uri = uri;
                    maxRead = requestLine.uri.length;
                } else {
                    throw new RuntimeException("uri too large!");
                }
            }
            if (buf[pos] == SP) {
                space = true;
                requestLine.uriEnd = uriStart;
            } else {
                requestLine.uri[uriStart] = buf[pos];
                uriStart ++;
            }
            pos ++;
        }
        // 解析协议
        maxRead = requestLine.protocol.length;
        while (true) {
            if (pos >= lastValid) {
                if (!fill()) {
                    return false;
                }
            }
            if (protocolStart >= maxRead) {
                if (maxRead*2 < HttpRequestLine.MAX_PROTOCOL_SIZE) {
                    byte[] protocol = new byte[maxRead*2];
                    System.arraycopy(requestLine.protocol, 0, protocol, 0, protocolStart);
                    requestLine.protocol = protocol;
                    maxRead = requestLine.protocol.length;
                } else {
                    throw new RuntimeException("protocol too large!");
                }
            }
            if (buf[pos] == CF) {
                requestLine.protocolEnd = protocolStart;
                pos++;
            } else if (buf[pos] == LF) {
                pos ++;
                parseRequestLine = false;
                return true;
            } else {
                requestLine.protocol[protocolStart] = buf[pos];
                protocolStart++;
                pos ++;
            }
        }
    }

    public boolean parseHeader(HttpHeader header) throws IOException {

        while (true) {
            if (pos >= lastValid) {
                if (!fill()) {
                    return false;
                }
            }
            byte b = buf[pos];
            if (b == CF) {
                pos ++;
            } else if (b == LF) {
                pos ++;
                end = pos;//请求头结束处，请求体开始处
                parseRequestHeader = false;
                header.nameEnd = 0;
                header.valueEnd = 0;
                return true;
            } else {
                break;
            }
        }
        int nameStart = 0;
        boolean colon = false;
        // 解析请求头name
        int maxRead = header.name.length;
        while (!colon) {
            if (pos >= lastValid) {
                if (!fill()) {
                    return false;
                }
            }
            if (nameStart >= maxRead) {
                if (2*maxRead < HttpHeader.MAX_NAME_SIZE) {
                    byte[] newName = new byte[2*maxRead];
                    System.arraycopy(header.name, 0, newName, 0, nameStart);
                    header.name = newName;
                    maxRead = header.name.length;
                }else {
                    throw new RuntimeException("header name too large!");
                }
            }
            byte b = buf[pos];
            byte COLON = (byte) ':';
            if (b == COLON) {
                header.nameEnd = nameStart;
                colon = true;
            } else {
                header.name[nameStart] = b;
                nameStart ++;
            }
            pos ++;
        }
        boolean space = false;
        // 跳过空格
        while (!space) {
            if (pos >= lastValid) {
                if (!fill()) {
                    return false;
                }
            }
            if (buf[pos] == SP) {
                space = true;
            }
            pos ++;
        }
        boolean eol = false;
        int valueStart = 0;
        maxRead = header.value.length;
        // 请求头值
        while (!eol) {
            if (pos >= lastValid) {
                if (!fill()) {
                    return false;
                }
            }
            if (valueStart >= maxRead) {
                if (2*maxRead < HttpHeader.MAX_VALUE_SIZE) {
                    byte[] newValue = new byte[2*maxRead];
                    System.arraycopy(header.value, 0, newValue, 0, nameStart);
                    header.value = newValue;
                    maxRead = header.value.length;
                } else {
                    throw new RuntimeException("value is too large");
                }
            }
            byte b = buf[pos];
            if (b == CF) {
                header.valueEnd = valueStart;
                pos ++;
            } else if (b == LF) {
                pos ++;
                eol = true;
            } else {
                header.value[valueStart] = b;
                valueStart ++;
                pos ++;
            }
        }
        return true;
    }

    // 从socket中读取字节流到buf中
    private boolean fill() throws IOException {
        ByteBuffer readBuffer = channel.getBufferHandler().getReadBuffer();
        readBuffer.clear();
        int read = channel.read(readBuffer);
        if (read > 0) {
            readBuffer.flip();
            expand(pos + read);
            readBuffer.get(buf, pos, read);
            lastValid = pos+read;
            return true;
        }
        else if (read == -1) {
            throw new IOException("read end of this stream");
        } else {
            return false;
        }
    }

    private void expand(int newSize) {
        if (newSize > buf.length) {
            byte[] newBuf = new byte[newSize];
            System.arraycopy(buf, 0, newBuf, 0, buf.length);
            buf = newBuf;
        }
    }

    public boolean getParseRequestHeader() {
        return parseRequestHeader;
    }

}
