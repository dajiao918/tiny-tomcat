package org.apache.catalina.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.coyote.HttpStatus;
import org.apache.catalina.coyote.Response;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author: Mr.Yu
 * @create: 2022-04-20 15:27
 **/
@Slf4j
public class ErrorHelper {

    public static void logErrorToSocket(Exception e, Response response) {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        e.printStackTrace(writer);
        log.error("异常信息：\n"+sw.toString());
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, sw.toString());
    }

}
