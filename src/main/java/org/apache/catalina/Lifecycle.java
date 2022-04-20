package org.apache.catalina;

import org.apache.catalina.core.LifecycleState;

import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 15:48
 **/
public interface Lifecycle {

    String BEFORE_INIT_EVENT = "before_init";

    String AFTER_INIT_EVENT = "after_init";

    String START_EVENT = "start";

    String BEFORE_START_EVENT = "before_start";

    String AFTER_START_EVENT = "after_start";

    String STOP_EVENT = "stop";

    String BEFORE_STOP_EVENT = "before_stop";

    String AFTER_STOP_EVENT = "after_stop";

    String AFTER_DESTROY_EVENT = "after_destroy";

    String BEFORE_DESTROY_EVENT = "before_destroy";

    String PERIODIC_EVENT = "periodic";

    String CONFIGURE_START_EVENT = "configure_start";

    public void addLifecycleListener(LifecycleListener listener);

    public List<LifecycleListener> findLifecycleListeners();

    public void removeLifecycleListener(LifecycleListener listener);

    public String getInfo();

    public void init();

    public void start();

    public void stop();

    public void destroy();

    public LifecycleState getState();

    public String getStateName();


}
