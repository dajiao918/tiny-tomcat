package org.apache.catalina;

import org.apache.catalina.coyote.Request;
import org.apache.catalina.coyote.Response;

import java.io.IOException;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 16:56
 **/
public interface Valve {

    public String getInfo();

    public Valve getNext();

    public void setNext(Valve valve);

    public void backgroundProcess();

    public void invoke(Request request, Response response);

}
