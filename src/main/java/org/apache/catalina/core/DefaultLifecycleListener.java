package org.apache.catalina.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Connector;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 10:10
 **/
@Slf4j
public class DefaultLifecycleListener implements LifecycleListener {
    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        Object source = event.getSource();
        Lifecycle lifecycle = (Lifecycle) source;
        // 只打印出container和connector的启动日志
        if (lifecycle instanceof Container || lifecycle instanceof Connector) {
            String info = lifecycle.getInfo();
            switch (event.getType()) {
                case Lifecycle.BEFORE_INIT_EVENT:
                    log.info("{} init ...", info);
                    break;
                case Lifecycle.START_EVENT:
                    log.info("{} starting ...", info);
                    break;
                case Lifecycle.STOP_EVENT:
                    log.info("{} stopping ...", info);
                    break;
            }
        }
    }
}
