package sax.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 10:50
 **/
@Data
public class Servlet {

    private String servletName;
    private String servletClass;
    private List<InitParam> initParam = new ArrayList<>();

    public void addInitParam(String application, String value) {
           initParam.add(new InitParam(application,value));
    }

}
