package org.apache.catalina.core;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.coyote.Request;
import org.apache.catalina.deploy.FilterMap;

import javax.servlet.Servlet;
import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2022-04-15 23:15
 **/
public class ApplicationFilterFactory {

    private static ApplicationFilterFactory instance;

    private ApplicationFilterFactory() {}

    public static ApplicationFilterFactory getInstance() {
        if (instance == null) {
            instance = new ApplicationFilterFactory();
        }
        return instance;
    }

    public ApplicationFilterChain createApplicationFilterChain(String uri, Servlet servlet,
                                                               Wrapper wrapper) {
        if (uri == null)
            return null;
        int i = uri.indexOf("/", 1);
        if (i == -1) return null;
        String servletUrlPattern = uri.substring(i);

        Context context = (Context) wrapper.getParent();
        List<FilterMap> filterMaps = context.findFilterMap();
        ApplicationFilterChain filterChain = new ApplicationFilterChain();
        filterChain.setServlet(servlet);
        for (int j = 0; j < filterMaps.size(); j++) {
            if (match(filterMaps.get(j),servletUrlPattern)) {
                ApplicationFilterConfig filterConfig =
                        context.findFilterConfig(filterMaps.get(j).getFilterName());
                filterChain.addFilter(filterConfig);
            }
        }
        return filterChain;
    }

    private boolean match(FilterMap filterMap, String servletUrlPattern) {
        // 如果配置的路径是 * 则匹配所有
        if (filterMap.getMatchAllServletNames()) {
            return true;
        }
        String[] urlPatterns = filterMap.getURLPatterns();
        if (urlPatterns == null) {
            return false;
        }
        for (String urlPattern : urlPatterns) {
            if (matchInternal(urlPattern, servletUrlPattern)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchInternal(String urlPattern, String servletUrlPattern) {
        if (urlPattern == null) {
            return false;
        }
        // 精确匹配
        if (urlPattern.equals(servletUrlPattern)) {
            return true;
        }
        // 模糊匹配
        if (urlPattern.equals("/*")) {
            return true;
        }
        if (urlPattern.endsWith("/*")) {
            String tempUrl = urlPattern.substring(0,urlPattern.length()-2);
            if (servletUrlPattern.startsWith(tempUrl)) {
                return true;
            }
        }
        // 扩展匹配
        if (urlPattern.startsWith("*.")) {
            String tempUrl = urlPattern.substring(2);
            return servletUrlPattern.endsWith(tempUrl);
        }
        // 默认
        return false;
    }

}
