package org.apache.catalina.util.xml;

import lombok.Data;

/**
 * 接收实体
 * @author: Mr.Yu
 * @create: 2022-04-04 11:35
 **/
@Data
public class TwoParam<F,S> {

    private F first;
    private S second;

}
