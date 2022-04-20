package org.apache.catalina.deploy;

/**
 * @author: Mr.Yu
 * @create: 2022-04-10 10:53
 **/
public class ApplicationListener {

    private final String className;

    public ApplicationListener(String className) {
        this.className = className;
    }


    public String getClassName() {
        return className;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ApplicationListener)) {
            return false;
        }
        ApplicationListener other = (ApplicationListener) obj;
        if (className == null) {
            if (other.className != null) {
                return false;
            }
        } else if (!className.equals(other.className)) {
            return false;
        }
        return true;
    }

}
