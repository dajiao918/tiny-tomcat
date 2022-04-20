package org.apache.catalina.core;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Valve;
import org.apache.catalina.coyote.HttpStatus;
import org.apache.catalina.coyote.Request;
import org.apache.catalina.coyote.Response;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 15:26
 **/
public abstract class ValveBase extends LifecycleBase implements Valve, Contained {

    protected Valve next = null;

    protected static final String info =
            "org.apache.catalina.core.ValveBase/1.0";

    protected Container container;

    @Override
    public Valve getNext() {
        return next;
    }

    @Override
    public void setNext(Valve valve) {
        next = valve;
    }

    public ValveBase() {
        super();
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void backgroundProcess() {
        // TODO
    }

    @Override
    public abstract void invoke(Request request, Response response);

    @Override
    public void initInternal() {
        // TODO
    }

    @Override
    public void startInternal() {
        // TODO
    }

    @Override
    public void stopInternal() {
        // TODO
    }

    @Override
    public void destroyInternal() {
        // TODO
    }

    @Override
    public String getInfo() {
        return info;
    }
}
