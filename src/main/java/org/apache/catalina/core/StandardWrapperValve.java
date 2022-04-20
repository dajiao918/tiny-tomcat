package org.apache.catalina.core;

import org.apache.catalina.Wrapper;
import org.apache.catalina.coyote.*;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author: Mr.Yu
 * @create: 2022-04-13 10:18
 **/
public class StandardWrapperValve extends ValveBase{

    @Override
    public void invoke(Request request, Response response) {
        Wrapper wrapper = request.getWrapper();
        Servlet servlet = null;
        try {
            // 申请一个servlet
            servlet = wrapper.allocate();
        } catch (ServletException e) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
        // 创建外观类
        RequestFacade requestFacade = new RequestFacade(request);
        ResponseFacade responseFacade = new ResponseFacade(response);
        requestFacade.setResponseFacade(responseFacade);
        responseFacade.setRequest(requestFacade);
        ApplicationFilterFactory factory = ApplicationFilterFactory.getInstance();
        // 创建过滤器链
        ApplicationFilterChain filterChain =
                factory.createApplicationFilterChain(request.getUri(), servlet, wrapper);
        if (filterChain != null) {
            try {
                // 请求开始执行
                filterChain.doFilter(requestFacade, responseFacade);
            } catch (IOException | ServletException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                return;
            }
        }
        try {
            if (servlet != null) {
                // 回收servlet
                wrapper.deallocate(servlet);
                // 调用servlet.destroy()
                wrapper.unload();
            }
        } catch (Exception e) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


}
