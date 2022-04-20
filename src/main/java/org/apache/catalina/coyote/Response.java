package org.apache.catalina.coyote;

import javax.servlet.http.Cookie;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2022-04-01 21:19
 **/
public class Response {

    public static final String BLANK = " ";
    public static final String CRLF = "\r\n";

    private final HttpStatus defaultStatus = HttpStatus.OK;

    private int status = -1;

    private String message = null;

    private String contentType = "text/html;charset=utf-8";

    private int contentLength = -1;

    private final List<HttpHeader> headers = new ArrayList<>();

    private final List<Cookie> cookies = new ArrayList<>();

    private OutputBuffer outputBuffer;

    private final StringBuilder headerAppender = new StringBuilder();
    private boolean isCommitted = false;
    private boolean error = false;
    private Request request;

    public void setRequest(Request req) {
        request = req;
    }

    public OutputBuffer getOutputBuffer() {
        return outputBuffer;
    }

    public void setOutputBuffer(OutputBuffer outputBuffer) {
        this.outputBuffer = outputBuffer;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void addHeader(HttpHeader header){
        if (header.nameStr == null) {
            return;
        }
        if (header.nameStr.equals("Content-Type")) {
            setContentType(header.valueStr);
        } else if (header.nameStr.equals("Content-Length")) {
            try {
                int i = Integer.parseInt(header.valueStr);
                setContentLength(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            headers.add(header);
        }
    }

    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }

    public ByteBuffer buildHeader() {
        //HTTP/1.1 200 OK
        if (status == -1) {
            headerAppender.append("HTTP/1.1").append(BLANK).append(defaultStatus.getCode()).append(BLANK).append(defaultStatus).append(CRLF);
        } else {
            headerAppender.append("HTTP/1.1").append(BLANK).append(status).append(BLANK).append(message).append(CRLF);
        }
        //Date: Sat, 31 Dec 2005 23:59:59 GMT
        headerAppender.append("Date:").append(BLANK).append(new Date()).append(CRLF);
        headerAppender.append("Content-Type:").append(BLANK).append(contentType).append(CRLF);
        if (contentLength != -1) {
            headerAppender.append("Content-Length:").append(BLANK).append(contentLength).append(CRLF);
        }
        if (!error) {
            headerAppender.append("Connection:").append(BLANK).append("keepAlive").append(CRLF);
        } else {
            headerAppender.append("Connection:").append(BLANK).append("close").append(CRLF);
        }
        for (HttpHeader header : headers) {
            headerAppender.append(header.nameStr).append(":").append(BLANK).append(header.valueStr).append(CRLF);
        }
        if (cookies.size() > 0) {
            for (Cookie cookie : cookies) {
                headerAppender.append("Set-Cookie:").append(BLANK).append(cookie.getName()).append("=").append(cookie.getValue()).append(CRLF);
            }
        }
        headerAppender.append(CRLF);
        byte[] bytes = headerAppender.toString().getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        return buffer;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setHeader(String name, String value) {
        for (HttpHeader header : headers) {
            if (header.nameStr.equals(name)) {
                header.valueStr = value;
                return;
            }
        }
        addHeader(new HttpHeader(name,value));
    }

    public int getStaus() {
        return status;
    }

    public String getContentType() {
        return contentType;
    }

    void setCommitted(boolean b) {
        this.isCommitted = b;
    }

    public boolean getIsCommitted() {
        return this.isCommitted;
    }

    public void recycle() {
        status = -1;
        message = null;
        contentType = "text/html;charset=utf-8";
        contentLength = -1;
        headers.clear();
        cookies.clear();
        isCommitted = false;
        error = false;
    }

    public Request getRequest() {
        return request;
    }

    // 主要设置response的状态是error
    private void error(HttpStatus status) {
        setStatus(status.getCode());
        setMessage(status.toString());
        setError(true);
    }

    //输出异常信息到socket
    public void sendError(HttpStatus status, String errorMsg) {
        error(status);
        if (errorMsg != null) {
            byte[] bytes = errorMsg.getBytes();
            getOutputBuffer().doWrite(bytes, 0, bytes.length);
        }
    }

    public boolean getError() {
        return error;
    }

    // 刷新缓冲区
    public void finishResponse() {
        outputBuffer.finish();
        if (outputBuffer.isChunk()) {
            outputBuffer.writeCRLF();
            outputBuffer.writeCRLF();
        }
    }
}
