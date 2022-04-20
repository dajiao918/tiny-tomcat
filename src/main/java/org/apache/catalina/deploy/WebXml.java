package org.apache.catalina.deploy;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;

import java.util.*;

/**
 * @author: Mr.Yu
 * @create: 2022-04-08 21:28
 **/
public class WebXml {

    private String description = null;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String displayName = null;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private Map<String, String> contextParams = new HashMap<String, String>();

    public void addContextParam(String param, String value) {
        contextParams.put(param, value);
    }

    public Map<String, String> getContextParams() {
        return contextParams;
    }

    private Map<String, FilterDef> filters =
            new LinkedHashMap<String, FilterDef>();

    public void addFilter(FilterDef filter) {
        if (filters.containsKey(filter.getFilterName())) {
            // Filter names must be unique within a web(-fragment).xml
            throw new RuntimeException("filter name duplicate in web.xml");
        }
        filters.put(filter.getFilterName(), filter);
    }

    public Map<String, FilterDef> getFilters() {
        return filters;
    }

    private Set<FilterMap> filterMaps = new LinkedHashSet<FilterMap>();
    private Set<String> filterMappingNames = new HashSet<String>();

    public void addFilterMapping(FilterMap filterMap) {
        filterMaps.add(filterMap);
        filterMappingNames.add(filterMap.getFilterName());
    }

    public Set<FilterMap> getFilterMappings() {
        return filterMaps;
    }

    private Set<String> listeners = new LinkedHashSet<String>();

    public void addListener(String className) {
        listeners.add(className);
    }

    public Set<String> getListeners() {
        return listeners;
    }


    private Map<String, ServletDef> servlets = new HashMap<String, ServletDef>();

    public void addServlet(ServletDef servletDef) {
        servlets.put(servletDef.getServletName(), servletDef);
    }

    public Map<String, ServletDef> getServlets() {
        return servlets;
    }


    private Map<String, String> servletMappings = new HashMap<String, String>();
    private Set<String> servletMappingNames = new HashSet<String>();

    public void addServletMapping(String urlPattern, String servletName) {
        String oldServletName = servletMappings.put(urlPattern, servletName);
        if (oldServletName != null) {
            throw new RuntimeException("servletName duplicate in web.xml");
        }
        servletMappingNames.add(servletName);
    }

    public Map<String, String> getServletMappings() {
        return servletMappings;
    }

    private Set<String> welcomeFiles = new LinkedHashSet<String>();

    public void addWelcomeFile(String welcomeFile) {
        welcomeFiles.add(welcomeFile);
    }

    public Set<String> getWelcomeFiles() {
        return welcomeFiles;
    }

    private SessionConfig sessionConfig = new SessionConfig();

    public void setSessionConfig(SessionConfig sessionConfig) {
        this.sessionConfig = sessionConfig;
    }

    public SessionConfig getSessionConfig() {
        return sessionConfig;
    }

    public void configureContext(Context context) {
        for (Map.Entry<String, String> entry : contextParams.entrySet()) {
            context.addParameter(entry.getKey(), entry.getValue());
        }
        context.setDisplayName(displayName);
        for (FilterDef filter : filters.values()) {
            if (filter.getAsyncSupported() == null) {
                filter.setAsyncSupported("false");
            }
            context.addFilterDef(filter);
        }

        for (FilterMap filterMap : filterMaps) {
            context.addFilterMap(filterMap);
        }
        for (String listener : listeners) {
            context.addListener(listener);
        }
        for (ServletDef servlet : servlets.values()) {
            Wrapper wrapper = context.createWrapper();
            if (servlet.getLoadOnStartup() != null) {
                wrapper.setLoadOnStartup(servlet.getLoadOnStartup());
            }
            if (servlet.getEnabled() != null) {
                wrapper.setEnabled(servlet.getEnabled());
            }
            wrapper.setName(servlet.getServletName());
            for (Map.Entry<String, String> entry : servlet.getParameterMap().entrySet()) {
                wrapper.addInitParameter(entry.getKey(), entry.getValue());
            }
            wrapper.setServletClass(servlet.getServletClass());
            if (servlet.getAsyncSupported() != null) {
                wrapper.setAsyncSupported(servlet.getAsyncSupported());
            }
            // 启动wrapper
            context.addChild(wrapper);
        }
        for (Map.Entry<String, String> entry : servletMappings.entrySet()) {
            context.addServletMapping(entry.getKey(), entry.getValue());
        }

        for (String welcomeFile : welcomeFiles) {
            if (welcomeFile != null && welcomeFile.length() > 0) {
                context.addWelcomeFile(welcomeFile);
            }
        }
    }

    public void merge(HashSet<WebXml> defaults) {
        for (WebXml webXml : defaults) {

            for (Map.Entry<String, String> entry : webXml.contextParams.entrySet()) {
                if (!this.contextParams.containsKey(entry.getKey())) {
                    this.contextParams.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<String, ServletDef> entry : webXml.servlets.entrySet()) {
                if (!this.servlets.containsKey(entry.getKey())) {
                    this.servlets.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<String, String> entry : webXml.servletMappings.entrySet()) {
                if (!this.servletMappings.containsKey(entry.getKey())) {
                    this.servletMappings.put(entry.getKey(), entry.getValue());
                }
            }

            this.listeners.addAll(webXml.getListeners());
            this.filterMaps.addAll(webXml.getFilterMappings());
            for (Map.Entry<String, FilterDef> entry : webXml.filters.entrySet()) {
                if (!this.filters.containsKey(entry.getKey())) {
                    this.filters.put(entry.getKey(), entry.getValue());
                }
            }

            this.welcomeFiles.addAll(webXml.welcomeFiles);
        }
    }
}
