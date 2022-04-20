package org.apache.catalina.coyote;

/**
 * @author: Mr.Yu
 * @create: 2022-04-01 21:22
 **/
public interface Adapter {

    void service(Request request, Response response);

}