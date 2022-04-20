package org.apache.catalina.coyote;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Manager;
import org.apache.catalina.Wrapper;
import org.apache.catalina.session.Session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author: Mr.Yu
 * @create: 2022-04-01 21:19
 **/
@Slf4j
@Data
public class Request{

    private InternalInputNioBuffer inputBuffer;

    private String method;

    private String uri;

    private String scheme;

    private int serverPort;

    private String remoteAddr;

    private String localAddr;

    private String remoteHostName;

    private String localHostName;

    private int localPort;

    private int remotePort;

    private int contentLength;

    private String contentType;
    private String queryString;

    private Map<String,String[]> params = new HashMap<>();
    private String requestedSessionId;
    private boolean requestedSessionURL;
    private boolean requestedSessionCookie;

    private Map<String, String> headers = new HashMap<>();

    private List<javax.servlet.http.Cookie> cookies = new ArrayList<>();
    private Host host;
    private Context context;
    private Wrapper wrapper;
    private boolean parseParams = false;
    private String servletPath = null;
    private Response response;
    private Session session = null;

    public void setUri(String uri) {
        this.uri = uri;
        int index = uri.indexOf("/",1);
        if (index != -1)
            servletPath = uri.substring(index);
    }

    public void addCookies(Cookie cookie) {
        cookies.add(cookie);
    }

    public void addHeaders(String name, String value) {
        headers.put(name, value);
    }


    public void setRequestedSessionId(String requestedSessionId) {
        this.requestedSessionId = requestedSessionId;
    }

    public void setRequestedSessionURL(boolean b) {
        this.requestedSessionURL = b;
    }

    public void setRequestedSessionCookie(boolean b) {
        this.requestedSessionCookie = b;
    }

    public boolean getRequestedSessionIdFromCookie() {
        return requestedSessionCookie;
    }

    public boolean getRequestedSessionIdFromURL() {
        return requestedSessionURL;
    }

    public String getParameter(String name) {
        if (!parseParams) {
            String val1 = queryString;
            String[] val2 = val1.split("&");
            for (String string : val2) {
                String[] val3 = string.split("=");
                if (val3.length > 1) {
                    String[] val4 = val3[1].split(",");
                    params.put(val3[0], val4);
                } else {
                    params.put(val3[0],new String[0]);
                }
            }
            parseParams = true;
        }
        return params.get(name)[0];
    }

    public int getServerPort() {
        return serverPort;
    }

    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    public HttpSession getSession(boolean create){
        if (session != null && session.isValid()) {
            return session.getSession();
        }
        session = null;
        Manager manager = context.getManager();
        if (manager == null)
            return null;
        if (requestedSessionId != null) {
            session = manager.findSession(requestedSessionId);
            if (session != null && session.isValid()) {
                session.access();
                return session.getSession();
            }
        }
        if (!create) {
            return null;
        }
        String sessionId = requestedSessionId;
        session = manager.createSession(sessionId);
        if (session != null) {
            Cookie cookie = new Cookie("JSESSIONID", session.getId());
            cookie.setMaxAge(context.getSessionTimeOut() * 60);
            response.addCookie(cookie);
            session.access();
        }
        if (session == null)
            return null;
        return session.getSession();
    }

    public void recycle() {
        method = "";
        uri = "";
        scheme = "";
        contentLength = -1;
        contentType = "";
        queryString = "";
        params.clear();
        requestedSessionId = "";
        requestedSessionURL = false;
        requestedSessionCookie = false;
        cookies.clear();
        host = null;
        context = null;
        wrapper = null;
        if (session != null) {
            session.endAccess();
        }
    }

    public String[] getParameterValues(String name) {
        return params.get(name);
    }

    public String getRemoteHost() {
        return null;
    }

    public String getServletPath() {
        return servletPath;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

}
