package org.apache.catalina.core;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.WelComeResource;
import org.apache.catalina.Wrapper;
import org.apache.catalina.util.WebClassLoader;
import org.apache.catalina.util.WebappClassLoader;

import javax.servlet.*;
import java.util.*;

/**
 * @author: Mr.Yu
 * @create: 2022-04-10 10:56
 **/
public class StandardWrapper extends ContainerBase implements Wrapper, ServletConfig {

    private static final String info = "org.apache.catalina.core.StandardWrapper/1.0";

    private String servletClass = null;
    private int loadOnStartup = -1;
    private boolean enabled = false;
    private boolean asyncSupported = false;
    private Servlet servlet = null;

    private final List<String> mappings = new ArrayList<>();
    private final Map<String,String> parameterMap = new HashMap<>();

    private final StandardWrapperFacade facade = new StandardWrapperFacade(this);
    private boolean instanceInitialized = false;

    public StandardWrapper() {
        super();
        pipeline.setBasic(new StandardWrapperValve());
    }

    @Override
    public int getLoadOnStartup() {
        return loadOnStartup;
    }

    @Override
    public void setLoadOnStartup(int value) {
        this.loadOnStartup = value;
    }

    public void setLoadOnStartup(String value) {
        try {
            setLoadOnStartup(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getRunAs() {
        return null;
    }

    @Override
    public void setRunAs(String runAs) {

    }

    @Override
    public String getServletClass() {
        return servletClass;
    }

    @Override
    public void setServletClass(String servletClass) {
        this.servletClass = servletClass;
    }

    @Override
    public String[] getServletMethods() throws ServletException {
        return new String[0];
    }

    @Override
    public Servlet getServlet() {
        return servlet;
    }

    @Override
    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    @Override
    public void addInitParameter(String name, String value) {
        parameterMap.put(name, value);
    }

    @Override
    public void addMapping(String mapping) {
        mappings.add(mapping);
        fireContainerEvent(Wrapper.ADD_MAPPING_EVENT, mapping);
    }

    @Override
    public Servlet allocate() throws ServletException {
        if (servlet != null) {
            return servlet;
        }
        servlet = loadServlet();
        return servlet;
    }

    private Servlet loadServlet() throws ServletException {
        WebappClassLoader classLoader = (WebappClassLoader) getClassLoader();
        if (servletClass == null)
            throw new RuntimeException("servlet class is null, must check web.xml");
        Class<?> clazz = null;
        try {
            clazz = classLoader.loadClass(servletClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("not found servlet, class " + servletClass);
        }
        Servlet servlet = null;
        if (clazz != null) {
            try {
                servlet = (Servlet) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
                throw new RuntimeException(e);
            }
        }
        if (servlet == null) {
            throw new RuntimeException("servlet is null, class is " + servletClass);
        }
        // 调用servlet的初始化方法
        servlet.init(facade);
        instanceInitialized = true;
        return servlet;
    }

    private ClassLoader getClassLoader() {
        Container parent = getParent();
        WebClassLoader classLoader = ((Context) parent).getClassLoader();
        return classLoader.getClassLoader();
    }

    @Override
    public void deallocate(Servlet servlet) {
        // NOTHING
    }

    @Override
    public String findInitParameter(String name) {
        return parameterMap.get(name);
    }

    @Override
    public String[] findInitParameters() {
        return parameterMap.values().toArray(new String[0]);
    }

    @Override
    public String[] findMappings() {
        return mappings.toArray(new String[0]);
    }

    @Override
    public void load() throws ServletException {
        servlet = loadServlet();
        if ("org.apache.catalina.servlets.DefaultServlet".equals(servletClass)
                && servlet instanceof WelComeResource) {
            addWelComeToDefaultServlet();
        }
        if (!instanceInitialized) {
            servlet.init(facade);
        }
    }

    private void addWelComeToDefaultServlet() {
        Context context = (Context) getParent();
        List<String> welcomeFiles = context.getWelcomeFiles();
        welcomeFiles.forEach((filename) -> {
            ((WelComeResource) servlet).addWelComeFile(filename);
        });
    }

    @Override
    public void removeInitParameter(String name) {
        parameterMap.remove(name);
    }

    @Override
    public void removeMapping(String mapping) {
        mappings.remove(mapping);
        fireContainerEvent(Wrapper.REMOVE_MAPPING_EVENT, mapping);
    }

    @Override
    public boolean isAsyncSupported() {
        return asyncSupported;
    }

    @Override
    public void setAsyncSupported(boolean asyncSupport) {
        this.asyncSupported = asyncSupport;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void unload() {

        if (servlet == null) {
            return;
        }

        try {
            servlet.destroy();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public String getServletName() {
        return name;
    }

    @Override
    public ServletContext getServletContext() {
        return ((Context)getParent()).getServletContext();
    }

    @Override
    public String getInitParameter(String name) {
        return parameterMap.get(name);
    }

    @Override
    public Enumeration getInitParameterNames() {
        Set<String> set = parameterMap.keySet();
        return Collections.enumeration(set);
    }
}
