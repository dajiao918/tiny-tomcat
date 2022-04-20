package org.apache.catalina.core;

import org.apache.catalina.Lifecycle;

import java.util.EventObject;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 16:40
 **/
public class LifecycleEvent extends EventObject {

    private Object data = null;

    private String type = null;


    public LifecycleEvent(Lifecycle lifecycle, String type, Object data) {

        super(lifecycle);
        this.type = type;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Lifecycle getLifecycle() {
        return (Lifecycle) getSource();
    }
}
