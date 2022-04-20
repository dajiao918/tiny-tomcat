package org.apache.catalina;

/**
 * @author: Mr.Yu
 * @create: 2022-04-20 09:14
 **/
public interface WelComeResource {

    public void addWelComeFile(String filename);

    public byte[] findWelcomeResource();
}