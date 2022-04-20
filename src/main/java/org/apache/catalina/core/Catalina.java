package org.apache.catalina.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Globals;
import org.apache.catalina.Server;
import org.apache.catalina.util.xml.Digester;

import java.io.*;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 17:00
 **/
@Slf4j
public class Catalina {

    private Server server;

    public void setServer(StandardServer server) {
        this.server = server;
    }

    public Server getServer() {
        return this.server;
    }

    public void load() {
        // 设置catalina.home为用户工作目录
        initDirs();
        // 创建xml解析器
        Digester digester = createDigester();
        InputStream inputStream = null;
        // 获取conf/server.xml文件
        File file = configFile();
        try {
            inputStream = new FileInputStream(file);
            // 以当前对象为根对象
            digester.push(this);
            // 解析xml，并实例化对象
            digester.parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // 初始化server
            getServer().init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (getServer() == null) {
            load();
        }
        if (getServer() == null) {
            log.error("server create failure");
            return;
        }
        try {
            getServer().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File configFile() {
        File file = new File(Globals.SERVER_CONFIG);
        if (!file.isAbsolute()) {
            file = new File(System.getProperty(Globals.CATALINA_HOME), Globals.SERVER_CONFIG);
        }
        return file;
    }

    // 设置系统变量CATALINA_HOME，也就是tomcat的根目录
    private void initDirs() {
        String catalineHome = System.getProperty(Globals.CATALINA_HOME);
        if (catalineHome == null) {
            catalineHome = System.getProperty("user.dir");
        }
        if (catalineHome != null) {
            File file = new File(catalineHome);
            // 获取文件的绝对路径
            if (!file.isAbsolute()) {
                try {
                    catalineHome = file.getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.setProperty(Globals.CATALINA_HOME, catalineHome);
        }
        if (System.getProperty(Globals.CATALINA_BASE) == null) {
            System.setProperty(Globals.CATALINA_BASE, System.getProperty(Globals.CATALINA_HOME));
        }
    }

    private Digester createDigester() {

        Digester digester = new Digester();
        // 创建StandardServer
        digester.addObjectCreate("/Server", "org.apache.catalina.core.StandardServer");
        // 设置属性
        digester.addSetProperties("/Server");
        // 将StandardServer通过setServer方法设置到Catalina中，下面是一样的操作
        digester.addSetBean("/Server", "setServer");
        digester.addObjectCreate("/Server/Service", "org.apache.catalina.core.StandardService");
        digester.addSetProperties("/Server/Service");
        digester.addSetBean("/Server/Service", "addService");
        digester.addObjectCreate("/Server/Service/Connector", "org.apache.catalina.connector.Connector");
        digester.addSetProperties("/Server/Service/Connector");
        digester.addSetBean("/Server/Service/Connector", "addConnector");
        digester.addObjectCreate("/Server/Service/Engine", "org.apache.catalina.core.StandardEngine");
        digester.addSetProperties("/Server/Service/Engine");
        digester.addSetBean("/Server/Service/Engine", "setContainer", "org.apache.catalina.Container");
        digester.addObjectCreate("/Server/Service/Engine/Host", "org.apache.catalina.core.StandardHost");
        digester.addSetProperties("/Server/Service/Engine/Host");
        digester.addLifecycleListenerRule("/Server/Service/Engine/Host", "org.apache.catalina.core.HostConfig");
        digester.addSetBean("/Server/Service/Engine/Host", "addChild","org.apache.catalina.Container");
        return digester;
    }

}
