package org.apache.catalina.core;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2022-04-15 23:17
 **/
public class ApplicationFilterChain implements FilterChain {

    private Servlet servlet = null;

    private final List<ApplicationFilterConfig> filters = new ArrayList<>();

    private int pos = 0;


    void addFilter(ApplicationFilterConfig filterConfig) {
        filters.add(filterConfig);
    }

    void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (pos < filters.size()) {

            ApplicationFilterConfig config = filters.get(pos);
            Filter filter = null;
            try {
                filter = config.getFilter();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw e;
            }
            pos ++;
            filter.doFilter(request, response,this );
        } else {
            try {
                servlet.service(request, response);
            } catch (ServletException | IOException e) {
                throw e;
            }
        }
    }
}
