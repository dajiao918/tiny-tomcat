package sax.handler2;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: Mr.Yu
 * @create: 2022-04-04 14:50
 **/
@Slf4j
public class SetBeanRule implements ParseRule{

    private String methodName;
    private GirlHandler2 handler2;
    static AtomicInteger count = new AtomicInteger(0);
    public SetBeanRule(String methodName, GirlHandler2 handler2) {
        this.methodName = methodName;
        this.handler2 = handler2;
    }

    @Override
    public void startElement(Attributes attributes) {

    }

    @Override
    public void body(String body) {

    }

    @Override
    public void endElement() {
        Object param = handler2.pop();
        Object obj = handler2.peek();
        try {
            Method method = obj.getClass().getMethod(methodName, param.getClass());
            method.invoke(obj, param);
            log.info("endElement"+count.getAndIncrement() + ": "+ obj.toString());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
