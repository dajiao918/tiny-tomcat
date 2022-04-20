package org.apache.catalina.session;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.core.LifecycleBase;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Mr.Yu
 * @create: 2022-04-19 11:29
 **/
public class StandardManager extends LifecycleBase implements Manager {

    private static final String info = "org.apache.catalina.session.StandardManager/1.0";

    private final Map<String,Session> sessionMap = new ConcurrentHashMap<>();

    private int processExpiresFrequency = 6;

    private int count = 0;

    private Container container;

    private boolean threadDone = false;

    public StandardManager(Container container) {
        this.container = container;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public int getActiveSessions() {
        return sessionMap.size();
    }

    @Override
    public void add(Session session) {
        sessionMap.put(session.getId(), session);
    }

    @Override
    public void changeSessionId(Session session) {
        session.setId(session.getId(),false);
    }

    @Override
    public Session createEmptySession() {
        return new StandardSession(this);
    }

    @Override
    public Session createSession(String sessionId) {
        Session session = createEmptySession();

        if (sessionId == null) {
            sessionId = generateSessionId();
        }
        session.setCreationTime(System.currentTimeMillis());
        session.setMaxInactiveInterval(((Context)getContainer()).getSessionTimeOut() * 60);
        session.setNew(true);
        session.setValid(true);
        session.setId(sessionId);
        return session;
    }

    private String generateSessionId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @Override
    public Session findSession(String id) {
        if (id == null)
            return null;
        return sessionMap.get(id);
    }

    @Override
    public Session[] findSessions() {
        return sessionMap.values().toArray(new Session[0]);
    }

    @Override
    public void remove(Session session) {
        sessionMap.remove(session.getId());
    }

    @Override
    public void backgroundProcess() {
        // 每执行六次此方法就执行一次session清理
        count = (count++) % processExpiresFrequency;
        if (count == 0) {
            sessionMap.forEach((id,session) -> {
                session.isValid();
            });
        }
    }

    @Override
    public void initInternal() {
        // TODO
    }

    @Override
    public void startInternal() {
        // 加载序列化的session
        // ...
        Thread thread = new Thread(new BackThread(),"sessionCleaner");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stopInternal() {
        threadDone = true;
        // 序列化当前sessionMap所有session
    }

    @Override
    public void destroyInternal() {
        // TODO
    }

    public class BackThread implements Runnable {

        @Override
        public void run() {
            while (!threadDone) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                backgroundProcess();
            }
        }
    }

}
