package sax.handler2;

import org.xml.sax.Attributes;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 10:25
 **/
public class CallParamRule implements ParseRule{

    private GirlHandler2 handler2;

    private int paramIndex;

    private String bodyText;

    public CallParamRule(GirlHandler2 handler2, int paramIndex) {
        this.handler2 = handler2;
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
        Object[] params = handler2.peekParams();
        params[paramIndex] = bodyText;
    }
}
