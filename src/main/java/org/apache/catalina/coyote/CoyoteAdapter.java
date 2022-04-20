package org.apache.catalina.coyote;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.util.ErrorHelper;
import org.apache.catalina.util.Mapper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author: Mr.Yu
 * @create: 2022-04-12 09:06
 **/
public class CoyoteAdapter implements Adapter{

    private final Connector connector;

    public CoyoteAdapter(Connector connector) {
        this.connector = connector;
    }

    private Connector getConnector() {
        return connector;
    }

    @Override
    public void service(Request request, Response response) {

        try {
            Map<String, String> headers = request.getHeaders();
            String hostName = headers.get("Host");
            if (hostName == null) {
                String error = "bad request, no host header";
                response.sendError(HttpStatus.BAD_REQUEST,error);
                return;
            }
            int i = hostName.indexOf(":");
            if (i > -1) {
                hostName = hostName.substring(0,i);
            }
            Mapper mapper = connector.getMapper();
            Host realHost = (Host) mapper.mappingHost(hostName);
            // 出现这种情况一般就是server.xml的问题
            if (realHost == null) {
                String error = "server internal error";
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR,error);
                return;
            }
            request.setHost(realHost);
            // 获取context的path  /testTomcat/hello -> /testTomcat
            String uri = request.getUri();
            int index = uri.indexOf("/",1);
            if (index == -1) {
                String error = "bad request uri " + uri;
                response.sendError(HttpStatus.BAD_REQUEST,error);
                return;
            }
            String contextPath = uri.substring(0,index);
            Context context = (Context) mapper.mappingContext(hostName,contextPath);
            if (context == null) {
                String error = "not found context " + contextPath;
                response.sendError(HttpStatus.NOT_FOUND,error);
                return;
            }
            String wrapperMapping = uri.substring(index);
            Wrapper wrapper = (Wrapper) mapper.mappingWrapper(hostName, contextPath, wrapperMapping);
            if (wrapper == null) {
                String error = "bad request uri " + uri;
                response.sendError(HttpStatus.NOT_FOUND,error);
                return;
            }
            request.setContext(context);
            request.setWrapper(wrapper);
            getConnector().getService().getContainer().getPipeline().getFirst().invoke(request, response);
        } catch (Exception e) {
            ErrorHelper.logErrorToSocket(e,response);
        } finally {
            // 刷新缓冲区
            response.finishResponse();
        }
    }

}
