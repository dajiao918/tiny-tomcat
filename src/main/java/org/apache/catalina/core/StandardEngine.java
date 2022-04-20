package org.apache.catalina.core;

import org.apache.catalina.Engine;
import org.apache.catalina.Service;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 11:13
 **/
public class StandardEngine extends ContainerBase implements Engine {

    private static final String info = "org.apache.catalina.core.StandardEngine/1.0";

    private Service service;

    private String defaultHost = null;

    public StandardEngine() {
        super();
        pipeline.setBasic(new standardEngineValve());
    }

    @Override
    public String getDefaultHost() {
        return defaultHost;
    }

    @Override
    public void setDefaultHost(String defaultHost) {
        this.defaultHost = defaultHost;
    }

    @Override
    public void startInternal() {
        setState(LifecycleState.STARTING, null);
        super.startInternal();
    }

    @Override
    public Service getService() {
        return service;
    }

    @Override
    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public String getInfo() {
        return info;
    }
}
