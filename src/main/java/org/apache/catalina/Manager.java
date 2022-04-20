package org.apache.catalina;

import org.apache.catalina.session.Session;

import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * @author: Mr.Yu
 * @create: 2022-04-19 11:26
 **/
public interface Manager {

    public Container getContainer();

    public void setContainer(Container container);

    public String getInfo();

    public int getActiveSessions();

    public void add(Session session);

    public void changeSessionId(Session session);

    public Session createEmptySession();

    public Session createSession(String sessionId);

    public Session findSession(String id) ;

    public Session[] findSessions();

    public void remove(Session session);

    public void backgroundProcess();
}