package org.apache.catalina.core;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.coyote.RequestFacade;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author: Mr.Yu
 * @create: 2022-04-18 09:15
 **/
public class ApplicationRequestDispatcher implements RequestDispatcher {

    private final String uri;
    private final Wrapper wrapper;
    private final String queryString;
    private final String servletPath;

    public ApplicationRequestDispatcher(String uri, Wrapper wrapper, String queryString) {
        this.uri = uri;
        this.wrapper = wrapper;
        this.queryString = queryString;
        this.servletPath = uri;
    }

    /*
     * forward：请求转发之后不能在使用response进行数据写出
     * */
    @Override
    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        Servlet servlet = null;
        try {
            servlet = wrapper.allocate();
        } catch (ServletException e) {
            throw new ServletException(e);
        }
        if (servlet != null) {
            ApplicationFilterFactory filterFactory =
                    ApplicationFilterFactory.getInstance();
            Context context = (Context) wrapper.getParent();
            String contextPath = context.getPath();
            ApplicationFilterChain filterChain =
                    filterFactory.createApplicationFilterChain(contextPath+uri, servlet, wrapper);
            RequestFacade requestFacade =
                    getRequestFacade(request);
            if (requestFacade == null) {
                throw new ServletException("internal server error");
            }
            ForwardRequestWrapper requestWrapper =
                    new ForwardRequestWrapper(requestFacade, uri, queryString, servletPath);
            try {
                filterChain.doFilter(requestWrapper, response);
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                throw new ServletException(e);
            }
        }
        if (servlet != null) {
            try {
                wrapper.deallocate(servlet);
                wrapper.unload();
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
        // question：使得输入流也被关闭正确与否?
        response.getOutputStream().close();
    }
    // 获取request内部的RequestFacade（在请求转发多次的时候会出现这种情况）
    private RequestFacade getRequestFacade(ServletRequest request) {
        if (request instanceof RequestFacade) {
            return (RequestFacade) request;
        }
        if (request instanceof ForwardRequestWrapper) {
            return ((ForwardRequestWrapper)request).getRequest();
        }
        return null;
    }

    @Override
    public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {

    }
}
