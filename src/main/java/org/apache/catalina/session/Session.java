package org.apache.catalina.session;

import org.apache.catalina.Manager;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Iterator;

/**
 * @author: Mr.Yu
 * @create: 2022-04-19 11:22
 **/
public interface Session {

    public static final String SESSION_CREATED_EVENT = "createSession";

    public static final String SESSION_DESTROYED_EVENT = "destroySession";

    public static final String SESSION_ACTIVATED_EVENT = "activateSession";

    public static final String SESSION_PASSIVATED_EVENT = "passivateSession";

    public long getCreationTime();

    public void setCreationTime(long time);

    public String getId();

    public void setId(String id);

    public void setId(String id, boolean notify);

    public String getInfo();

    public long getLastAccessedTime();

    public Manager getManager();

    public void setManager(Manager manager);

    public int getMaxInactiveInterval();

    public void setMaxInactiveInterval(int interval);

    public void setNew(boolean isNew);

    public HttpSession getSession();

    public void setValid(boolean isValid);

    public boolean isValid();

    public void access();

    public void endAccess();

    public void expire();
}