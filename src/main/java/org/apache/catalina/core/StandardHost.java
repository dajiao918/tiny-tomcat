package org.apache.catalina.core;

import org.apache.catalina.Host;

import java.util.concurrent.ExecutorService;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 16:03
 **/
public class StandardHost extends ContainerBase implements Host {

    private static final String info =
            "org.apache.catalina.core.StandardHost/1.0";

    private String[] aliases = new String[0];

    private String appBase = "webapps";

    private String xmlBase = null;

    private boolean autoDeploy = true;

    private String configClass =
            "org.apache.catalina.core.ContextConfig";

    private String contextClass =
            "org.apache.catalina.core.StandardContext";

    public StandardHost() {
        super();
        pipeline.setBasic(new StandardHostValve());
    }

    @Override
    public String getXmlBase() {
        return xmlBase;
    }

    @Override
    public void setXmlBase(String xmlBase) {
        this.xmlBase = xmlBase;
    }

    @Override
    public String getAppBase() {
        return appBase;
    }

    @Override
    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }

    @Override
    public boolean getAutoDeploy() {
        return autoDeploy;
    }

    @Override
    public void setAutoDeploy(boolean autoDeploy) {
        this.autoDeploy = autoDeploy;
    }

    @Override
    public ExecutorService getStartStopExecutor() {
        return null;
    }

    @Override
    public void addAlias(String alias) {
        String[] newAliases = new String[aliases.length + 1];
        System.arraycopy(aliases, 0, newAliases, 0, aliases.length);
        newAliases[aliases.length] = alias;
        aliases = newAliases;
    }

    @Override
    public void initInternal() {
        super.initInternal();
    }

    @Override
    public void startInternal() {
        setState(LifecycleState.STARTING, null);
        super.startInternal();
    }

    @Override
    public String[] findAliases() {
        return aliases;
    }

    @Override
    public void removeAlias(String alias) {

    }

    @Override
    public boolean getCreateDirs() {
        return false;
    }

    @Override
    public void setCreateDirs(boolean createDirs) {

    }

    @Override
    public String getInfo() {
        return info;
    }
}
