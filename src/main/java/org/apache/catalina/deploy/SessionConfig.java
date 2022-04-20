package org.apache.catalina.deploy;

import java.util.EnumSet;

/**
 * @author: Mr.Yu
 * @create: 2022-04-08 21:40
 **/
public class SessionConfig {

    private Integer sessionTimeout;
    private String cookieName;
    private String cookieDomain;
    private String cookiePath;
    private String cookieComment;
    private Boolean cookieHttpOnly;
    private Boolean cookieSecure;
    private Integer cookieMaxAge;

    public Integer getSessionTimeout() {
        return sessionTimeout;
    }
    public void setSessionTimeout(String sessionTimeout) {
        this.sessionTimeout = Integer.valueOf(sessionTimeout);
    }

    public String getCookieName() {
        return cookieName;
    }
    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getCookieDomain() {
        return cookieDomain;
    }
    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    public String getCookiePath() {
        return cookiePath;
    }
    public void setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
    }

    public String getCookieComment() {
        return cookieComment;
    }
    public void setCookieComment(String cookieComment) {
        this.cookieComment = cookieComment;
    }

    public Boolean getCookieHttpOnly() {
        return cookieHttpOnly;
    }
    public void setCookieHttpOnly(String cookieHttpOnly) {
        this.cookieHttpOnly = Boolean.valueOf(cookieHttpOnly);
    }

    public Boolean getCookieSecure() {
        return cookieSecure;
    }
    public void setCookieSecure(String cookieSecure) {
        this.cookieSecure = Boolean.valueOf(cookieSecure);
    }

    public Integer getCookieMaxAge() {
        return cookieMaxAge;
    }
    public void setCookieMaxAge(String cookieMaxAge) {
        this.cookieMaxAge = Integer.valueOf(cookieMaxAge);
    }

}
