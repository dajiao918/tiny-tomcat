package org.apache.catalina;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;

import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 16:53
 **/
public interface Pipeline {

    public Valve getBasic();

    public void setBasic(Valve valve);

    public void addValve(Valve valve);

    public List<Valve> getValves();

    public void removeValve(Valve valve);

    public Valve getFirst();

    public Container getContainer();

    public void setContainer(Container container);
}