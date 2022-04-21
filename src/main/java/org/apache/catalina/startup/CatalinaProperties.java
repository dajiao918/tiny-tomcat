package org.apache.catalina.startup;

import org.apache.catalina.Globals;

import java.io.*;
import java.util.Properties;

/**
 * @author: Mr.Yu
 * @create: 2022-04-21 02:20
 **/
public class CatalinaProperties {

    private static Properties properties;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        InputStream is = null;
        try {
            File base = new File(getCatalinaBase());
            File conf = new File(base,"conf");
            File propsFile = new File(conf,"catalina.properties");
            is = new FileInputStream(propsFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (is != null) {
            try {
                properties = new Properties();
                properties.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (is == null) {
            properties = new Properties();
        }
    }

    private static String getCatalinaBase() {
        return System.getProperty(Globals.CATALINA_BASE, System.getProperty("user.dir"));
    }

    public static String getProperty(String name) {
        return properties.getProperty(name);
    }

}
