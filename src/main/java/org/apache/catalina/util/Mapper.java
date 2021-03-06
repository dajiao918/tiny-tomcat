package org.apache.catalina.util;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.Element;


/**
 * @author: Mr.Yu
 * @create: 2022-04-09 20:18
 **/
@Slf4j
public final class Mapper {

    private Host[] hosts = new Host[0];

    private final Context context = new Context();

    private String defaultHost = null;

    public String getDefaultHost() {
        return defaultHost;
    }

    public void setContext(String path, Object context) {
        this.context.name = path;
        this.context.obj = context;
    }

    public void setDefaultHost(String defaultHost) {
        this.defaultHost = defaultHost;
    }

    public void addHost(String name, Object host) {
        Host[] newHosts = new Host[hosts.length + 1];
        Host newHost = new Host(name,host);
        if (addNewElement(newHost, hosts, newHosts)) {
            hosts = newHosts;
        } else {
            log.error("duplicate host name: {}",name);
        }
    }

    public void addContext(String hostName, Object host, String ContextPath, Object context,
                           String[] welcomeResources) {
        Host mapHost = exactFind(hosts, hostName);
        if (mapHost == null) {
            addHost(hostName, host);
            mapHost = exactFind(hosts, hostName);
        }
        if (mapHost != null) {
            addContext(mapHost, ContextPath, context, welcomeResources);
        } else {
            log.error("add host failure host name is {}",hostName);
        }
    }

    private void addContext(Host mapHost,String contextPath, Object context, String[] welcomeResources) {
        Context newContext = new Context(contextPath, context);
        newContext.welcomeResources = welcomeResources;

        Context[] contexts = mapHost.contexts;
        Context[] newContexts = new Context[contexts.length+1];
        if (addNewElement(newContext, contexts, newContexts)) {
            mapHost.contexts = newContexts;
        } else {
            log.error("duplicate context path: {}",contextPath);
        }
    }

    public void addWrapper(String hostName, String contextPath, String pattern, Object wrapper) {
        Host host = exactFind(hosts, hostName);
        if (host == null) {
            log.error("host not found during add wrapper hostname is {}", hostName);
            return;
        }
        Context context = exactFind(host.contexts, contextPath);
        if (context == null) {
            log.error("context not found during add wrapper contextPath is {}", contextPath);
            return;
        }
        addWrapper(context, pattern, wrapper);
    }

    public void addWrapper(String pattern, Object wrapper) {
        addWrapper(context, pattern, wrapper);
    }

    public void addWrapper(Context context, String pattern, Object wrapper) {
        if (pattern.endsWith("/*")) {
            pattern = pattern.substring(0,pattern.length() - 2);
            Wrapper newWrapper = new Wrapper(pattern, wrapper);
            Wrapper[] oldWrappers = context.wildcardWrappers;
            Wrapper[] newWrappers = new Wrapper[oldWrappers.length + 1];
            if (addNewElement(newWrapper,oldWrappers,newWrappers)) {
                context.wildcardWrappers = newWrappers;
            }
        } else if (pattern.startsWith("*.")) {
            pattern = pattern.substring(2);
            Wrapper newWrapper = new Wrapper(pattern, wrapper);
            Wrapper[] oldWrappers = context.extensionWrappers;
            Wrapper[] newWrappers = new Wrapper[oldWrappers.length + 1];
            if (addNewElement(newWrapper,oldWrappers,newWrappers)) {
                context.extensionWrappers = newWrappers;
            }
        } else if (pattern.equals("/")) {
            context.defaultWrapper = new Wrapper(pattern, wrapper);
        } else {
            if (pattern.length() == 0) {
                pattern = "/";
            }
            Wrapper newWrapper = new Wrapper(pattern, wrapper);
            Wrapper[] oldWrappers = context.exactWrappers;
            Wrapper[] newWrappers = new Wrapper[oldWrappers.length + 1];
            if (addNewElement(newWrapper,oldWrappers,newWrappers)) {
                context.exactWrappers = newWrappers;
            }
        }
    }

    public void removeWrapper(String urlPattern) {
        removeWrapper(context,urlPattern);
    }

    public void removeWrapper(Context context, String pattern) {
        if (pattern.endsWith("/*")) {
            pattern = pattern.substring(0,pattern.length()-2);
            Wrapper[] oldWrappers = context.wildcardWrappers;
            if (oldWrappers.length == 0) return;
            Wrapper[] newWrappers = new Wrapper[oldWrappers.length-1];
            Wrapper wrapper = new Wrapper(pattern, null);
            if (removeElement(wrapper, oldWrappers, newWrappers)) {
                context.wildcardWrappers = newWrappers;
            }
        } else if (pattern.startsWith("*.")) {
            pattern = pattern.substring(2);
            Wrapper[] oldWrappers = context.extensionWrappers;
            if (oldWrappers.length == 0) return;
            Wrapper[] newWrappers = new Wrapper[oldWrappers.length-1];
            Wrapper wrapper = new Wrapper(pattern, null);
            if (removeElement(wrapper, oldWrappers, newWrappers)) {
                context.extensionWrappers = newWrappers;
            }
        } else if (pattern.equals("/")) {
            context.defaultWrapper = null;
        } else {
            if (pattern.equals("")) {
                pattern = "/";
            }
            Wrapper[] oldWrappers = context.exactWrappers;
            if (oldWrappers.length == 0) return;
            Wrapper[] newWrappers = new Wrapper[oldWrappers.length-1];
            Wrapper wrapper = new Wrapper(pattern, null);
            if (removeElement(wrapper, oldWrappers, newWrappers)) {
                context.exactWrappers = newWrappers;
            }
        }
    }

    public boolean addNewElement(MapElement newElement, MapElement[] oldElements, MapElement[] newElements) {
        int pos = findElement(newElement.name, oldElements);
        // pos??????-1?????????pos??????name??????name????????????host????????????
        if (pos != -1 && oldElements[pos].name.equals(newElement.name)) {
            return false;
        }
        // pos+1??????????????????????????????????????????oldElements???0->pos??????????????????newElements??? ????????????=pos-0+1
        System.arraycopy(oldElements, 0, newElements, 0, pos+1);
        // ??????pos+1
        newElements[pos+1] =  newElement;
        // ?????????oldElements???pos+1->oldElements.length-1?????????
        // ???newElements(???pos+2?????????)??????????????????=(oldElements.length-1-pos-1)+1
        System.arraycopy(oldElements, pos+1,
                newElements, pos+2, oldElements.length-pos-1);
        return true;
    }

    public boolean removeElement(MapElement newElement, MapElement[] oldElements, MapElement[] newElements) {
        int pos = findElement(newElement.name, oldElements);
        if (pos != -1 && oldElements[pos].name.equals(newElement.name)) {
            int len = oldElements.length;
            // ??????0->pos-1 ???newElement???
            System.arraycopy(oldElements, 0, newElements, 0, pos);
            // ??????pos?????????pos+1->len-1???newElements????????????????????????len-1-pos-1+1
            System.arraycopy(oldElements, pos+1, newElements, pos, len - pos - 1);
            return true;
        }
        return false;
    }

    public  <E extends MapElement> E exactFind(E[] map, String name) {
        int pos = findElement(name, map);
        if (pos >= 0) {
            if (map[pos].name.equals(name)) {
                return map[pos];
            }
        }
        return null;
    }

    /**
     * ?????????????????????????????? ????????????
     * @param name
     * @return int
     * */
    private int findElement(String name, MapElement[] elements) {
        int left = 0;
        int right = elements.length - 1;
        // ???????????????????????????
        if (right == -1) {
            return -1;
        }
        // ?????????????????????????????????????????????hosts????????????
        if (name.compareTo(elements[0].name) < 0) {
            return -1;
        }
        // ??????????????????????????????????????????hosts????????????
        if (right == 0) {
            return 0;
        }
        int mid = 0;
        while (left <= right) {
            mid = (left + right) / 2;
            if (name.compareTo(elements[mid].name) < 0) {
                right = mid - 1;
            } else if (name.compareTo(elements[mid].name) == 0) {
                return mid;
            } else {
                left = mid + 1;
            }
        }
        // ???????????????left = right + 1??????left???????????????name??????right???????????????name???
        // ???left < hosts.length????????????left<name???????????????left+1??????????????????????????????
        if (left < elements.length && name.compareTo(elements[left].name) < 0) return left;
        // ??????left>name???????????????left??????????????????????????????
        else if (left < elements.length && name.compareTo(elements[left].name) > 0) return right;
        // ??????left > hosts.length????????????????????????????????????
        else return elements.length - 1;
    }

    public void removeWrapper(String hostName, String contextPath, String urlPattern) {
        Host host = exactFind(hosts, hostName);
        if (host == null) {
            log.error("not found host byt name {}",hostName);
            return;
        }
        Context context = exactFind(host.contexts, contextPath);
        if (context == null) {
            log.error("not found context byt name {}",contextPath);
            return;
        }
        removeWrapper(context, urlPattern);
    }

    public void removeHost(String hostName) {
        Host host = new Host(hostName, null);
        Host[] oldHosts = hosts;
        Host[] newHosts = new Host[oldHosts.length-1];
        if (removeElement(host,oldHosts,newHosts)) {
            hosts = newHosts;
        }
    }

    public void removeContext(String hostName, String contextPath) {
        Host host = exactFind(hosts, hostName);
        if (host == null) {
            log.error("not found host byt name {}",hostName);
            return;
        }
        Context[] oldEles = host.contexts;
        Context[] newEles = new Context[oldEles.length - 1];
        Context newElement = new Context(contextPath, null);
        if (removeElement(newElement,oldEles,newEles)) {
            host.contexts = newEles;
        }
    }

    public Object mappingHost(String hostName) {
        Host host = mappingHostInternal(hostName);
        if (host == null) return null;
        return host.obj;
    }

    private Host mappingHostInternal(String hostName) {
        Host[] eles = hosts;
        return exactFind(eles, hostName);
    }

    public Object mappingContext(String hostName, String contextPath) {
        Host host = mappingHostInternal(hostName);
        Context[] contexts = host.contexts;
        Context context = mappingContextInternal(contexts,contextPath);
        if (context == null)
            return null;
        return context.obj;
    }

    public Object mappingWrapper(String hostName, String contextPath, String pattern) {
        Host host = mappingHostInternal(hostName);
        if (host == null)
            return null;
        Context context = mappingContextInternal(host.contexts, contextPath);
        return mappingWrapper(context, pattern);
    }

    private Object mappingWrapper(Context context, String pattern) {
        if (context == null)
            return null;
        Object wrapper = mappingExactWrapper(context.exactWrappers,pattern);
        if (wrapper == null) {
            wrapper = mappingWildWrapper(context.wildcardWrappers,pattern);
        }
        if (wrapper == null) {
            wrapper = mappingExetenstionWrapper(context.extensionWrappers,pattern);
        }
        if (wrapper == null) {
            if (context.defaultWrapper != null)
                wrapper = context.defaultWrapper.obj;
        }
        return wrapper;
    }

    // ?????? *.do *.ac ...
    private Object mappingExetenstionWrapper(Wrapper[] extensionWrappers, String pattern) {
        int last = -1;
        char[] chars = pattern.toCharArray();
        for (int i = chars.length-1; i >= 0; i --) {
            if (chars[i] == '.') {
                last = i;
                break;
            }
        }
        if (last == -1)
            return null;

        pattern = pattern.substring(last+1);
        return exactFind(extensionWrappers, pattern);
    }

    // ?????? .../*  a/* c/* cf/* d/*
    private Object mappingWildWrapper(Wrapper[] wildcardWrappers, String pattern) {
        int pos = findElement(pattern, wildcardWrappers);
        if (pos != -1) {
            String name = wildcardWrappers[pos].name;
            if (pattern.startsWith(name)) {
                return wildcardWrappers[pos].obj;
            }
        }
        return null;
    }

    private Object mappingExactWrapper(Wrapper[] exactWrappers, String pattern) {
        int pos = findElement(pattern, exactWrappers);
        // ????????????
        if (pos != -1 && exactWrappers[pos].name.equals(pattern)) {
            return exactWrappers[pos].obj;
        }
        return null;
    }

    private Context mappingContextInternal(Context[] contexts, String contextPath) {
        return exactFind(contexts, contextPath);
    }

    // context????????????mapper
    public Object mappingWrapper(String path) {
        if (path == null)
            return null;
        return mappingWrapper(context, path);
    }


    private abstract static class MapElement{
        String name;
        Object obj;

        public MapElement(String name, Object o) {
            this.name = name;
            this.obj = o;
        }
    }

    private static class Host extends MapElement {

        Context[] contexts;

        public Host(String name, Object o) {
            super(name, o);
            contexts = new Context[0];
        }
    }

    private static class Context extends MapElement {

        // ????????????>????????????>????????????>??????wrapper

        // ??????wrapper
        public Wrapper defaultWrapper = null;
        // ????????????
        public Wrapper[] exactWrappers = new Wrapper[0];
        // /* ????????????
        public Mapper.Wrapper[] wildcardWrappers = new Wrapper[0];
        // *. ????????????
        public Wrapper[] extensionWrappers = new Wrapper[0];

        public String[] welcomeResources = new String[0];

        public Context() {
            super(null,null);
        }

        public Context(String path, Object o) {
            super(path, o);
        }
    }

    private static class Wrapper extends MapElement {

        public Wrapper(String name, Object wrapper) {
            super(name, wrapper);
        }
    }

}
