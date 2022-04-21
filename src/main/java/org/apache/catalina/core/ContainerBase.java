package org.apache.catalina.core;

import org.apache.catalina.*;
import org.apache.catalina.connector.MapperListener;
import org.apache.catalina.core.LifecycleBase;
import org.apache.catalina.core.StandardPipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 11:14
 **/
public abstract class ContainerBase extends LifecycleBase implements Container {

    protected HashMap<String,Container> children = new HashMap<>();

    protected Container parent = null;

    protected ClassLoader parentClassLoader = null;

    protected Pipeline pipeline = new StandardPipeline(this);

    protected int startStopThreads = 1;

    protected String name;

    protected final List<ContainerListener> listeners = new ArrayList<>();

    public ContainerBase() {
        super();
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
    public Pipeline getPipeline() {
        return pipeline;
    }

    @Override
    public Container getParent() {
        return parent;
    }

    @Override
    public void setParent(Container container) {
        this.parent = container;
    }

    @Override
    public ClassLoader getParentClassLoader() {
        return parentClassLoader;
    }

    @Override
    public void setParentClassLoader(ClassLoader parent) {
        this.parentClassLoader = parent;
    }

    @Override
    public void addChild(Container child) {
        if (children.get(child.getName()) != null) {
            return;
        }
        child.setParent(this);
        children.put(child.getName(), child);

        if (getState().isAvailable()) {
            child.start();
        }
        fireContainerEvent(Container.ADD_CHILD_EVENT, child);
    }

    @Override
    public void addContainerListener(ContainerListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeContainerListener(ContainerListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void fireContainerEvent(String type, Object data) {
        if (listeners.size() < 1)
            return;
        ContainerEvent event = new ContainerEvent(this, type, data);
        for (ContainerListener listener : listeners) {
            listener.containerEvent(event);
        }
    }

    @Override
    public Container findChild(String name) {
        if (name != null) {
            return children.get(name);
        }
        return null;
    }

    @Override
    public Container[] findChildren() {
        ArrayList<Container> containers = new ArrayList<>(children.values());
        return containers.toArray(new Container[children.size()]);
    }

    @Override
    public void removeChild(Container child) {
        if (child.getName() != null) {
            Container container = children.remove(child.getName());
            container.stop();
        }
        fireContainerEvent(Container.REMOVE_CHILD_EVENT, child);
    }

    @Override
    public int getStartStopThreads() {
        return startStopThreads;
    }

    @Override
    public void initInternal() {
        Container[] children = findChildren();

        for (Container child : children) {
            child.init();
        }

        if (pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).init();
        }
    }

    @Override
    public void startInternal() {
        Container[] children = findChildren();

        for (Container child : children) {
            child.start();
        }

        if (pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).start();
        }
    }

    @Override
    public void stopInternal() {
        setState(LifecycleState.STOPPING, null);
        Container[] children = findChildren();

        for (Container child : children) {
            child.stop();
        }

        if (pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).stop();
        }
    }

    @Override
    public void destroyInternal() {
        // TODO
    }

    @Override
    public void setStartStopThreads(int startStopThreads) {
        this.startStopThreads = startStopThreads;
    }
}
