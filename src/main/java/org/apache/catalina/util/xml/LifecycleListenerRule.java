package org.apache.catalina.util.xml;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.xml.sax.Attributes;

/**
 * @author: Mr.Yu
 * @create: 2022-04-13 11:29
 **/
public class LifecycleListenerRule implements ParseRule{

    private String className;
    private Digester digester;

    public LifecycleListenerRule(Digester digester,String className) {
        this.className = className;
        this.digester = digester;
    }

    @Override
    public void startElement(Attributes attributes) {
        if (className == null) {
            return;
        }
        try {
            Class<?> clazz = Class.forName(className);
            LifecycleListener listener = (LifecycleListener) clazz.newInstance();
            Lifecycle lifecycle = (Lifecycle) digester.peek();
            lifecycle.addLifecycleListener(listener);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void body(String body) {

    }

    @Override
    public void endElement() {

    }
}
