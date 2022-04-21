package org.apache.catalina.connector;

import org.apache.catalina.*;
import org.apache.catalina.core.LifecycleBase;
import org.apache.catalina.core.LifecycleEvent;
import org.apache.catalina.core.LifecycleState;
import org.apache.catalina.util.Mapper;

/**
 *
 * @author: Mr.Yu
 * @create: 2022-04-11 11:25
 **/
public class MapperListener extends LifecycleBase implements ContainerListener, LifecycleListener {

    private static final String info = "org.apache.catalina.connector.MapperListener/1.0";

    // 全局mapper映射器，可以映射到当前service的所有host，context，wrapper
    private Mapper mapper;

    private Connector connector;

    public MapperListener(Mapper mapper, Connector connector) {
        this.mapper = mapper;
        this.connector = connector;
    }

    @Override
    public void containerEvent(ContainerEvent event) {
        if (event.getType().equals(Container.ADD_CHILD_EVENT)) {
            Container container = event.getContainer();
            if (container instanceof Host) {
                Host host = (Host) container;
                registerHost(host);
            } else if (container instanceof Context) {
                Context context = (Context) container;
                Host host =  ((Host) context.getParent());
                String hostName = host.getName();
                registerContext(hostName, host, context);
            } else if (container instanceof Wrapper) {
                Wrapper wrapper = (Wrapper) container;
                Context context = (Context) wrapper.getParent();
                Host host = (Host) context.getParent();
                registerWrapper(host.getName(), context.getPath(), wrapper);
            }
        } else if (event.getType().equals(Container.REMOVE_CHILD_EVENT)) {
            // 去除mapper中的映射在stop事件发生时进行
            Container child = (Container) event.getData();
            removeListeners(child);
        } else if (event.getType().equals(Wrapper.ADD_MAPPING_EVENT)) {
            Container container = event.getContainer();
            String mapping = (String) event.getData();
            Wrapper wrapper = (Wrapper) container;
            Context context = (Context) wrapper.getParent();
            Host host = (Host) context.getParent();
            mapper.addWrapper(host.getName(),context.getPath(),mapping,wrapper);
        } else if (event.getType().equals(Wrapper.REMOVE_MAPPING_EVENT)) {
            Container container = event.getContainer();
            String mapping = (String) event.getData();
            Wrapper wrapper = (Wrapper) container;
            Context context = (Context) wrapper.getParent();
            Host host = (Host) context.getParent();
            mapper.removeWrapper(host.getName(),context.getPath(),mapping);
        }
    }

    @Override
    public void initInternal() { }

    @Override
    public void startInternal() {
        setState(LifecycleState.STARTING, null);
        findDefaultHost();
        Engine engine = (Engine) connector.getService().getContainer();
        // 添加监听器到connector关联的容器中
        addListener(engine);

        Container[] conHosts = engine.findChildren();
        for (Container conHost : conHosts) {
            Host host = (Host) conHost;
            if (!LifecycleState.NEW.equals(host.getState())) {
                registerHost(host);
            }
        }
    }

    /**
     * 注册host到mapper中
     * @param host
     * @return void
     * */
    private void registerHost(Host host) {
        String hostName = host.getName();
        mapper.addHost(hostName,host);
        Container[] children = host.findChildren();
        for (Container child : children) {
            Context context = (Context) child;
            registerContext(hostName,host,context);
        }
    }

    /**
     * 注册context到mapper中
     * @param hostName mapper中context的上级host
     * @param host host实体
     * @param context context
     * @return void
     * */
    private void registerContext(String hostName, Host host, Context context) {
        String path = context.getPath();
        mapper.addContext(hostName, host, path, context, null);
        Container[] children = context.findChildren();
        for (Container child : children) {
            Wrapper wrapper = (Wrapper) child;
            registerWrapper(hostName,path,wrapper);
        }
    }

    private void registerWrapper(String hostName, String contextPath, Wrapper wrapper) {
        for (String mapping : wrapper.findMappings()) {
            mapper.addWrapper(hostName, contextPath, mapping, wrapper);
        }
    }

    private void addListener(Container container) {
        container.addContainerListener(this);
        container.addLifecycleListener(this);
        Container[] children = container.findChildren();
        for (Container child : children) {
            addListener(child);
        }
    }

    private void removeListeners(Container container) {
        container.removeContainerListener(this);
        container.removeLifecycleListener(this);
        for (Container child : container.findChildren()) {
            removeListeners(child);
        }
    }

    private void findDefaultHost() {

        Service service = connector.getService();
        Engine engine = (Engine) service.getContainer();
        String defaultHost = engine.getDefaultHost();

        Container[] children = engine.findChildren();
        for (Container child : children) {
            Host host = (Host) child;
            if (host.getName().equals(defaultHost)) {
                mapper.setDefaultHost(defaultHost);
                break;
            }
        }

    }

    @Override
    public void stopInternal() {
        setState(LifecycleState.STOPPING,null);

        Engine engine = (Engine) connector.getService().getContainer();
        removeListeners(engine);
    }

    @Override
    public void destroyInternal() {}

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        // 先删除孩子，再删除父亲，使用AFTER_STOP_EVENT事件
        if (event.getType().equals(Lifecycle.AFTER_STOP_EVENT)) {
            Object obj = event.getSource();
            if (obj instanceof Wrapper) {
                unregisterWrapper((Wrapper) obj);
            } else if (obj instanceof Context) {
                unregisterContext((Context) obj);
            } else if (obj instanceof Host) {
                unregisterHost((Host) obj);
            }
        } else if (event.getType().equals(Lifecycle.AFTER_START_EVENT)) {
            Object obj = event.getSource();
            if (obj instanceof Wrapper) {
                Wrapper wrapper = (Wrapper) obj;
                Context context = (Context) wrapper.getParent();
                Host host = (Host) context.getParent();
                if (context.getState().isAvailable()) {
                    registerWrapper(host.getName(),context.getPath(),wrapper);
                }
            } else if (obj instanceof Context) {
                Context c = (Context) obj;
                Host host = (Host) c.getParent();
                if (c.getParent().getState().isAvailable()) {
                    registerContext(host.getName(),host,c);
                }
            } else if (obj instanceof Host) {
                registerHost((Host) obj);
            }
        }
    }

    private void unregisterHost(Host host) {
        mapper.removeHost(host.getName());
    }

    private void unregisterContext(Context context) {
        Host host = (Host) context.getParent();
        mapper.removeContext(host.getName(),context.getPath());
    }

    private void unregisterWrapper(Wrapper wrapper) {
        Context context = (Context) wrapper.getParent();
        Host host = (Host) context.getParent();
        for (String mapping : wrapper.findMappings()) {
            mapper.removeWrapper(host.getName(),context.getPath(),mapping);
        }
    }
}
