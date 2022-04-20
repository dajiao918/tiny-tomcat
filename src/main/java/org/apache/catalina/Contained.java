package org.apache.catalina;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 15:34
 **/
public interface Contained {

    public Container getContainer();

    public void setContainer(Container container);

}