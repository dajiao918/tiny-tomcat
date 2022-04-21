package org.apache.catalina.core;

import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 09:54
 **/
public class StandardService extends LifecycleBase implements Service {

    private String info = "org.apache.catalina.core.StandardService/1.0";

    private Container container;

    private String name;

    private Server server;

    private Connector[] connectors = new Connector[0];

    private ClassLoader parentClassLoader;

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        Container oldContainer = this.container;
        if (oldContainer instanceof Engine) {
            ((Engine) oldContainer).setService(null);
        }

        if (container instanceof Engine) {
            ((Engine) container).setService(this);
        }

        this.container = container;
        if (container != null && getState().isAvailable()) {
            container.start();
        }
        if (oldContainer != null) {
            oldContainer.stop();
        }
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public ClassLoader getParentClassLoader() {
        if (parentClassLoader != null)
            return  parentClassLoader;
        parentClassLoader = getServer().getParentClassLoader();
        return parentClassLoader;
    }

    @Override
    public void setParentClassLoader(ClassLoader parent) {
        this.parentClassLoader = parent;
    }

    @Override
    public void addConnector(Connector connector) {

        connector.setService(this);

        Connector[] newConnectors = new Connector[connectors.length + 1];
        System.arraycopy(connectors, 0, newConnectors, 0, connectors.length);
        newConnectors[connectors.length] = connector;
        connectors = newConnectors;
        if (getState().isAvailable()) {
            connector.start();
        }
    }

    @Override
    public Connector[] findConnectors() {
        return connectors;
    }

    @Override
    public void removeConnector(Connector connector) {
        // TODO
    }

    @Override
    public void initInternal() {

        if (container != null) {
            container.init();
        }

        for (Connector connector : connectors) {
            connector.init();
        }
    }

    @Override
    public void startInternal() {

        if (container != null) {
            container.start();
        }

        for (Connector connector : connectors) {
            connector.start();
        }
    }

    @Override
    public void stopInternal() {

        // 首先停止connector接收socket，以及处理事件
        for (Connector connector : connectors) {
            connector.pause();
        }

        if (container != null) {
            container.stop();
        }

        for (Connector connector : connectors) {
            connector.stop();
        }
    }

    @Override
    public void destroyInternal() {
        // TODO
    }
}
