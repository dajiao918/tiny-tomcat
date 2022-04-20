package org.apache.catalina.util.xml;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在遇到xml标签时创建对象，当然这里需要指定的xml标签才创建，并不是每个xml标签都创建
 * 需要看具体的xml标签作用是什么
 * @author: Mr.Yu
 * @create: 2022-04-04 14:21
 **/
@Slf4j
public class ObjectCreateRule implements ParseRule{
    // xml标签上的全路径类名的属性名是什么，可以根据classAttribute获取标签上的全路径类名
    String classAttribute;
    // 全路径类名
    String className;
    // 类加载器
    ClassLoader loader;
    Digester digester;

    public ObjectCreateRule(String classAttribute, Digester digester) {
        this(digester,null,classAttribute);
    }

    public ObjectCreateRule(Digester digester, String className) {
        this(digester,className,null);
    }

    public ObjectCreateRule(Digester digester, String className, String classAttribute) {
        this.classAttribute = classAttribute;
        this.digester = digester;
        this.loader = Thread.currentThread().getContextClassLoader();
        this.className = className;
    }

    @Override
    public void startElement(Attributes attributes) {
        String className = null;
        if (classAttribute != null) {
            className = attributes.getValue(classAttribute);
        }
        if (className == null) {
            className = this.className;
        }
        if (className == null) {
            log.error("xml must has classname attribute!");
        }
        Object obj = null;
        try {
            obj = loader.loadClass(className).newInstance();
        } catch (ClassNotFoundException e) {
            log.error("class not found via the classname: " + className);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        digester.push(obj);
    }

    @Override
    public void body(String body) {
    }

    @Override
    public void endElement() {
        digester.pop();
    }
}
