package sax.handler2;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;

import javax.jws.Oneway;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 10:12
 **/
@Slf4j
public class CallMethodRule implements ParseRule{

    private int paramCount;

    private String bodyText;

    private String methodName;

    private GirlHandler2 handler2;

    private Object[] paramTypes;

    public CallMethodRule(String methodName, GirlHandler2 handler2, int paramCount) {
        this.handler2 = handler2;
        this.paramCount = paramCount;
        this.methodName = methodName;
        if (paramCount == 0) {
            paramTypes = new Object[1];
        } else {
            paramTypes = new Object[paramCount];
        }
        for (int i = 0; i < paramTypes.length; i++) {
            paramTypes[i] = String.class;
        }
    }

    @Override
    public void startElement(Attributes attributes) {
        if (paramCount > 0) {
            Object[] objects = new Object[paramCount];
            for (int i = 0; i < objects.length; i++) {
                objects[i] = null;
            }
            handler2.pushParams(objects);
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
            params = handler2.popParams();
        } else {
            params = new Object[1];
            params[0] = bodyText;
        }
        callMethod(params);
    }

    private void callMethod(Object[] params) {
        Object obj = handler2.peek();
        try {
            Method method = findMethod(obj);
            if (method != null) {
                method.invoke(obj, params);
            }
            log.info(obj.toString());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private Method findMethod(Object obj) throws NoSuchMethodException {
        Method[] methods = obj.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                Class<?>[] types = methods[i].getParameterTypes();
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
                    return methods[i];
                }
            }
        }
        return null;
    }
}
