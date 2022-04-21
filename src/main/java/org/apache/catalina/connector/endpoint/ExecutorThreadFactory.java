package org.apache.catalina.connector.endpoint;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: Mr.Yu
 * @create: 2022-04-21 15:42
 **/
public class ExecutorThreadFactory implements ThreadFactory {

    private final String name;
    private final AtomicInteger count = new AtomicInteger();
    private final boolean daemon;
    private final ThreadGroup group;

    public ExecutorThreadFactory(String name, boolean daemon) {
        this.name = name;
        this.daemon = daemon;
        this.group = Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(group, r,name+count.getAndIncrement());
        thread.setDaemon(daemon);
        int threadPriority = Thread.NORM_PRIORITY;
        thread.setPriority(threadPriority);
        return thread;
    }
}
