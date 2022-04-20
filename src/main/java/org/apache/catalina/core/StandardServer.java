package org.apache.catalina.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.Service;

/**
 * @author: Mr.Yu
 * @create: 2022-04-07 08:48
 **/
@Slf4j
public class StandardServer extends LifecycleBase implements Server{

    private String info = "org.apache.catalina.core.StandardServer/1.0";

    private ClassLoader parentClassloader;

    private Catalina catalina;

    private Service[] services = new StandardService[0];

    public StandardServer() {
        super();
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public ClassLoader getParentClassLoader() {
        return parentClassloader;
    }

    @Override
    public void setParentClassLoader(ClassLoader parent) {
        this.parentClassloader = parent;
    }

    @Override
    public Catalina getCatalina() {
        return catalina;
    }

    @Override
    public void setCatalina(Catalina catalina) {
        this.catalina = catalina;
    }

    @Override
    public void addService(StandardService service) {

        service.setServer(this);

        Service[] newServices = new StandardService[services.length + 1];
        System.arraycopy(services,0,newServices,0 , services.length);
        services = newServices;
        services[services.length-1] = service;
        if (getState().isAvailable()) {
            service.start();
        }
    }

    @Override
    public void await() {
        // TODO
    }

    @Override
    public Service findService(String name) {
        for (int i = 0; i < services.length; i++) {
            if (services[i].getName().equals(name)) {
                return services[i];
            }
        }
        return null;
    }

    @Override
    public Service[] findServices() {
        return services;
    }

    @Override
    public void removeService(Service service) {
        // TODO
    }

    @Override
    public void initInternal() {

        for (Service service : services) {
            service.init();
        }
    }

    @Override
    public void startInternal() {
        for (Service service : services) {
            service.start();
        }
        log.info("服务器初始完毕....");
    }

    @Override
    public void stopInternal() {
        for (Service service : services) {
            service.stop();
        }
    }

    @Override
    public void destroyInternal() {
        // TODO
    }
}
