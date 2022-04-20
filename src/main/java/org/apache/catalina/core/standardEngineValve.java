package org.apache.catalina.core;

import org.apache.catalina.Host;
import org.apache.catalina.Valve;
import org.apache.catalina.coyote.Request;
import org.apache.catalina.coyote.Response;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 15:59
 **/
public class standardEngineValve extends ValveBase {

    private static final String info =
            "org.apache.catalina.core.standardEngineValve/1.0";

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void invoke(Request request, Response response) {
        Host host = request.getHost();
        host.getPipeline().getFirst().invoke(request, response);
    }
}
