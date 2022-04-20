package org.apache.catalina.core;

import org.apache.catalina.Wrapper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * @author: Mr.Yu
 * @create: 2022-04-10 22:29
 **/
public class StandardWrapperFacade implements ServletConfig {

    private final ServletConfig config;

    public StandardWrapperFacade(ServletConfig config) {
        this.config = config;
    }

    @Override
    public String getServletName() {
        return config.getServletName();
    }

    @Override
    public ServletContext getServletContext() {
        return config.getServletContext();
    }

    @Override
    public String getInitParameter(String name) {
        return config.getInitParameter(name);
    }

    @Override
    public Enumeration getInitParameterNames() {
        return config.getInitParameterNames();
    }
}
