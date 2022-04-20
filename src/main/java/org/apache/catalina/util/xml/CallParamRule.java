package org.apache.catalina.util.xml;

import org.xml.sax.Attributes;

/**
 * 设置参数值到digester的参数栈中，这些参数在CallMethodRule的endElement方法使用
 * @author: Mr.Yu
 * @create: 2022-04-05 10:25
 **/
public class CallParamRule implements ParseRule{

    private Digester digester;

    private int paramIndex;

    private String bodyText;

    public CallParamRule(Digester digester, int paramIndex) {
        this.digester = digester;
        this.paramIndex = paramIndex;
    }

    @Override
    public void startElement(Attributes attributes) {

    }

    @Override
    public void body(String body) {
        bodyText = body.trim();
    }

    @Override
    public void endElement() {
        Object[] params = digester.peekParams();
        params[paramIndex] = bodyText;
    }
}
