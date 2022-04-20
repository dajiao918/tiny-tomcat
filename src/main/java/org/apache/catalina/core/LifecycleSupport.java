package org.apache.catalina.core;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 09:03
 **/
public class LifecycleSupport {

    private Lifecycle lifecycle = null;
    private List<LifecycleListener> listeners = new ArrayList<>(1);

    public LifecycleSupport(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public void addLifecycleListener(LifecycleListener lifecycleListener) {
        listeners.add(lifecycleListener);
    }

    public void publishEvent(LifecycleEvent event) {
        listeners.forEach((listener) -> {
            listener.lifecycleEvent(event);
        });
    }

    public void removeLifecycleListener(LifecycleListener listener) {
        listeners.remove(listener);
    }

    public List<LifecycleListener> getLifecycleListener(){
        return listeners;
    }

}
