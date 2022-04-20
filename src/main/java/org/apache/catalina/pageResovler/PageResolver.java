package org.apache.catalina.pageResovler;

import org.apache.catalina.coyote.InternalInputNioBuffer;
import org.apache.catalina.coyote.Request;
import org.apache.catalina.coyote.RequestFacade;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Mr.Yu
 * @create: 2022-04-19 22:00
 **/
public class PageResolver {

    static Pattern pattern = Pattern.compile("\\$\\{(.*?)}");

    static int first;

    static String current;

    public static byte[] resolve(byte[] bytes, HttpServletRequest request) {

        String page = new String(bytes, 0, bytes.length);
        Matcher matcher = pattern.matcher(page);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group(1);
            current = group;
            Object obj = parseRootObj(request);
            String value;
            if (obj == null) {
                value = "";
            } else {
                int second = group.indexOf(".",first+1);
                value = parseObjAttr(obj, second);
            }
            matcher.appendReplacement(sb,value);
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        return result.length() == 0 ? bytes : result.getBytes();
    }

    private static String parseObjAttr(Object obj, int index) {
        // 解析完毕
        if (index == -1) {
            if (obj == null)
                return "";
            return obj.toString();
        }
        int preIndex = index;
        String remaining = current.substring(index+1);
        String attr;
        // 匹配下一个 .
        index = current.indexOf(".",index+1);
        if (index == -1) {
            attr = remaining;
        } else {
            // 两个 . 之间的距离，例如：request.user.girl.name
            // 此时如果：remaining=girl.name  则preIndex=12, index=17 => bound=5
            int bound = index - preIndex;
            // 此时要的属性attr为girl，长度是bound-1
            attr = remaining.substring(0,bound-1);
        }
        if (attr.length() < 1) {
            throw new RuntimeException("regex has error!");
        }
        String methodName = "get" + attr.substring(0,1).toUpperCase() + attr.substring(1);
        try {
            Method method = obj.getClass().getMethod(methodName);
            obj = method.invoke(obj);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return parseObjAttr(obj, index);
    }

    private static Object parseRootObj(HttpServletRequest request) {
        int i = current.indexOf(".");
        if (i == -1)
            return null;
        first = i;
        String scope = current.substring(0,i);
        int len;
        if ((len = current.indexOf(".", first+1)) == -1) {
            len = current.length();
        }
        String attrName = current.substring(i+1,len);
        switch (scope) {
            case "request":
                return request.getAttribute(attrName);
            case "session":
                return request.getSession().getAttribute(attrName);
            case "context":
                return request.getServletContext().getAttribute(attrName);
            default:
                return null;
        }
    }

}
