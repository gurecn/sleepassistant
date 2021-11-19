package com.devdroid.sleepassistant.utils.thread;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamingThreadFactory implements ThreadFactory {
    private final ThreadGroup group;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;

    private final boolean daemon;

    public NamingThreadFactory(String poolName) {
        this(poolName, false);
    }

    public NamingThreadFactory(String poolName, boolean daemon) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = poolName + "-thread-";
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        t.setDaemon(daemon);
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
