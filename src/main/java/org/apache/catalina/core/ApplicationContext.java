package org.apache.catalina.core;

import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Wrapper;
import org.apache.catalina.util.Mapper;
import org.apache.catalina.util.MimeUtils;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Mr.Yu
 * @create: 2022-04-10 23:12
 **/
public class ApplicationContext implements ServletContext {

    private final StandardContext context;

    private final Map<String,Object> attributes = new ConcurrentHashMap<>();


    public ApplicationContext(StandardContext context) {
        this.context = context;
    }

    @Override
    public String getContextPath() {
        return context.getPath();
    }

    @Override
    public ServletContext getContext(String uripath) {
        if (uripath == null || !uripath.startsWith("/")) {
            return null;
        }
        Host host = (Host) context.getParent();
        Context child = (Context) host.findChild(uripath);
        if (child == null) {
            return null;
        }
        if (!child.getState().isAvailable()) {
            return null;
        }
        if (child.getCrossContext()) {
            return child.getServletContext();
        } else if (child == context) {
            return context.getServletContext();
        }
        return null;
    }

    @Override
    public int getMajorVersion() {
        return Globals.MAJOR_VERSION;
    }

    @Override
    public int getMinorVersion() {
        return Globals.MINOR_VERSION;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
    }

    @Override
    public String getMimeType(String file) {
        return MimeUtils.getMimeType(file);
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        return null;
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return null;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        if (path == null)
            return null;
        if (!path.startsWith("/"))
            return null;
        int index = path.indexOf("?");
        String queryString;
        String uri;
        if (index != -1) {
            queryString = path.substring(index+1);
            uri = path.substring(0,index);
        } else {
            uri = path;
            queryString = null;
        }

        Mapper mapper = context.getMapper();
        Wrapper wrapper = (Wrapper) mapper.mappingWrapper(uri);
        if (wrapper == null)
            return null;
        return new ApplicationRequestDispatcher(uri,wrapper,queryString);
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        return null;
    }

    @Override
    @Deprecated
    public Servlet getServlet(String name) throws ServletException {
        return null;
    }

    @Override
    @Deprecated
    public Enumeration<Servlet> getServlets() {
        return null;
    }

    @Override
    @Deprecated
    public Enumeration<String> getServletNames() {
        return null;
    }

    @Override
    public void log(String msg) {

    }

    @Override
    public void log(Exception exception, String msg) {

    }

    @Override
    public void log(String message, Throwable throwable) {

    }

    @Override
    @Deprecated
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public String getServerInfo() {
        return null;
    }

    @Override
    public String getInitParameter(String name) {
        return context.getInitParameter(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return context.getInitParameterNames();
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        if (name == null || name.equals("")) {
            return false;
        }
        return context.setInitParameter(name, value);
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
    public void setAttribute(String name, Object object) {
        if (name == null || name.equals("")) {
            return;
        }
        List<Object> eventListeners = context.getEventListeners();
        boolean replaced = attributes.containsKey(name);
        if (eventListeners.size() > 0) {
            ServletContextAttributeEvent event;
            if (replaced) {
                Object oldValue = attributes.get(name);
                event = new ServletContextAttributeEvent(this, name, oldValue);
            } else {
                event = new ServletContextAttributeEvent(this, name, object);
            }
            for (Object listener : eventListeners) {
                if (listener instanceof ServletContextAttributeListener) {
                    if (replaced) {
                        ((ServletContextAttributeListener) listener).attributeReplaced(event);
                    } else {
                        ((ServletContextAttributeListener) listener).attributeAdded(event);
                    }
                }
            }
        }
    }

    @Override
    public void removeAttribute(String name) {
        if (name == null || name.equals("") || !attributes.containsKey(name)) {
            return;
        }
        List<Object> eventListeners = context.getEventListeners();
        Object value = attributes.remove(name);
        if (eventListeners.size() > 0) {
            ServletContextAttributeEvent event =
                    new ServletContextAttributeEvent(this, name, value);
            for (Object listener : eventListeners) {
                if (listener instanceof ServletContextAttributeListener) {
                    ((ServletContextAttributeListener) listener).attributeRemoved(event);
                }
            }
        }
    }

    @Override
    public String getServletContextName() {
        return context.getPath();
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return null;
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return null;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        return null;
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {

    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;
    }

    @Override
    public void addListener(String className) {

    }

    @Override
    public <T extends EventListener> void addListener(T t) {

    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {

    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public void declareRoles(String... roleNames) {

    }
}
