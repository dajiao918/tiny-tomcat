package org.apache.catalina;

import org.apache.catalina.connector.MapperListener;

import javax.management.ObjectName;
import javax.naming.directory.DirContext;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 15:51
 **/
public interface Container extends Lifecycle{

    String ADD_CHILD_EVENT = "addChild";

    String ADD_VALVE_EVENT = "addValve";

    String REMOVE_CHILD_EVENT = "removeChild";

    String REMOVE_MAPPER_EVENT = "removeMapper";

    String REMOVE_VALVE_EVENT = "removeValve";

    public Pipeline getPipeline();

    public String getName();

    public void setName(String name);

    public Container getParent();

    public void setParent(Container container);

    public ClassLoader getParentClassLoader();

    public void setParentClassLoader(ClassLoader parent);

    public void addChild(Container child);

    public Container findChild(String name);

    public Container[] findChildren();

    public void removeChild(Container child);

    public int getStartStopThreads();

    public void setStartStopThreads(int startStopThreads);

    public void fireContainerEvent(String type, Object data);

    public void addContainerListener(ContainerListener listener);

    void removeContainerListener(ContainerListener listener);
}