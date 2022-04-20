package sax.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Mr.Yu
 * @create: 2022-04-04 10:54
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coder {

    private String name;
    private String love;
    private String sex;
    private Girl girl;

}
