package org.apache.catalina.core;

import org.apache.catalina.deploy.FilterDef;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * @author: Mr.Yu
 * @create: 2022-04-11 10:49
 **/
public class ApplicationFilterConfig implements FilterConfig {

    private StandardContext context;

    private FilterDef filterDef;

    private Filter filter;

    public ApplicationFilterConfig(StandardContext context, FilterDef filterDef) throws ClassNotFoundException, InstantiationException, ServletException, IllegalAccessException {
        this.context = context;
        this.filterDef = filterDef;
        getFilter();
    }

    Filter getFilter() throws ClassNotFoundException, IllegalAccessException, InstantiationException, ServletException {
        if (filter != null)
            return filter;
        ClassLoader classLoader = context.getClassLoader().getClassLoader();
        String filterClass = filterDef.getFilterClass();
        filter = (Filter) classLoader.loadClass(filterClass).newInstance();
        filter.init(this);
        return filter;
    }

    @Override
    public String getFilterName() {
        return filterDef.getFilterName();
    }

    @Override
    public ServletContext getServletContext() {
        return context.getServletContext();
    }

    @Override
    public String getInitParameter(String name) {
        return filterDef.getParameterMap().get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(filterDef.getParameterMap().keySet());
    }

    void release() {
        filter.destroy();
    }

}
