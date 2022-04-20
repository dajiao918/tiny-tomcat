package org.apache.catalina.util.xml;

import org.xml.sax.Attributes;

/**
 * @author: Mr.Yu
 * @create: 2022-04-04 14:18
 **/
public interface ParseRule {
    void startElement(Attributes attributes);

    void body(String body);

    void endElement();
}
