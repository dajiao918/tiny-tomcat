package org.apache.catalina.util;

import org.apache.catalina.Container;

import java.beans.PropertyChangeListener;

/**
 * @author: Mr.Yu
 * @create: 2022-04-10 21:21
 **/
public interface Loader {

    public void backgroundProcess();

    public ClassLoader getClassLoader();

    public Container getContainer();

    public void setContainer(Container container);

    public String getInfo();

    public boolean getReloadable();

    public void setReloadable(boolean reloadable);

    public boolean modified();

}