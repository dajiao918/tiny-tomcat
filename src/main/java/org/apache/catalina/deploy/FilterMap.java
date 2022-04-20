package org.apache.catalina.deploy;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author: Mr.Yu
 * @create: 2022-04-08 21:32
 **/
public class FilterMap {

    private String filterName = null;

    public String getFilterName() {
        return (this.filterName);
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    private boolean matchAllUrlPatterns = false;

    public boolean getMatchAllUrlPatterns() {
        return matchAllUrlPatterns;
    }

    private boolean matchAllServletNames = false;

    public boolean getMatchAllServletNames() {
        return matchAllServletNames;
    }


    private String[] urlPatterns = new String[0];

    public String[] getURLPatterns() {
        if (matchAllUrlPatterns) {
            return new String[] {};
        } else {
            return (this.urlPatterns);
        }
    }

    public void addURLPattern(String urlPattern) {
        if ("*".equals(urlPattern)) {
            this.matchAllUrlPatterns = true;
        } else {
            String[] results = new String[urlPatterns.length + 1];
            System.arraycopy(urlPatterns, 0, results, 0, urlPatterns.length);
            results[urlPatterns.length] = urlPattern;
            urlPatterns = results;
        }
    }
}
