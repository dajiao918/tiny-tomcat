package sax.handler2;

import sax.handler.GirlHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

/**
 * @author: Mr.Yu
 * @create: 2022-04-04 14:45
 **/
public class Main {

    public static void main(String[] args) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            InputStream stream = GirlHandler.class.getClassLoader().getResourceAsStream("web.xml");
            parser.parse(stream, new GirlHandler2());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
