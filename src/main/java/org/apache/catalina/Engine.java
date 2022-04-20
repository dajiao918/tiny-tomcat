package org.apache.catalina;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 15:52
 **/
public interface Engine extends Container{
    String getDefaultHost();

    void setDefaultHost(String defaultHost);

    Service getService();

    void setService(Service service);
}