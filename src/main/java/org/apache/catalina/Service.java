package org.apache.catalina;

import org.apache.catalina.connector.Connector;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 15:51
 **/
public interface Service extends Lifecycle{

    Container getContainer();

    void setContainer(Container container);

    String getInfo();

    String getName();

    void setName(String name);

    Server getServer();

    void setServer(Server server);

    ClassLoader getParentClassLoader();

    void setParentClassLoader(ClassLoader parent);

    void addConnector(Connector connector);

    Connector[] findConnectors();

    void removeConnector(Connector connector);
}
