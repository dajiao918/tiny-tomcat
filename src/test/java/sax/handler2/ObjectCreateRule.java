package sax.handler2;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import sax.handler.GirlHandler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: Mr.Yu
 * @create: 2022-04-04 14:21
 **/
@Slf4j
public class ObjectCreateRule implements ParseRule{

    String classAttribute;
    String className;
    ClassLoader loader;
    GirlHandler2 handler;
    static AtomicInteger count = new AtomicInteger(0);

    public ObjectCreateRule(String classAttribute, GirlHandler2 handler, String className) {
        this.classAttribute = classAttribute;
        this.handler = handler;
        this.loader = Thread.currentThread().getContextClassLoader();
        this.className = className;
    }

    @Override
    public void startElement(Attributes attributes) {
        String className = attributes.getValue(classAttribute);
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
            log.error("class not found via the classname: " + classAttribute);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        handler.push(obj);
    }

    @Override
    public void body(String body) {
    }

    @Override
    public void endElement() {

    }
}
