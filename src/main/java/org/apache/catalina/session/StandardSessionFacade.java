package org.apache.catalina.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

/**
 * @author: Mr.Yu
 * @create: 2022-04-19 19:05
 **/
public class StandardSessionFacade implements HttpSession {

    private StandardSession session;

    public StandardSessionFacade(StandardSession standardSession) {
        this.session = standardSession;
    }

    @Override
    public long getCreationTime() {
        return session.getCreationTime();
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    @Override
    public ServletContext getServletContext() {
        return session.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return session.getAttribute(name);
    }

    @Override
    public Object getValue(String name) {
        return session.getValue(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return session.getAttributeNames();
    }

    @Override
    public String[] getValueNames() {
        return session.getValueNames();
    }

    @Override
    public void setAttribute(String name, Object value) {
        session.setAttribute(name,value);
    }

    @Override
    public void putValue(String name, Object value) {
        session.putValue(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }

    @Override
    public void removeValue(String name) {
        session.removeValue(name);
    }

    @Override
    public void invalidate() {
        session.invalidate();
    }

    @Override
    public boolean isNew() {
        return session.isNew();
    }
}
