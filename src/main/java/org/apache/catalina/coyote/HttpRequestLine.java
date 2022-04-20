package org.apache.catalina.coyote;

/**
 * @author: Mr.Yu
 * @create: 2022-04-16 11:18
 **/
public class HttpRequestLine {

    public byte[] method;
    public int methodEnd;
    public byte[] uri;
    public int uriEnd;
    public byte[] protocol;
    public int protocolEnd;

    public static final int INITIAL_METHOD_SIZE = 16;
    public static final int INITIAL_URI_SIZE = 128;
    public static final int INITIAL_PROTOCOL_SIZE = 16;
    public static final int MAX_METHOD_SIZE = 2048;
    public static final int MAX_URI_SIZE = 32768*2;
    public static final int MAX_PROTOCOL_SIZE = 2048;

    public HttpRequestLine() {
        this(new byte[INITIAL_METHOD_SIZE], 0, new byte[INITIAL_URI_SIZE], 0,
                new byte[INITIAL_PROTOCOL_SIZE], 0);
    }

    public HttpRequestLine(byte[] method, int methodEnd,
                           byte[] uri, int uriEnd,
                           byte[] protocol, int protocolEnd) {

        this.method = method;
        this.methodEnd = methodEnd;
        this.uri = uri;
        this.uriEnd = uriEnd;
        this.protocol = protocol;
        this.protocolEnd = protocolEnd;
    }

    public int indexOf(byte b) {
        for (int i = 0; i < uriEnd; i++) {
            if (uri[i] == b) {
                return i;
            }
        }
        return -1;
    }

    public void recycle() {
        this.methodEnd = 0;
        this.protocolEnd = 0;
        this.uriEnd = 0;
    }
}
