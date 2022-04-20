package org.apache.catalina.util.xml;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 根据指定的方法来对栈顶元素设置属性
 * @author: Mr.Yu
 * @create: 2022-04-05 10:12
 **/
@Slf4j
public class CallMethodRule implements ParseRule{

    private int paramCount;

    private String bodyText;

    private String methodName;

    private Digester digester;

    private Object[] paramTypes;

    public CallMethodRule(String methodName, Digester digester, int paramCount) {
        this.digester = digester;
        this.paramCount = paramCount;
        this.methodName = methodName;
        if (paramCount == 0) {
            paramTypes = new Object[1];
        } else {
            paramTypes = new Object[paramCount];
        }
        Arrays.fill(paramTypes, String.class);
    }

    @Override
    public void startElement(Attributes attributes) {
        if (paramCount > 0) {
            Object[] objects = new Object[paramCount];
            for (int i = 0; i < objects.length; i++) {
                objects[i] = null;
            }
            digester.pushParams(objects);
        }
    }

    @Override
    public void body(String body) {
        bodyText = body;
    }

    @Override
    public void endElement() {
        Object[] params;
        if (paramCount > 0) {
            params = digester.popParams();
        } else {
            params = new Object[1];
            params[0] = bodyText;
        }
        callMethod(params);
    }

    private void callMethod(Object[] params) {
        Object obj = digester.peek();
        try {
            Method method = findMethod(obj);
            if (method != null) {
                method.invoke(obj, params);
            }
            // log.info(obj.toString());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Method findMethod(Object obj)  {
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class<?>[] types = method.getParameterTypes();
                if (paramTypes.length != types.length) {
                    continue;
                }
                boolean ok = true;
                for (int j = 0; j < paramTypes.length; j++) {
                    if (!paramTypes[j].equals(types[j])) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    return method;
                }
            }
        }
        return null;
    }
}
