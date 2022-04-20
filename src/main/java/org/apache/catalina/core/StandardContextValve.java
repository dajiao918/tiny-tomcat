package org.apache.catalina.core;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.coyote.HttpStatus;
import org.apache.catalina.coyote.Request;
import org.apache.catalina.coyote.RequestFacade;
import org.apache.catalina.coyote.Response;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2022-04-13 10:16
 **/
public class StandardContextValve extends ValveBase{

    @Override
    public void invoke(Request request, Response response) {
        String uri = request.getUri();
        // 外部不能访问/META-INF/ 和 /WEB-INF/ 下的资源
        if ((uri.startsWith("/META-INF/", 0))
                || (uri.equalsIgnoreCase("/META-INF"))
                || (uri.startsWith("/WEB-INF/", 0))
                || (uri.equalsIgnoreCase("/WEB-INF"))) {
            String error = "You don't have permission to access "+uri+" on this server.";
            response.sendError(HttpStatus.FORBIDDEN, error);
            return;
        }

        Context context = (Context) container;
        // 调用request的listener
        List<Object> eventListeners = context.getEventListeners();
        ServletRequestEvent event = new ServletRequestEvent(context.getServletContext(), new RequestFacade(request));
        for (Object eventListener : eventListeners) {
            if (eventListener instanceof ServletRequestListener) {
                ServletRequestListener requestListener = (ServletRequestListener) eventListener;
                try {
                    requestListener.requestInitialized(event);
                } catch (Exception e) {
                    response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            }
        }


        Wrapper wrapper = request.getWrapper();
        if (wrapper == null) {
            throw new RuntimeException();
        }

        wrapper.getPipeline().getFirst().invoke(request, response);

        for (Object eventListener : eventListeners) {
            if (eventListener instanceof ServletRequestListener) {
                ServletRequestListener requestListener = (ServletRequestListener) eventListener;
                try {
                    requestListener.requestDestroyed(event);
                } catch (Exception e) {
                    response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            }
        }
    }

}
