package org.apache.catalina.core;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import org.apache.catalina.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 15:20
 **/
public class StandardPipeline extends LifecycleBase implements Pipeline, Contained {

    private static final String info = "org.apache.catalina.core.StandardPipeline/1.0";

    private Container container;

    private Valve basic = null;

    private Valve first = null;

    public StandardPipeline(Container container) {
        super();
        this.container = container;
    }

    @Override
    public Valve getBasic() {
        return basic;
    }

    @Override
    public void setBasic(Valve valve) {
        Valve oldBasic = this.basic;
        if (oldBasic == valve)
            return;

        if (oldBasic != null) {
            if (getState().isAvailable() && (oldBasic instanceof Lifecycle)) {
                ((Lifecycle) oldBasic).stop();
            }
            if (oldBasic instanceof Contained) {
                ((Contained) oldBasic).setContainer(null);
            }
        }

        if (valve == null) {
            return;
        }
        if (valve instanceof Contained) {
            ((Contained) valve).setContainer(this.container);
        }
        Valve v = first;
        while (v != null ) {
            if (v.getNext() == oldBasic) {
                v.setNext(valve);
                break;
            }
            v = v.getNext();
        }
        this.basic = valve;
    }

    @Override
    public void addValve(Valve valve) {
        if (valve instanceof Contained) {
            ((Contained) valve).setContainer(this.container);
        }
        if (valve instanceof Lifecycle) {
            ((Lifecycle) valve).start();
        }
        if (first == null) {
            first = valve;
            valve.setNext(basic);
            return;
        }
        Valve v = first;
        while ((v.getNext()) != basic) {
            v = v.getNext();
        }
        v.setNext(valve);
        valve.setNext(basic);
    }

    @Override
    public List<Valve> getValves() {
        List<Valve> valves = new ArrayList<>();
        Valve v = first;
        if (v == null) {
            v = basic;
        }
        while (v != null) {
            valves.add(v);
            v = v.getNext();
        }
        return valves;
    }

    @Override
    public void removeValve(Valve valve) {
        // TODO
    }

    @Override
    public Valve getFirst() {
        if (first == null)
            return basic;
        return first;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public void initInternal() {
    }

    @Override
    public void startInternal() {

    }

    @Override
    public void stopInternal() {

    }

    @Override
    public void destroyInternal() {

    }

    @Override
    public String getInfo() {
        return info;
    }
}
