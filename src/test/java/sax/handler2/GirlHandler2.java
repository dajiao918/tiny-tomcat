package sax.handler2;

import com.sun.javafx.css.Rule;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sax.entity.Coder;
import sax.entity.Girl;

import java.util.*;

/**
 * @author: Mr.Yu
 * @create: 2022-04-04 14:35
 **/
@Slf4j
public class GirlHandler2 extends DefaultHandler {

    Stack<Object> stack = new Stack<>();

    LinkedList<StringBuilder> bodyTexts = new LinkedList<>();

    Map<String, List<ParseRule>> ruleMap = new HashMap<>();

    LinkedList<Object[]> params = new LinkedList<>();

    StringBuilder bodyText = new StringBuilder();

    public GirlHandler2(){
    }

    {
        ArrayList<ParseRule> rules = new ArrayList<>();
        rules.add(new ObjectCreateRule("class", this,"sax.entity.Servlet"));
        ruleMap.put("servlet", rules);
        rules = new ArrayList<>();
        ruleMap.put("servlet-name", rules);
        rules.add(new CallMethodRule("setServletName",this,0));
        rules = new ArrayList<>();
        ruleMap.put("servlet-class", rules);
        rules.add(new CallMethodRule("setServletClass",this,0));
        rules = new ArrayList<>();
        ruleMap.put("init-param", rules);
        rules.add(new CallMethodRule("addInitParam", this, 2));
        rules = new ArrayList<>();
        ruleMap.put("param-name", rules);
        rules.add(new CallParamRule(this,0));
        rules = new ArrayList<>();
        ruleMap.put("param-value", rules);
        rules.add(new CallParamRule(this,1));
    }

//    {
//        log.info("obj code kuai init");
//        ArrayList<ParseRule> rules = new ArrayList<>();
//        rules.add(new ObjectCreateRule("class",this));
//        rules.add(new SetPropertiesRule(this));
//        ruleMap.put("Coder",rules);
//        rules = new ArrayList<>();
//        rules.add(new ObjectCreateRule("class",this));
//        rules.add(new SetPropertiesRule(this));
//        rules.add(new SetBeanRule("setGirl", this));
//        ruleMap.put("Girl",rules);
//    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        bodyTexts.push(bodyText);
        bodyText = new StringBuilder();
        List<ParseRule> rules = this.ruleMap.get(qName);
        if (rules != null) {
            for (ParseRule rule : rules) {
                rule.startElement(attributes);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        bodyText.append(ch,start,length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        List<ParseRule> rules = this.ruleMap.get(qName);
        if (rules != null) {
            for (ParseRule rule : rules) {
                rule.body(bodyText.toString());
            }
        }

        rules = this.ruleMap.get(qName);
        if (rules != null) {
            for (ParseRule rule : rules) {
                rule.endElement();
            }
        }
        bodyText = bodyTexts.pop();
    }

    public void push(Object obj) {
        if (obj == null) {
            log.error("obj is null");
            return;
        }
        stack.push(obj);
    }

    public Object peek() {
        return stack.size() == 0 ? null : stack.peek();
    }

    public Object pop() {
        return stack.size() == 0 ? null : stack.pop();
    }

    public void pushParams(Object[] objects) {
        this.params.push(objects);
    }

    public Object[] popParams() {
        return params.pop();
    }

    public Object[] peekParams() {
        return params.peek();
    }
}
