package org.apache.catalina.util;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.core.LifecycleBase;
import org.apache.catalina.core.StandardContext;

/**
 * @author: Mr.Yu
 * @create: 2022-04-10 21:19
 **/
public class WebClassLoader extends LifecycleBase implements Loader {

    private static final String info = "org.apache.catalina.util.WebClassLoader/1.0";

    private WebappClassLoader classLoader;

    private Container container;

    private boolean reloadable;

    public WebClassLoader(Container container) {
        this.container = container;
    }

    @Override
    public void initInternal() {
        Context context = ((Context) container);
        Host host = (Host) context.getParent();
        ClassLoader classLoader = this.getClass().getClassLoader();
        String path = "\\" + host.getAppBase() + context.getPath() + "\\";
        this.classLoader =
                new WebappClassLoader(classLoader,path);
    }

    @Override
    public void startInternal() {
        classLoader.prepJarClass();
    }

    @Override
    public void stopInternal() {
        classLoader = null;
    }

    @Override
    public void destroyInternal() {

    }

    @Override
    public void backgroundProcess() {
        // 检测文件是否改动，热部署项目
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public boolean getReloadable() {
        return reloadable;
    }

    @Override
    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }

    @Override
    public boolean modified() {
        return false;
    }
}
