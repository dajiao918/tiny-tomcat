package org.apache.catalina;

import org.apache.catalina.core.Catalina;
import org.apache.catalina.core.StandardService;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 15:50
 **/
public interface Server extends Lifecycle{

    String getInfo();

    ClassLoader getParentClassLoader();

    void setParentClassLoader(ClassLoader parent);

    Catalina getCatalina();

    void setCatalina(Catalina catalina);

    void addService(StandardService service);

    void await();

    Service findService(String name);

    Service[] findServices();

    void removeService(Service service);
}