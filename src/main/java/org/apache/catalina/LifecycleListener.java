package org.apache.catalina;

import org.apache.catalina.core.LifecycleEvent;

public interface LifecycleListener {

    public void lifecycleEvent(LifecycleEvent event);
}