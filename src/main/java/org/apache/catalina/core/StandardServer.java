package org.apache.catalina.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

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

    private int port = 8085;
    private ServerSocket serverSocket;

    public StandardServer() {
        super();
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public ClassLoader getParentClassLoader() {
        if (parentClassloader != null)
            return parentClassloader;

        parentClassloader = catalina.getParentClassLoader();
        if (parentClassloader == null)
            parentClassloader = ClassLoader.getSystemClassLoader();
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

    // 等待停止服务器
    @Override
    public void await() {
        try {
            serverSocket = new ServerSocket(port,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (serverSocket != null) {
            try {
                while (true) {
                    String command = null;
                    Socket stopSocket = null;
                    InputStream inputStream = null;
                    try {
                        stopSocket = serverSocket.accept();
                        inputStream = stopSocket.getInputStream();
                        byte[] bytes = new byte[100];
                        int read = inputStream.read(bytes, 0, bytes.length);
                        command = new String(bytes,0,read);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            if (stopSocket != null) {
                                stopSocket.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if ("shutdown".equals(command)) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
        setState(LifecycleState.INITIALIZING, null);
        for (Service service : services) {
            service.init();
        }
    }

    @Override
    public void startInternal() {
        setState(LifecycleState.STARTING, null);
        for (Service service : services) {
            service.start();
        }
        log.info("服务器初始完毕....");
    }

    @Override
    public void stopInternal() {
        setState(LifecycleState.STOPPING, null);
        for (Service service : services) {
            service.stop();
        }
    }

    @Override
    public void destroyInternal() {
        // TODO
    }
}
