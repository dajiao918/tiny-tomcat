package org.apache.catalina.session;

import org.apache.catalina.Context;
import org.apache.catalina.Manager;

import javax.servlet.ServletContext;
import javax.servlet.http.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Mr.Yu
 * @create: 2022-04-19 11:19
 **/
public class StandardSession implements HttpSession,Session, Serializable {

    private static final long serialVersionUID = 1L;

    private static final String info = "StandardSession";
    // 忽略请求时间
    private static final boolean IGNORE_REQUEST_TIME = true;

    private transient Manager manager;

    private long createTime;

    private long lastAccessedTime;

    private String id;

    private int maxInactiveInterval;

    private transient HttpSession facade;

    private boolean isNew = false;

    private boolean isValid = false;

    private final Map<String,Object> attributes = new ConcurrentHashMap<>();

    public StandardSession(Manager manager) {
        this.manager = manager;
    }

    @Override
    public long getCreationTime() {
        return createTime;
    }

    @Override
    public void setCreationTime(long time) {
        createTime = time;
        lastAccessedTime = time;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        setId(id, true);
    }

    @Override
    public void setId(String id, boolean notify) {
        if (id == null)
            return;
        if (manager != null && this.id != null) {
            manager.remove(this);
        }
        this.id = id;
        if (manager != null)
            manager.add(this);
        if (notify) {
            tellNew();
        }
    }

    // 回调session创建监听器的方法
    private void tellNew() {
        Context context = (Context) manager.getContainer();
        if (context == null)
            return;
        List<Object> lifecycleListeners = context.getLifecycleListeners();
        if (lifecycleListeners.size() == 0)
            return;
        HttpSessionEvent event = new HttpSessionEvent(getSession());
        for (Object listener : lifecycleListeners) {
            try {
                if (listener instanceof HttpSessionListener) {
                    ((HttpSessionListener)listener).sessionCreated(event);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getInfo() {
        return info;
    }


    /**
    * 上一次访问session的时间，session过期时间就是根据 现在毫秒数-lastAccessedTime > 存活时间
    * */
    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public Manager getManager() {
        return manager;
    }

    @Override
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Override
    public ServletContext getServletContext() {
        return ((Context)manager.getContainer()).getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public HttpSession getSession() {
        if (facade == null) {
            facade = new StandardSessionFacade(this);
        }
        return facade;
    }

    @Override
    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    @Override
    public boolean isValid() {
        if (!isValid) {
            return false;
        }

        if (maxInactiveInterval > 0) {
            long now = System.currentTimeMillis();
            int idleTime = (int) ((now - lastAccessedTime) / 1000L);
            if (idleTime >= maxInactiveInterval) {
                expire();
            }
        }
        return isValid;
    }

    @Override
    public void access() {
        lastAccessedTime = System.currentTimeMillis();
    }

    @Override
    public void endAccess() {
        isNew = false;

        if(IGNORE_REQUEST_TIME) {
            lastAccessedTime = System.currentTimeMillis();
        }
    }

    @Override
    public void expire() {

        Context context = (Context) manager.getContainer();
        List<Object> listeners = context.getLifecycleListeners();

        if (listeners.size() > 0) {
            HttpSessionEvent event = new HttpSessionEvent(getSession());
            for (Object listener : listeners) {
                try {
                    if (listener instanceof HttpSessionListener) {
                        ((HttpSessionListener) listener).sessionDestroyed(event);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        manager.remove(this);

        setValid(false);
        String[] keys = keys();
        for (int i = 0; i < keys.length; i++) {
            removeAttribute(keys[i]);
        }
    }

    private String[] keys() {
        return attributes.keySet().toArray(new String[0]);
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        if (name == null)
            return null;
        return attributes.get(name);
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public String[] getValueNames() {
        return attributes.keySet().toArray(new String[0]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (name == null) {
            return;
        }
        boolean replaced = attributes.containsKey(name);
        HttpSessionBindingEvent event = null;
        Object oldValue = attributes.put(name, value);
        if (value instanceof HttpSessionBindingListener ) {
            event = new HttpSessionBindingEvent(getSession(), name,value);
            ((HttpSessionBindingListener)value).valueBound(event);
        }
        if (oldValue != value && oldValue instanceof HttpSessionBindingListener) {
            event = new HttpSessionBindingEvent(getSession(), name,oldValue);
            ((HttpSessionBindingListener)oldValue).valueUnbound(event);
        }
        event = null;
        Context context = (Context) manager.getContainer();
        List<Object> listeners = context.getEventListeners();
        if (listeners.size() > 0) {
            for (Object listener : listeners) {
                if (listener instanceof HttpSessionAttributeListener) {
                    if (replaced) {
                        if (event == null) {
                            event = new HttpSessionBindingEvent(getSession(), name, oldValue);
                        }
                        ((HttpSessionAttributeListener) listener).attributeReplaced(event);
                    } else {
                        if (event == null) {
                            event = new HttpSessionBindingEvent(getSession(), name, value);
                        }
                        ((HttpSessionAttributeListener) listener).attributeAdded(event);
                    }
                }
            }
        }
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        if (name == null || !attributes.containsKey(name)) {
            return;
        }

        Object value = attributes.remove(name);
        HttpSessionBindingEvent event = null;
        if (value instanceof HttpSessionBindingListener) {
            event = new HttpSessionBindingEvent(getSession(), name, value);
            try {
                ((HttpSessionBindingListener)value).valueUnbound(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Context context = (Context) manager.getContainer();
        List<Object> listeners = context.getEventListeners();
        if (listeners.size() > 1) {
            try {
                for (Object listener : listeners) {
                    if (listener instanceof HttpSessionAttributeListener) {
                        if (event == null) {
                            event = new HttpSessionBindingEvent(getSession(), name, value);
                        }
                        ((HttpSessionAttributeListener) listener).attributeRemoved(event);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        expire();
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
