package org.apache.catalina.coyote;

/**
 * @author: Mr.Yu
 * @create: 2022-04-16 11:44
 **/
public class HttpHeader {

    byte[] name;
    int nameEnd;
    String nameStr;
    byte[] value;
    int valueEnd;
    String valueStr;

    private static final int INITIAL_NAME_SIZE = 64;
    public static final int INITIAL_VALUE_SIZE = 128;
    public static final int MAX_NAME_SIZE = 256;
    public static final int MAX_VALUE_SIZE = 8192;
    public HttpHeader() {
        this(new byte[INITIAL_NAME_SIZE],new byte[INITIAL_VALUE_SIZE]);
    }

    public HttpHeader(byte[] name, byte[] value) {
        this.name = name;
        this.value = value;
    }

    public HttpHeader(String nameStr, String valueStr) {
        this.nameStr = nameStr;
        this.valueStr = valueStr;
    }
}
