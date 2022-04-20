package org.apache.catalina.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Globals;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author: Mr.Yu
 * @create: 2022-04-10 11:28
 **/
@Slf4j
public class WebappClassLoader extends ClassLoader {

    private final String jarPath;
    private final String classPath;

    private ClassLoader j2eeClassLoader;

    private final Map<String, byte[]> resourcesMap = new HashMap<>(20);

    private final Map<String, Class<?>> classesMap = new HashMap<>(20);

    public WebappClassLoader(ClassLoader parent, String path) {
        super(parent);
        jarPath = System.getProperty(Globals.CATALINA_HOME) + path + Globals.WEB_LIB + "\\";
        classPath = System.getProperty(Globals.CATALINA_HOME) + path + Globals.WEB_CLASS + "\\";
        j2eeClassLoader = String.class.getClassLoader();
        if (j2eeClassLoader == null) {
            j2eeClassLoader = getSystemClassLoader();
            while (j2eeClassLoader.getParent() != null) {
                j2eeClassLoader = j2eeClassLoader.getParent();
            }
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> aClass = null;
        // 从本地缓冲中获取
        if (classesMap.get(name) != null) {
            aClass = classesMap.get(name);
        }
        // 从jvm缓存中获取
        if (aClass == null)
            aClass = findLoadedClass(name);

        // 使用系统加载器加载类
        if (aClass == null) {
            try {
                aClass = j2eeClassLoader.loadClass(name);
            } catch (ClassNotFoundException e) {

            }
        }

        // 从WEB_INF目录下寻找
        if (aClass == null)
            aClass = findClass(name);

        // 从父类加载器加载
        if (aClass == null) {
            aClass = getParent().loadClass(name);
        }
        return aClass;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        name = replaceSlash(name);
        byte[] bytes = null;
        if (resourcesMap.get(name) != null) {
            bytes = resourcesMap.get(name);
        } else {
            BufferedInputStream inputStream = null;
            try  {
                // 将 '.' 换成 '/'
                String file = replace(name) + ".class";
                inputStream = new BufferedInputStream(new FileInputStream(classPath + file));
                bytes = transfer(inputStream);
            } catch (IOException e) {

            }  finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (bytes != null) {
            Class<?> clazz = null;
            try {
                clazz = defineClass(name, bytes, 0, bytes.length);
            } catch (ClassFormatError classFormatError) {
                throw new RuntimeException(classFormatError);
            }
            classesMap.put(name, clazz);
            return clazz;
        }
        return null;
    }

    private byte[] transfer(InputStream inputStream) throws IOException {

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(inputStream.available())) {
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buf)) != -1) {
                stream.write(buf, 0, len);
            }
            return stream.toByteArray();
        } catch (IOException e) {
            throw e;
        }
    }

    private String replace(String name) {
        char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '.') {
                chars[i] = '/';
            }
        }
        return new String(chars);
    }

    public void prepJarClass() {
        List<File> jarFiles = searchJar();
        if (jarFiles == null)
            return;
        for (File jar : jarFiles) {
            try {
                JarFile jarFile = new JarFile(jar);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String name = jarEntry.getName();
                    if (!name.endsWith(".class")) {
                        continue;
                    }
                    // 去除.class
                    name = name.substring(0,name.length()-6);
                    // 将 / 换成 .
                    name = replaceSlash(name);
                    InputStream inputStream = jarFile.getInputStream(jarEntry);
                    byte[] bytes = transfer(inputStream);
                    resourcesMap.put(name, bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String replaceSlash(String name) {
        if (name == null || name.length() == 0)
            return null;
        char[] array = name.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '/') {
                array[i] = '.';
            }
        }
        return new String(array);
    }

    private List<File> searchJar() {
        File file = new File(jarPath);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null)
                return Arrays.asList(files);
        }
        return null;
    }

}
