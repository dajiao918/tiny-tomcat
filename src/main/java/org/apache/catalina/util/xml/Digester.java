package org.apache.catalina.util.xml;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 11:46
 **/
@Slf4j
public class Digester extends DefaultHandler {

    // 根据xml文件创建的对象存储栈
    private Stack<Object> objStacks = new Stack<>();
    // 每个标签的body内容栈，由于是xml层级套娃
    // 处理最外层的元素的body的时候(endElement方法执行的时候)一定是最后的，处理最内层的元素是先开始的
    // 在同级元素的时候，标签的startElement和endElement方法就是一起执行的
    private LinkedList<StringBuilder> bodyTexts = new LinkedList<>();
    // 根据标签的 名字 存储 创建对象、设置属性、调用方法的规则
    private Map<String, List<ParseRule>> ruleMap = new HashMap<>();
    // CallMethod规则和CallParam规则使用的参数栈
    private LinkedList<Object[]> params = new LinkedList<>();
    // 每个标签的body值
    private StringBuilder bodyText = new StringBuilder();

    private String match = "/";

    public Digester(){
    }

    /**
     * 交给parser解析的回调方法，当开始遇到新标签时触发
     * @param uri 为知
     * @param localName 未知
     * @param qName 标签名
     * @param attributes 标签上的属性集合
     * @return void
     * */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (!match.equals("/")) {
            match += "/";
        }
        match += qName;

        bodyTexts.push(bodyText);
        bodyText = new StringBuilder();
        List<ParseRule> rules = this.ruleMap.get(match);
        if (rules != null) {
            for (ParseRule rule : rules) {
                rule.startElement(attributes);
            }
        }
    }

    /**
     * 在处理body时的回调方法
     * @param ch 字符集合
     * @param start 开始
     * @param length 长度
     * @return void
     * */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        bodyText.append(ch,start,length);
    }

    /**
     * 在遇到标签的结束部分时触发，所以层级标签的时候，最外层的endElement方法是最后调用的
     * @param uri
     * @param localName
     * @param qName
     * @return void
     * */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        List<ParseRule> rules = this.ruleMap.get(match);
        if (rules != null) {
            for (ParseRule rule : rules) {
                rule.body(bodyText.toString());
            }
        }

        if (rules != null) {
            int size = rules.size();
            // 反着调用规则的endElement方法，因为一般第一个规则都是objectCreateRule规则
            // 而在objectCreateRule规则之后就是设置属性的规则，所以必须要等设置属性的规则
            // 调用完之后再调用objectCreateRule的endElement方法，保持栈中对象的正确性
            for (int i = size - 1; i >= 0; i--) {
                rules.get(i).endElement();
            }
        }
        bodyText = bodyTexts.pop();
        int index = match.lastIndexOf("/");
        if (index >= 0) {
            match = match.substring(0,index);
        }
    }

    public void addObjectCreate(String pattern, String className) {
        addRule(pattern,new ObjectCreateRule(this, className));
    }

    public void addObjectCreate(String pattern, String className, String classAttribute) {
        addRule(pattern,new ObjectCreateRule(this, className,classAttribute));
    }

    public void addLifecycleListenerRule(String pattern, String className) {
        addRule(pattern, new LifecycleListenerRule(this, className));
    }

    private void addRule(String pattern, ParseRule rule) {
        List<ParseRule> rules = ruleMap.computeIfAbsent(pattern, k -> new ArrayList<>());
        rules.add(rule);
    }

    public void addSetProperties(String pattern) {
        addRule(pattern, new SetPropertiesRule(this));
    }

    public void addSetBean(String pattern, String methodName) {
        addRule(pattern, new SetBeanRule(methodName,this));
    }

    public void addSetBean(String pattern, String methodName, String paramType) {
        addRule(pattern, new SetBeanRule(methodName,this,paramType));
    }

    public void addCallMethod(String pattern, String methodName, int paramCount) {
        addRule(pattern, new CallMethodRule(methodName,this,paramCount));
    }

    public void addCallParam(String pattern, int paramIndex) {
        addRule(pattern, new CallParamRule(this, paramIndex));
    }

    public void push(Object obj) {
        if (obj == null) {
            log.error("obj is null");
            return;
        }
        objStacks.push(obj);
    }

    public Object peek() {
        return objStacks.size() == 0 ? null : objStacks.peek();
    }

    public Object pop() {
        return objStacks.size() == 0 ? null : objStacks.pop();
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

    public void parse(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(inputStream, this);
        clear();
    }

    public Object peekSecond() {
        Object first = objStacks.pop();
        Object second = objStacks.peek();
        objStacks.push(first);
        return second;
    }

    public void clear() {
        objStacks.clear();
        match = "/";
        bodyTexts.clear();
        params.clear();
    }

}
