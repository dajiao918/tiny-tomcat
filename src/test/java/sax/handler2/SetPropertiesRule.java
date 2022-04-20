package sax.handler2;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;
import sax.handler.TwoParam;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: Mr.Yu
 * @create: 2022-04-04 14:29
 **/
@Slf4j
public class SetPropertiesRule implements ParseRule{

    GirlHandler2 handler;
    static AtomicInteger count = new AtomicInteger(0);
    public SetPropertiesRule(GirlHandler2 handler) {
        this.handler = handler;
    }

    @Override
    public void startElement(Attributes attributes) {
        Object obj = handler.peek();
        setProperties(attributes, obj);
        log.info("startElement"+count.getAndIncrement() + ": " + obj.toString());
    }

    @Override
    public void body(String body) {

    }

    @Override
    public void endElement() {

    }

    private void setProperties(Attributes attributes, Object obj) {
        Method[] methods = obj.getClass().getMethods();
        for (int i = 0; i < attributes.getLength(); i++) {
            String methodName = "set" + attributes.getLocalName(i).substring(0, 1).toUpperCase() + attributes.getQName(i).substring(1);
            String value = attributes.getValue(i);
            TwoParam<Method, Object[]> param = getParam(methods, methodName, value);
            if (param == null) {
                continue;
            }
            Method method = param.getFirst();
            Object[] params = param.getSecond();
            try {
                method.invoke(obj, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private TwoParam<Method, Object[]> getParam(Method[] methods, String methodName, String value) {
        for (Method method : methods) {
            if (method.getParameterCount() != 1 || !method.getName().equals(methodName)) {
                continue;
            }
            Object[] params = new Object[1];
            Class<?>[] types = method.getParameterTypes();
            Class<?> type = types[0];
            if (type.equals(String.class)) {
                params[0] = value;
            } else {
                boolean ok = transfer(type, params, value);
                if (!ok) {
                    return null;
                }
            }
            TwoParam<Method, Object[]> param = new TwoParam<>();
            param.setFirst(method);
            param.setSecond(params);
            return param;
        }
        return null;
    }

    private boolean transfer(Class<?> type, Object[] params, String value) {
        String typeName = type.getName();
        boolean ok = true;
        switch (typeName) {
            case "java.lang.Integer":
            case "int":
                try {
                    int parseInt = Integer.parseInt(value);
                    params[0] = parseInt;
                } catch (Exception e) {
                    ok = false;
                    log.error("trans int occur error");
                }
                break;
            case "java.lang.Long":
            case "long":
                try {
                    long l = Long.parseLong(value);
                    params[0] = l;
                } catch (Exception e) {
                    ok = false;
                    log.error("trans int occur error");
                }
                break;
            case "java.lang.Boolean":
            case "boolean":
                params[0] = Boolean.valueOf(value);
                break;
        }
        return ok;
    }

}
