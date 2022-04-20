package org.apache.catalina;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

/**
 * @author: Mr.Yu
 * @create: 2022-04-05 15:53
 **/
public interface Wrapper extends Container{

    String ADD_MAPPING_EVENT = "add_mapping";

    String REMOVE_MAPPING_EVENT = "remove_mapping";

    public int getLoadOnStartup();

    /**
     * Set the load-on-startup order value (negative value means
     * load on first call).
     */
    public void setLoadOnStartup(int value);


    /**
     * Return the run-as identity for this servlet.
     */
    public String getRunAs();


    /**
     * Set the run-as identity for this servlet.
     */
    public void setRunAs(String runAs);


    /**
     * Return the fully qualified servlet class name for this servlet.
     */
    public String getServletClass();


    /**
     * Set the fully qualified servlet class name for this servlet.
     */
    public void setServletClass(String servletClass);


    /**
     * Gets the names of the methods supported by the underlying servlet.
     */
    public String[] getServletMethods() throws ServletException;



    /**
     * Return the associated servlet instance.
     */
    public Servlet getServlet();


    /**
     * Set the associated servlet instance
     */
    public void setServlet(Servlet servlet);


    /**
     * Add a new servlet initialization parameter for this servlet.
     */
    public void addInitParameter(String name, String value);


    /**
     * Add a mapping associated with the Wrapper.
     *
     * @param mapping The new wrapper mapping
     */
    public void addMapping(String mapping);


    /**
     * Allocate an initialized instance of this Servlet that is ready to have
     * its <code>service()</code> method called.  If the servlet class does
     * not implement <code>SingleThreadModel</code>, the (only) initialized
     * instance may be returned immediately.  If the servlet class implements
     * <code>SingleThreadModel</code>, the Wrapper implementation must ensure
     * that this instance is not allocated again until it is deallocated by a
     * call to <code>deallocate()</code>.
     */
    public Servlet allocate() throws ServletException;


    /**
     * Return this previously allocated servlet to the pool of available
     * instances.  If this servlet class does not implement SingleThreadModel,
     * no action is actually required.
     */
    public void deallocate(Servlet servlet);


    /**
     * Return the value for the specified initialization parameter name,
     * if any; otherwise return <code>null</code>.
     *
     * @param name Name of the requested initialization parameter
     */
    public String findInitParameter(String name);


    /**
     * Return the names of all defined initialization parameters for this
     * servlet.
     */
    public String[] findInitParameters();

    /**
     * Return the mappings associated with this wrapper.
     */
    public String[] findMappings();

    /**
     * Load and initialize an instance of this servlet, if there is not already
     * at least one initialized instance.  This can be used, for example, to
     * load servlets that are marked in the deployment descriptor to be loaded
     * at server startup time.
     */
    public void load() throws ServletException;

    public void removeInitParameter(String name);

    public void removeMapping(String mapping);

    public boolean isAsyncSupported();

    public void setAsyncSupported(boolean asyncSupport);

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    void unload();
}