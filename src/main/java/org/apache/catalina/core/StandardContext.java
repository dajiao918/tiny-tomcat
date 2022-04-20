package org.apache.catalina.core;

import com.sun.applet2.AppletParameters;
import com.sun.deploy.net.MessageHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.*;
import org.apache.catalina.deploy.ApplicationListener;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.util.Mapper;
import org.apache.catalina.util.WebClassLoader;

import javax.servlet.*;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Mr.Yu
 * @create: 2022-04-08 21:04
 **/
@Slf4j
public class StandardContext extends ContainerBase implements Context {

    private static final String info = "org.apache.catalina.core.StandardContext/1.0";

    private final Map<String, String> parameters = new ConcurrentHashMap<>();

    private final Map<String, FilterDef> filterDefMap = new HashMap<>();

    private String displayName = null;

    // web项目的文件名
    private String docBase = null;

    // 相当于 /docBase
    private String path = null;

    // 是否允许别的context访问
    private boolean crossContext = false;

    private ApplicationContext context;

    // context里面的映射器
    private final Mapper mapper = new Mapper();
    // session管理器
    private final Manager manager = new StandardManager(this);

    private final Map<String,String> servletMappings = new HashMap<>();
    private final List<String> welcomeFiles = new ArrayList<>();
    private final List<FilterMap> filterMaps = new ArrayList<>();
    private final List<ApplicationListener> applicationListeners = new ArrayList<>();

    private WebClassLoader classLoader;
    private List<Object> eventListeners = new ArrayList<>(2);
    private List<Object> lifecycleListeners = new ArrayList<>(1);
    private final Map<String, ApplicationFilterConfig> filterConfigMap = new HashMap<>();
    // session过期时间，单位min
    private int sessionTimeOut = 30;

    public StandardContext() {
        super();
        pipeline.setBasic(new StandardContextValve());
    }

    @Override
    public void removeServletMapping(String pattern) {

    }

    @Override
    public void removeWelcomeFile(String name) {

    }

    @Override
    public String getBaseName() {
        return docBase;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    @Override
    public boolean getCrossContext() {
        return crossContext;
    }

    @Override
    public void setCrossContext(boolean crossContext) {
        this.crossContext = crossContext;
    }

    @Override
    public ServletContext getServletContext() {
        if (context == null) {
            context = new ApplicationContext(this);
        }
        return context;
    }

    public Mapper getMapper() {
        return mapper;
    }

    @Override
    public void addParameter(String key, String value) {
        String absent = parameters.putIfAbsent(key, value);
        if (absent != null) {
            throw new RuntimeException("context parameter name duplicate " + name);
        }
    }

    public String getInitParameter(String name) {
        return parameters.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        parameters.put(name, value);
        return true;
    }

    @Override
    public int getSessionTimeOut() {
        return sessionTimeOut;
    }

    public void setSessionTimeOut(String sessionTimeOut) {
        int timeout = this.sessionTimeOut;
        try {
            timeout = Integer.parseInt(sessionTimeOut);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        this.sessionTimeOut = timeout;
    }

    public void setSessionTimeOut(int sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

    public Manager getManager() {
        return this.manager;
    }

    @Override
    public List<String> getWelcomeFiles() {
        return welcomeFiles;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public void addFilterDef(FilterDef filter) {
        filterDefMap.put(filter.getFilterName(), filter);
    }

    @Override
    public void addFilterMap(FilterMap filterMap) {
        filterMaps.add(filterMap);
    }

    @Override
    public ApplicationFilterConfig findFilterConfig(String filterName) {
        return filterConfigMap.get(filterName);
    }

    @Override
    public List<FilterMap> findFilterMap() {
        return filterMaps;
    }

    @Override
    public void addListener(String listener) {
        ApplicationListener applicationListener = new ApplicationListener(listener);
        applicationListeners.add(applicationListener);
    }

    public void setEventListeners(List<Object> eventListeners) {
        this.eventListeners = eventListeners;
    }

    public List<Object> getEventListeners() {
        return this.eventListeners;
    }

    public void setLifecycleListeners(List<Object> lifecycleListeners) {
        this.lifecycleListeners = lifecycleListeners;
    }

    public List<Object> getLifecycleListeners() {
        return this.lifecycleListeners;
    }

    @Override
    public Wrapper createWrapper() {
        return new StandardWrapper();
    }

    @Override
    public void addServletMapping(String urlPattern, String servletName) {
        if (findChild(servletName) == null) {
            throw new RuntimeException("Context has not a servlet with name " + servletName);
        }
        String name = servletMappings.get(urlPattern);
        if (name != null) {
            // 不允许多个servlet 对应一个路径
            Wrapper wrapper = (Wrapper) findChild(name);
            wrapper.removeMapping(urlPattern);
            mapper.removeWrapper(urlPattern);
            servletMappings.put(urlPattern, servletName);
        }
        Wrapper wrapper = (Wrapper) findChild(servletName);
        // 但可以允许一个servlet对应多个路径
        wrapper.addMapping(urlPattern);
        mapper.addWrapper(urlPattern, wrapper);
    }

    @Override
    public void addWelcomeFile(String welcomeFile) {
        welcomeFiles.add(welcomeFile);
    }

    @Override
    public WebClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void startInternal() {

        // 创建webappLoader
        classLoader = new WebClassLoader(this);
        ((Lifecycle) classLoader).start();

        // 启动contextConfig监听器，加载web.xml
        fireLifecycleEvent(Lifecycle.CONFIGURE_START_EVENT, null);

        for (Container child : findChildren()) {
            if (!child.getState().isAvailable()) {
                child.start();
            }
        }

        if (pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).start();
        }

        mapper.setContext(path, this);

        if (manager instanceof Lifecycle) {
            ((Lifecycle) manager).start();
        }

        // 初始化监听器
        if (!listenerStart()) {
            throw new RuntimeException("init listener failed, please check web.xml or ServletContextListener");
        }
        // 初始化过滤器
        if (!filterStart()) {
            throw new RuntimeException("init filter failed, please check web.xml or your filter init method");
        }
        // 初始化被要求初始化的servlet
        if (!loadOnStartup(findChildren())) {
            throw new RuntimeException("init servlet on start up failed");
        }
    }

    private boolean filterStart() {
        filterConfigMap.clear();
        boolean ok = true;
        for (Map.Entry<String, FilterDef> entry : filterDefMap.entrySet()) {
            String name = entry.getKey();
            ApplicationFilterConfig filterConfig = null;
            try {
                filterConfig = new ApplicationFilterConfig(this, entry.getValue());
                filterConfigMap.put(name,filterConfig);
            } catch (Exception e) {
                e.printStackTrace();
                ok = false;
            }
        }
        return ok;
    }

    private boolean listenerStart() {
        ClassLoader classLoader = this.classLoader.getClassLoader();
        List<Object> objects = new ArrayList<>();
        for (ApplicationListener applicationListener : applicationListeners) {
            try {
                Object obj = classLoader.loadClass(applicationListener.getClassName()).newInstance();
                objects.add(obj);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                log.error("init listener failed listener class " + applicationListener.getClassName());
                return false;
            }
        }

        // 事件类型监听器
        List<Object> eventListeners = new ArrayList<>();
        // 生命类型监听器
        List<Object> lifecycleListeners = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof ServletContextAttributeListener ||
                object instanceof ServletRequestAttributeListener ||
                object instanceof ServletRequestListener || // tomcat把这个也视为事件类型的监听器
                object instanceof HttpSessionAttributeListener) {
                eventListeners.add(object);
            } else if (object instanceof ServletContextListener ||
                        object instanceof HttpSessionListener) {
                lifecycleListeners.add(object);
            }
        }
        eventListeners.addAll(getEventListeners());
        setEventListeners(eventListeners);
        lifecycleListeners.addAll(getLifecycleListeners());
        setLifecycleListeners(lifecycleListeners);
        // 确保context被初始化
        ServletContextEvent event = new ServletContextEvent(getServletContext());
        for (Object lifecycleListener : lifecycleListeners) {
            if (lifecycleListener instanceof ServletContextListener) {
                try {
                    // 调用ServletContextListener的初始化方法
                    ((ServletContextListener) lifecycleListener).contextInitialized(event);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }


    private boolean loadOnStartup(Container[] children) {
        // 根据wrapper的loadOnStartup排序，值越小的约先启动，这里使用红黑树
        TreeMap<Integer, List<Wrapper>> map = new TreeMap<>();
        for (Container child : children) {
            Wrapper wrapper = (Wrapper) child;
            // 小于0不予考虑
            if (wrapper.getLoadOnStartup() < 0) {
                continue;
            }
            int up = wrapper.getLoadOnStartup();
            List<Wrapper> wrappers = map.get(up);
            // 如果值相等以web.xml的先后顺序为加载顺序
            if (wrappers == null) {
                wrappers = new ArrayList<>();
                map.put(up, wrappers);
            }
            wrappers.add(wrapper);
        }
        for (List<Wrapper> wrappers : map.values()) {
            for (Wrapper wrapper : wrappers) {
                try {
                    wrapper.load();
                } catch (ServletException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }
}
