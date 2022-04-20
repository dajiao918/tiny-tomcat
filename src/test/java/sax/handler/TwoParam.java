package sax.handler;

import lombok.Data;

/**
 * @author: Mr.Yu
 * @create: 2022-04-04 11:35
 **/
@Data
public class TwoParam<F,S> {

    private F first;
    private S second;

}
