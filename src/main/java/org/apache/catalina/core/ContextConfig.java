package org.apache.catalina.core;

import org.apache.catalina.*;
import org.apache.catalina.deploy.WebXml;
import org.apache.catalina.util.xml.Digester;
import org.xml.sax.SAXException;
import sun.rmi.runtime.Log;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashSet;

/**
 * @author: Mr.Yu
 * @create: 2022-04-08 21:17
 **/
public class ContextConfig implements LifecycleListener {

    protected Context context;

    protected Digester digester;

    @Override
    public void lifecycleEvent(LifecycleEvent event) {

        if (event.getType().equals(Lifecycle.BEFORE_START_EVENT)) {
            context = (Context) event.getLifecycle();
            digester = createDigester();
        }
        else if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
            webConfig();
        }
    }

    private void webConfig() {

        HashSet<WebXml> defaults = new HashSet<>();
        // 解析全局web.xml文件
        WebXml defaultWebXmlFragment = getDefaultWebXmlFragment();
        if (defaultWebXmlFragment != null)
            defaults.add(defaultWebXmlFragment);

        WebXml webXml = new WebXml();
        // 以webXml作为根节点，将web.xml的内容全部解析到webXml对象中
        InputStream stream = webXmlStream();
        parse(webXml,stream);
        // 解析web-fragment.xml...

        // 合并web.xml
        webXml.merge(defaults);
        // 将webXml内容解析到context
        webXml.configureContext(context);
    }

    public void parse(WebXml webXml, InputStream stream) {
        digester.push(webXml);
        try {
            digester.parse(stream);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private WebXml getDefaultWebXmlFragment() {
        WebXml webXml = new WebXml();
        InputStream defaultWebStream = getDefaultWebStream();
        if (defaultWebStream == null) {
            return null;
        }
        parse(webXml, defaultWebStream);
        return webXml;
    }

    private InputStream getDefaultWebStream() {
        String globalWebXmlPath = Globals.GLOBAL_WEB_XML;
        String baseDir = System.getProperty(Globals.CATALINA_HOME);
        if (baseDir != null) {
            File file = new File(baseDir, globalWebXmlPath);
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private InputStream webXmlStream() {
        String contextBaseName = context.getBaseName();
        String appBase = ((Host) context.getParent()).getAppBase();
        String webXml = contextBaseName + "/" + Globals.WEB_XML;
        File file = new File( appBase, webXml );
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(file.getAbsolutePath() + "not exist!");
        }

    }

    private Digester createDigester() {
        Digester digester = new Digester();
        // 设置setDisplayName
        digester.addCallMethod("/web-app/description","setDescription",0);
        // 设置display-name
        digester.addCallMethod("/web-app/display-name", "setDisplayName", 0);
        // 创建filterDef
        digester.addObjectCreate("/web-app/filter", "org.apache.catalina.deploy.FilterDef");
        digester.addSetBean("/web-app/filter","addFilter");
        // 读取filter-name
        digester.addCallMethod("/web-app/filter/filter-name", "setFilterName", 0);
        // 读取filter-class
        digester.addCallMethod("/web-app/filter/filter-class", "setFilterClass", 0);
        // 读取初始化参数
        digester.addCallMethod("/web-app/filter/init-param", "addInitParameter", 2);
        digester.addCallParam("/web-app/filter/init-param/param-name", 0);
        digester.addCallParam("/web-app/filter/init-param/param-value", 1);
        // 创建filterMapping
        digester.addObjectCreate("/web-app/filter-mapping", "org.apache.catalina.deploy.FilterMap");
        digester.addSetBean("/web-app/filter-mapping", "addFilterMapping");
        digester.addCallMethod("/web-app/filter-mapping/filter-name", "setFilterName", 0);
        digester.addCallMethod("/web-app/filter-mapping/url-pattern", "addURLPattern",0);

        // 添加监听器
        digester.addCallMethod("/web-app/listener", "addListener", 1);
        digester.addCallParam("/web-app/listener/listener-class", 0);

        // 创建servletDef
        digester.addObjectCreate("/web-app/servlet", "org.apache.catalina.deploy.ServletDef");
        digester.addSetBean("/web-app/servlet", "addServlet");
        digester.addCallMethod("/web-app/servlet/description", "setDescription", 0);
        digester.addCallMethod("/web-app/servlet/display-name","setDisplayName",0);
        digester.addCallMethod("/web-app/servlet/servlet-name", "setServletName",0);
        digester.addCallMethod("/web-app/servlet/servlet-class", "setServletClass", 0);

        // 读取初始化参数
        digester.addCallMethod("/web-app/servlet/init-param", "addInitParameter", 2);
        digester.addCallParam("/web-app/servlet/init-param/param-name", 0);
        digester.addCallParam("/web-app/servlet/init-param/param-value", 1);
        digester.addCallMethod("/web-app/servlet/load-on-startup","setLoadOnStartup",0);
        digester.addCallMethod("/web-app/servlet-mapping", "addServletMapping", 2);
        digester.addCallParam("/web-app/servlet-mapping/url-pattern", 0);
        digester.addCallParam("/web-app/servlet-mapping/servlet-name", 1);

        digester.addCallMethod("/web-app/welcome-file-list/welcome-file","addWelcomeFile",0);

        return digester;
    }

    private void configureStart() {

    }
}
