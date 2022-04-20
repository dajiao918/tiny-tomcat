package org.apache.catalina;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 19:50
 **/
public interface Globals {

    String CATALINA_BASE = "catalina.base";

    String CATALINA_HOME = "catalina.home";

    String SERVER_CONFIG = "conf/server.xml";

    String WEB_XML = "WEB-INF/web.xml";
    String WEB_LIB = "WEB-INF\\lib";
    String WEB_CLASS = "WEB-INF\\classes";
    int MAJOR_VERSION = 3;
    int MINOR_VERSION = 0;
    String GLOBAL_WEB_XML = "conf/web.xml";
    String WEB_INF = "WEB-INF";
    String DEPLOY_BASE = "/webapps";
}
