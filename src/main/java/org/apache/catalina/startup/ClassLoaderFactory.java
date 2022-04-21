package org.apache.catalina.startup;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: Mr.Yu
 * @create: 2022-04-21 10:45
 **/
public class ClassLoaderFactory {
    public static ClassLoader createClassloader(Set<String> set,ClassLoader parent) {
        Set<URL> urls = new HashSet<>();
        for (String str : set) {
            File file = new File(str);
            if (file.exists() && file.isDirectory()) {
                String uri = file.toURI().toString();
//                System.out.println(uri);
                try {
                    URL url = new URL(uri);
                    urls.add(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        URL[] resources = urls.toArray(new URL[0]);
        return new URLClassLoader(resources);
    }
}
