package servlet;

import org.apache.catalina.servlets.DefaultServlet;

import javax.servlet.ServletException;

/**
 * @author: Mr.Yu
 * @create: 2022-04-18 16:11
 **/
public class TestDefaultServlet {

    public static void main(String[] args) throws ServletException {
        DefaultServlet servlet = new DefaultServlet();
        servlet.init(null);
//        servlet.sendStaticResource(null, null);
    }

}
