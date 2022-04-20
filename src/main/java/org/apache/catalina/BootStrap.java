package org.apache.catalina;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.Catalina;

/**
 * @author: Mr.Yu
 * @create: 2022-03-30 19:35
 **/
public class BootStrap {

    public static void main(String[] args) {
        Catalina catalina = new Catalina();
        catalina.load();
        catalina.start();
    }

}
