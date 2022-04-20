package sax.handler;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sax.entity.Coder;
import sax.entity.Girl;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: Mr.Yu
 * @create: 2022-04-04 11:01
 **/
@Slf4j
public class GirlHandler extends DefaultHandler {

    Stack<Object> stack = new Stack<>();
    AtomicInteger count = new AtomicInteger(0);

    public void push(Object obj) {
        if (obj != null)
            stack.push(obj);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        log.info("uri: {}", uri);
        log.info("localName: {}", qName);
        if ("Coder".equals(qName)) {
            Coder coder = new Coder();
            setProperties(attributes, coder);
            int i = count.getAndIncrement();
            log.info(i + ": " + coder.toString());
            stack.push(coder);
        } else if ("Girl".equals(qName)) {
            Girl girl = new Girl();
            setProperties(attributes, girl);
            int i = count.getAndIncrement();
            log.info(i + ": " + girl.toString());
            Coder coder = (Coder) stack.peek();
            coder.setGirl(girl);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String s = new String(ch, start, length);
        log.info("content: "+s.trim());
    }

    private void setProperties(Attributes attributes, Object obj) {
        Method[] methods = obj.getClass().getMethods();
        for (int i = 0; i < attributes.getLength(); i++) {
            String methodName = "set" + attributes.getQName(i).substring(0, 1).toUpperCase() + attributes.getQName(i).substring(1);
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


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("Coder".equals(qName)) {
            Coder coder = (Coder) stack.pop();
            int i = count.getAndIncrement();
            log.info(i + ": " + coder.toString());
        } else if ("Girl".equals(qName)) {
            int i = count.getAndIncrement();
            log.info(i + ": girl end element");
        }
    }

    public static void main(String[] args) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            InputStream stream = GirlHandler.class.getClassLoader().getResourceAsStream("digester.xml");
            parser.parse(stream, new GirlHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object pop() {
        return stack.size() == 0 ? null : stack.peek();
    }
}
