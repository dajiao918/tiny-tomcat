package org.apache.catalina;

import java.util.EventObject;

/**
 * @author: Mr.Yu
 * @create: 2022-04-11 11:28
 **/
public class ContainerEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    private Object data = null;

    private String type = null;

    public ContainerEvent(Container container, String type, Object data) {

        super(container);
        this.type = type;
        this.data = data;

    }

    public Object getData() {

        return (this.data);

    }

    public Container getContainer() {

        return (Container) getSource();

    }

    public String getType() {

        return (this.type);

    }

    @Override
    public String toString() {

        return ("ContainerEvent['" + getContainer() + "','" +
                getType() + "','" + getData() + "']");

    }

}
