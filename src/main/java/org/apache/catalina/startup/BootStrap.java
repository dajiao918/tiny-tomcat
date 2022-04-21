package org.apache.catalina.startup;

import org.apache.catalina.Globals;
import org.apache.catalina.core.Catalina;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author: Mr.Yu
 * @create: 2022-03-30 19:35
 **/
public class BootStrap {

    Object catalina;

    ClassLoader commonLoader;
    ClassLoader catalinaLoader;
    ClassLoader shareLoader;


    public static void main(String[] args) {

        BootStrap bootStrap = new BootStrap();
        try {
            bootStrap.init();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            bootStrap.load();
            bootStrap.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void start() throws Exception{
        String methodName = "start";
        Class<?>[] paramTypes = new Class[0];
        Object[] params = new Object[0];
        Method method = catalina.getClass().getMethod(methodName, paramTypes);
        // catalina.start()
        method.invoke(catalina, params);
    }

    private void load() throws Exception {
        String methodName = "load";
        Class<?>[] paramTypes = new Class[0];
        Object[] params = new Object[0];
        Method method = catalina.getClass().getMethod(methodName, paramTypes);
        // catalina.load()
        method.invoke(catalina, params);
    }

    private void init() throws Exception {

        setCatalinaHome();
        initClassLoader();
        // 设置主线程的类加载器为catalinaLoader，其实就是commonloader
        Thread.currentThread().setContextClassLoader(catalinaLoader);

        Class<?> clazz = catalinaLoader.loadClass("org.apache.catalina.core.Catalina");
        catalina = clazz.newInstance();
        Class<?>[] paramsType = new Class[1];
        paramsType[0] = Class.forName("java.lang.ClassLoader");
        Method method = clazz.getMethod("setParentClassloader", paramsType);
        Object[] params = new Object[1];
        params[0] = shareLoader;
        // 设置类加载器为commonLoader
        method.invoke(catalina, params);
    }

    private void initClassLoader() {
        try {
            commonLoader = createClassloader("common",null);
            if (commonLoader == null) {
                commonLoader = this.getClass().getClassLoader();
            }
            catalinaLoader = createClassloader("server", commonLoader);
            shareLoader = createClassloader("share", commonLoader);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private ClassLoader createClassloader(String name, ClassLoader parent) {
        String value = CatalinaProperties.getProperty(name+".loader");
        if (value == null || value.equals("")) {
            return parent;
        }
        value = replace(value);
        StringTokenizer tokenizer = new StringTokenizer(value,",");
        Set<String> set = new HashSet<>();
        while (tokenizer.hasMoreElements()) {
            String repository = tokenizer.nextToken().trim();
            set.add(repository);
        }
        return ClassLoaderFactory.createClassloader(set,null);
    }

    private String replace(String value) {
        int left = value.indexOf("${");
        // 简单实现，不考虑过多的情况
        if (left != -1) {
            int right = value.indexOf("}");
            if (right != -1) {
                String target = value.substring(left+2,right);
                if (Globals.CATALINA_HOME.equals(target)) {
                    target = System.getProperty(Globals.CATALINA_HOME);
                } else if (Globals.CATALINA_BASE.equals(target)) {
                    target = System.getProperty(Globals.CATALINA_BASE);
                } else {
                    target = System.getProperty(target);
                }
                target = target==null?"":target;
                String tempLeft = value.substring(0,left);
                String tempRight = value.substring(right+1);
                value = tempLeft + target + tempRight;
            }
            // 递归解析
            return replace(value);
        } else {
            return value;
        }
    }

    private void setCatalinaHome() {
        String home = System.getProperty("user.dir");
        System.setProperty(Globals.CATALINA_HOME, home);
        // 我也费解为什么tomcat要设置两个一样的系统变量
        System.setProperty(Globals.CATALINA_BASE, home);
    }

}
