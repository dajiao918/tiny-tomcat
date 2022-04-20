package org.apache.catalina.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;

import java.io.File;
import java.io.IOException;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 22:05
 **/
@Slf4j
public class HostConfig implements LifecycleListener {

    protected File appBase;

    protected StandardHost host;

    protected String contextClass = "org.apache.catalina.core.StandardContext";

    protected String configClass = "org.apache.catalina.core.ContextConfig";

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if (event.getType().equals(Lifecycle.BEFORE_INIT_EVENT)) {
            init(event);
        } else if (event.getType().equals(Lifecycle.START_EVENT)) {
            start();
        }
    }

    private void init(LifecycleEvent event) {
        Lifecycle lifecycle = event.getLifecycle();
        this.host = (StandardHost) lifecycle;
    }

    private void start() {
        if (!appBase().isDirectory()) {
            log.error("appBase is not a directory");
            return;
        }
        deployApp();
    }

    private void deployApp() {
        File appBase = appBase();
        String[] appDirectories = appBase.list();
        deployDirectories(appBase, appDirectories);
    }

    private void deployDirectories(File appBase, String[] appDirectories) {
        for (int i = 0; i < appDirectories.length; i++) {

            File dir = new File(appBase, appDirectories[i]);
            deployDirectory(dir,appDirectories[i]);
        }
    }

    private void deployDirectory(File dir, String docBase) {
        Context context;
        try {
            Class<?> clazz = Class.forName(contextClass);
            Object o = clazz.newInstance();
            context = (Context) o;
            LifecycleListener lifecycleListener = (LifecycleListener) Class.forName(configClass).newInstance();
            context.addLifecycleListener(lifecycleListener);
            context.setName("/" + docBase);
            context.setPath("/" + docBase);
            context.setDocBase(docBase);
            host.addChild(context);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // 添加部署过的项目 。。

        }
    }

    private File appBase() {

        if (appBase != null) {
            return appBase;
        }
        appBase = returnCanonicalPath(host.getAppBase());
        return this.appBase;
    }

    private File returnCanonicalPath(String path) {

        File file = new File(path);
        File base = new File(System.getProperty(Globals.CATALINA_HOME));
        if (!file.isAbsolute()) {
            file = new File(base, path);
        }
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
