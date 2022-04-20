package org.apache.catalina.coyote;

import com.sun.org.apache.regexp.internal.RE;
import org.apache.catalina.Context;
import org.apache.catalina.coyote.CoyoteInputStream;
import org.apache.catalina.coyote.Request;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.http.Cookie;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Mr.Yu
 * @create: 2022-04-13 10:33
 **/
public class RequestFacade implements HttpServletRequest {

    private final Request request;

    private final ServletInputStream servletInputStream;

    private ResponseFacade response;

    private final Map<String,Object> attributes = new ConcurrentHashMap<>();

    public RequestFacade(Request request) {
        this.request = request;
        servletInputStream = new CoyoteInputStream(request.getInputBuffer());
    }

    void setResponse(ResponseFacade response){
        this.response = response;
    }

    ResponseFacade getResponse() {
        return this.response;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public javax.servlet.http.Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String name) {
        return 0;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return null;
    }

    @Override
    public int getIntHeader(String name) {
        return 0;
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return request.getContext().getPath();
    }

    @Override
    public String getQueryString() {
        return request.getQueryString();
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return request.getRequestedSessionId();
    }

    @Override
    public String getRequestURI() {
        return request.getUri();
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return request.getServletPath();
    }

    @Override
    public HttpSession getSession(boolean create) {
        return request.getSession(create);
    }

    @Override
    public HttpSession getSession() {
        return request.getSession(true);
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return request.getRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return request.getRequestedSessionIdFromURL();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, IllegalStateException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String name) throws IOException, IllegalStateException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return request.getContentLength();
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return servletInputStream;
    }

    @Override
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return request.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        return request.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return null;
    }

    @Override
    public String getProtocol() {
        return request.getScheme();
    }

    @Override
    public String getScheme() {
        String scheme = request.getScheme();
        int index = scheme.indexOf("/");
        if (index > -1) {
            scheme = scheme.substring(0,index);
        }
        return scheme;
    }

    @Override
    public String getServerName() {
        return request.getHost().getName();
    }

    @Override
    public int getServerPort() {
        return request.getServerPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    @Override
    public void setAttribute(String name, Object o) {
        if (name == null || name.equals(""))
            return;
        Context context = request.getContext();
        List<Object> eventListeners = context.getEventListeners();
        boolean replaced = attributes.containsKey(name);
        if (eventListeners.size() > 0) {
            ServletRequestAttributeEvent event;
            if (replaced) {
                Object oldValue = attributes.get(name);
                event = new ServletRequestAttributeEvent(context.getServletContext(), this, name, oldValue);
            } else {
                event = new ServletRequestAttributeEvent(context.getServletContext(), this, name, o);
            }
            for (Object listener : eventListeners) {
                if (listener instanceof ServletRequestAttributeListener) {
                    if (replaced) {
                        ((ServletRequestAttributeListener) listener).attributeReplaced(event);
                    } else {
                        ((ServletRequestAttributeListener) listener).attributeAdded(event);
                    }
                }
            }
        }
        attributes.put(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        if (name == null || name.equals("") || !attributes.containsKey(name))
            return;
        Context context = request.getContext();
        List<Object> eventListeners = context.getEventListeners();
        Object value = attributes.remove(name);
        if (eventListeners.size() > 0) {
            ServletRequestAttributeEvent event =
                    new ServletRequestAttributeEvent(context.getServletContext(), this, name, value);
            for (Object listener : eventListeners) {
                if (listener instanceof ServletRequestAttributeListener) {
                    ((ServletRequestAttributeListener) listener).attributeRemoved(event);
                }
            }
        }

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return getServletContext().getRequestDispatcher(path);
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return request.getRemotePort();
    }

    @Override
    public String getLocalName() {
        return request.getLocalHostName();
    }

    @Override
    public String getLocalAddr() {
        return request.getLocalAddr();
    }

    @Override
    public int getLocalPort() {
        return request.getLocalPort();
    }

    @Override
    public ServletContext getServletContext() {
        return request.getContext().getServletContext();
    }

    @Override
    public AsyncContext startAsync() {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    public void setResponseFacade(ResponseFacade facade) {
        this.response = facade;
    }
}
