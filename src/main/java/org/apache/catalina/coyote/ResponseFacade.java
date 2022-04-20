package org.apache.catalina.coyote;

import org.apache.catalina.coyote.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.spi.http.HttpHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

/**
 * @author: Mr.Yu
 * @create: 2022-04-13 10:38
 **/
public class ResponseFacade implements HttpServletResponse {

    private final Response response;

    private final ServletOutputStream outputStream;
    private RequestFacade request;

    public ResponseFacade(Response response) {
        this.response = response;
        outputStream = new CoyoteOutputStream((InternalOutputNioBuffer) response.getOutputBuffer(),this);
    }

    public void setRequest(RequestFacade request) {
        this.request = request;
    }

    @Override
    public void addCookie(Cookie cookie) {
        if (isCommitted())
            return;
        response.addCookie(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
        return false;
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        response.setStatus(sc);
        response.setMessage(msg);
        response.setError(true);
    }

    @Override
    public void sendError(int sc) throws IOException {
        response.setStatus(sc);
        response.setError(true);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        response.setStatus(HttpStatus.MOVED_TEMPORARILY.getCode());
        response.setMessage(HttpStatus.MOVED_TEMPORARILY.toString());
        HttpHeader header = new HttpHeader("Location",location);
        response.addHeader(header);
        outputStream.flush();
    }

    @Override
    public void setDateHeader(String name, long date) {

    }

    @Override
    public void addDateHeader(String name, long date) {

    }

    @Override
    public void setHeader(String name, String value) {
        if (isCommitted())
            return;
        response.setHeader(name,value);
    }

    @Override
    public void addHeader(String name, String value) {
        if (isCommitted())
            return;
        response.addHeader(new HttpHeader(name,value));
    }

    @Override
    public void setIntHeader(String name, int value) {
    }

    @Override
    public void addIntHeader(String name, int value) {
    }

    @Override
    public void setStatus(int sc) {
        response.setStatus(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        setStatus(sc);
        response.setMessage(sm);
    }

    @Override
    public int getStatus() {
        return response.getStaus();
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return response.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    @Override
    public void setCharacterEncoding(String charset) {

    }

    @Override
    public void setContentLength(int len) {
        if (isCommitted())
            return;
        response.setContentLength(len);
    }

    @Override
    public void setContentType(String type) {
        if (isCommitted())
            return;
        response.setContentType(type);
    }

    @Override
    public void setBufferSize(int size) {}

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {}

    @Override
    public void resetBuffer() {}

    @Override
    public boolean isCommitted() {
        return response.getIsCommitted();
    }

    @Override
    public void reset() {}

    @Override
    public void setLocale(Locale loc) {}

    @Override
    public Locale getLocale() {
        return null;
    }

    public RequestFacade getRequest() {
        return request;
    }
}
