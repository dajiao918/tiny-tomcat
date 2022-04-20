package org.apache.catalina.deploy;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Mr.Yu
 * @create: 2022-04-08 21:29
 **/
public class FilterDef {

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

    private transient Filter filter = null;

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    private String filterClass = null;

    public String getFilterClass() {
        return (this.filterClass);
    }

    public void setFilterClass(String filterClass) {
        this.filterClass = filterClass;
    }

    private String filterName = null;

    public String getFilterName() {
        return (this.filterName);
    }

    public void setFilterName(String filterName) {
        if (filterName == null || filterName.equals("")) {
            throw new RuntimeException("filterName not be null");
        }
        this.filterName = filterName;
    }



    private Map<String, String> parameters = new HashMap<String, String>();

    public Map<String, String> getParameterMap() {

        return (this.parameters);

    }

    private String asyncSupported = null;

    public String getAsyncSupported() {
        return asyncSupported;
    }

    public void setAsyncSupported(String asyncSupported) {
        this.asyncSupported = asyncSupported;
    }



    public void addInitParameter(String name, String value) {

        if (parameters.containsKey(name)) {
            return;
        }
        parameters.put(name, value);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("FilterDef[");
        sb.append("filterName=");
        sb.append(this.filterName);
        sb.append(", filterClass=");
        sb.append(this.filterClass);
        sb.append("]");
        return (sb.toString());

    }

}
