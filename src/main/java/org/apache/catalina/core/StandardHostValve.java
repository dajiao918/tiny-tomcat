package org.apache.catalina.core;

import org.apache.catalina.Context;
import org.apache.catalina.Valve;
import org.apache.catalina.coyote.Request;
import org.apache.catalina.coyote.Response;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 16:05
 **/
public class StandardHostValve extends ValveBase {
    @Override
    public void invoke(Request request, Response response) {
        Context context = request.getContext();
        context.getPipeline().getFirst().invoke(request, response);

    }
}
