package org.apache.catalina.coyote;

/**
 * @author: Mr.Yu
 * @create: 2022-04-14 15:24
 **/
public enum HttpStatus {
    OK(200),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500),
    BAD_REQUEST(400),
    MOVED_TEMPORARILY(302),
    FORBIDDEN(403);
    private int code;
    HttpStatus(int code){
        this.code = code;
    }
    public int getCode(){
        return code;
    }
}
