package org.apache.catalina.util.xml;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 弹出当前栈中元素，将弹出元素根据methodName获取的方法和栈顶元素关联
 * @author: Mr.Yu
 * @create: 2022-04-04 14:50
 **/
@Slf4j
public class SetBeanRule implements ParseRule{

    private String methodName;
    private Digester digester;
    private String paramType;
    public SetBeanRule(String methodName, Digester digester) {
        this(methodName,digester,null);
    }

    public SetBeanRule(String methodName, Digester digester,String paramType) {
        this.methodName = methodName;
        this.digester = digester;
        this.paramType = paramType;
    }

    @Override
    public void startElement(Attributes attributes) {

    }

    @Override
    public void body(String body) {

    }

    @Override
    public void endElement() {
        Object param = digester.peek();
        Object obj = digester.peekSecond();
        Class<?>[] params = new Class[1];
        if (paramType != null) {
            try {
                params[0] = Class.forName(paramType);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            params[0] = param.getClass();
        }
        try {
            Method method = obj.getClass().getMethod(methodName, params);
            method.invoke(obj, param);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("obj is {},methodName is {}, params is {}",obj.getClass(),methodName,params[0].toString());
            throw new RuntimeException(e);
        }
    }
}
