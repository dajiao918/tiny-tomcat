package org.apache.catalina;

import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 15:52
 **/
public interface Host extends Container{

    String ADD_ALIAS_EVENT = "addAlias";

    String REMOVE_ALIAS_EVENT = "removeAlias";

    String getXmlBase();

    void setXmlBase(String xmlBase);

    String getAppBase();

    void setAppBase(String appBase);

    boolean getAutoDeploy();

    void setAutoDeploy(boolean autoDeploy);

    ExecutorService getStartStopExecutor();

    void addAlias(String alias);

    String[] findAliases();

    void removeAlias(String alias);

    boolean getCreateDirs();

    void setCreateDirs(boolean createDirs);

}