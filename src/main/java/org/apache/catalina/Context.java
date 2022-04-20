package org.apache.catalina;

import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.deploy.ServletDef;
import org.apache.catalina.util.Mapper;
import org.apache.catalina.util.WebClassLoader;
import org.apache.catalina.util.WebappClassLoader;

import javax.servlet.ServletContext;
import java.net.URL;
import java.util.*;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 15:52
 **/
public interface Context extends Container{

    void removeServletMapping(String pattern);

    void removeWelcomeFile(String name);

    String getBaseName();

    void setPath(String s);

    String getPath();

    void setDocBase(String docBase);

    void addParameter(String key, String value);

    public String getInitParameter(String name);

    void setDisplayName(String displayName);

    void addFilterDef(FilterDef filter);

    void addFilterMap(FilterMap filterMap);

    List<FilterMap> findFilterMap();

    ApplicationFilterConfig findFilterConfig(String filterName);

    void addListener(String listener);

    void setEventListeners(List<Object> eventListeners);

    List<Object> getEventListeners();

    void setLifecycleListeners(List<Object> lifecycleListeners);

    List<Object> getLifecycleListeners();

    Wrapper createWrapper();

    void addServletMapping(String key, String value);

    void addWelcomeFile(String welcomeFile);

    WebClassLoader getClassLoader();

    boolean getCrossContext();

    void setCrossContext(boolean crossContext);

    ServletContext getServletContext();

    Mapper getMapper();

    Enumeration<String> getInitParameterNames();

    boolean setInitParameter(String name, String value);

    int getSessionTimeOut();

    public void setSessionTimeOut(String sessionTimeOut);

    public void setSessionTimeOut(int sessionTimeOut);

    Manager getManager();

    List<String> getWelcomeFiles();
}