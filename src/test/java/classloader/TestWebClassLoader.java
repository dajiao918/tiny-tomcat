package classloader;

import org.apache.catalina.util.WebappClassLoader;

/**
 * @author: Mr.Yu
 * @create: 2022-04-10 13:26
 **/
public class TestWebClassLoader {

    public static void main(String[] args) throws ClassNotFoundException {
        WebappClassLoader classLoader = new WebappClassLoader(TestWebClassLoader.class.getClassLoader(),"\\webapps\\test\\");
        Class<?> aClass = classLoader.loadClass("security.ArraycopyTest");
        System.out.println(aClass.getClassLoader());
        Class<?> managerClass = classLoader.loadClass("security.TestSecurityManager");
        System.out.println(managerClass.getClassLoader());
        Class<?> stringClazz = classLoader.loadClass("com.dajiao.pojo.User");
        System.out.println(stringClazz.getClassLoader());
    }

}
