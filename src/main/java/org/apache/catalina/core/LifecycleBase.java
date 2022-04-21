package org.apache.catalina.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;

import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 08:55
 **/
@Slf4j
public abstract class LifecycleBase implements Lifecycle {

    protected LifecycleSupport lifecycle = new LifecycleSupport(this);

    protected LifecycleState state = LifecycleState.NEW;

    public LifecycleBase() {
        LifecycleListener lifecycleListener = new DefaultLifecycleListener();
        addLifecycleListener(lifecycleListener);
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycle.addLifecycleListener(listener);
    }

    @Override
    public List<LifecycleListener> findLifecycleListeners() {
        return lifecycle.getLifecycleListener();
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycle.removeLifecycleListener(listener);
    }

    protected void fireLifecycleEvent(String description, Object data) {
        LifecycleEvent event = new LifecycleEvent(this, description, data);
        lifecycle.publishEvent(event);
    }

    @Override
    public LifecycleState getState() {
        return state;
    }

    @Override
    public String getStateName() {
        return state.getLifecycleEvent();
    }

    @Override
    public final void init() {
        if ( !(state == LifecycleState.NEW) ) {
            log.error("state not NEW but is {} during invoke init method",state);
        }
        setState(LifecycleState.INITIALIZING,null);
        initInternal();
        setState(LifecycleState.INITIALIZED, null);
    }

    protected void setState(LifecycleState state, Object data){
        this.state = state;
        fireLifecycleEvent(state.getLifecycleEvent(),data);
    }

    public abstract void initInternal();

    @Override
    public final void start() {
        if ( (state == LifecycleState.STARTING_PREP) || (state == LifecycleState.STARTING) ||
                (state == LifecycleState.STARTED)) {
            log.error("lifecycle already invoked start method and state is {}",state);
            return;
        }

        if (state == LifecycleState.NEW) {
            init();
        } else if (state == LifecycleState.FAILED) {
            stop();
        } else if (state != LifecycleState.INITIALIZED) {
            log.error("state is {} but not is INITIALIZED invoked start method",state);
        }

        setState(LifecycleState.STARTING_PREP,null);
        startInternal();
        setState(LifecycleState.STARTED, null);
    }

    public abstract void startInternal();

    @Override
    public final void stop() {
        try {
            setState(LifecycleState.STOPPING_PREP,null);
            stopInternal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setState(LifecycleState.STOPPED, null);
            destroy();
        }
    }

    public abstract void stopInternal();

    @Override
    public final void destroy() {
        if (state != LifecycleState.STOPPED) {
            stop();
        }
        setState(LifecycleState.DESTROYING,null);
        destroyInternal();
        setState(LifecycleState.DESTROYED, null);
    }

    public abstract void destroyInternal();

}
