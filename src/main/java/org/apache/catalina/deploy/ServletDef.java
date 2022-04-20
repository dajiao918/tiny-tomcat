package org.apache.catalina.deploy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author: Mr.Yu
 * @create: 2022-04-08 21:34
 **/
public class ServletDef {

    private String description = null;

    public String getDescription() {
        return (this.description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String displayName = null;

    public String getDisplayName() {
        return (this.displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private String servletName = null;

    public String getServletName() {
        return (this.servletName);
    }

    public void setServletName(String servletName) {
        if (servletName == null || servletName.equals("")) {
            throw new RuntimeException("servletName shouldn't be null in web.xml");
        }
        this.servletName = servletName;
    }

    private String servletClass = null;

    public String getServletClass() {
        return (this.servletClass);
    }

    public void setServletClass(String servletClass) {
        this.servletClass = servletClass;
    }

    private Map<String, String> parameters = new HashMap<String, String>();

    public Map<String, String> getParameterMap() {

        return (this.parameters);

    }

    public void addInitParameter(String name, String value) {

        if (parameters.containsKey(name)) {
            return;
        }
        parameters.put(name, value);

    }

    private Integer loadOnStartup = null;

    public Integer getLoadOnStartup() {
        return (this.loadOnStartup);
    }

    public void setLoadOnStartup(String loadOnStartup) {
        this.loadOnStartup = Integer.valueOf(loadOnStartup);
    }

    private Boolean asyncSupported = null;

    public Boolean getAsyncSupported() {
        return this.asyncSupported;
    }

    public void setAsyncSupported(String asyncSupported) {
        this.asyncSupported = Boolean.valueOf(asyncSupported);
    }

    private Boolean enabled = null;

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = Boolean.valueOf(enabled);
    }

    private boolean overridable = false;

    public boolean isOverridable() {
        return overridable;
    }

    public void setOverridable(boolean overridable) {
        this.overridable = overridable;
    }
}
