package com.devdroid.sleepassistant.utils.thread;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPoolUtils {
    private static final String TAG = ThreadPoolUtils.class.getSimpleName();

    /**
     * 默认线程池
     */
    private static final ThreadPoolExecutor DEFAULT_EXECUTOR;

    /**
     * 单线程线程池
     */
    private static final ThreadPoolExecutor SINGLETON_EXECUTOR;

    /**
     * 定时任务线程池
     */
    private static final ScheduledThreadPoolExecutor SCHEDULED_EXECUTOR;

    /**
     * 定时任务线程池 (创建守护线程执行)
     */
    private static final ScheduledThreadPoolExecutor SCHEDULED_DAEMON_EXECUTOR;

    static {
        int cpuCount = Runtime.getRuntime().availableProcessors();
        final int corePoolSize = Math.max(cpuCount * 2, 4);

        DEFAULT_EXECUTOR =
            new ThreadPoolExecutor(corePoolSize, corePoolSize * 2, 30, TimeUnit.SECONDS, new LinkedBlockingQueue <Runnable>(),
                    new NamingThreadFactory("ThreadPoolUtils"), new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    try {
                        executor.getQueue().put(r);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });

        SCHEDULED_EXECUTOR =
            new ScheduledThreadPoolExecutor(corePoolSize, new NamingThreadFactory("ThreadPoolUtils-scheduled", false));

        SCHEDULED_DAEMON_EXECUTOR =
            new ScheduledThreadPoolExecutor(corePoolSize, new NamingThreadFactory("ThreadPoolUtils-scheduled-daemon", true));

        SINGLETON_EXECUTOR = newSingletonExecutor("ThreadPoolUtils-singleton");
    }

    private ThreadPoolUtils() {
        throw new UnsupportedOperationException("util class cant be instantiation");
    }

    public static void execute(Runnable runnable) {
        DEFAULT_EXECUTOR.execute(runnable);
    }

    public static void executeSingleton(Runnable runnable) {
        SINGLETON_EXECUTOR.execute(runnable);
    }

    /**
     * 调用线程池执行定时任务
     *
     * @param runnable 任务
     * @param delay    启动延迟
     * @param unit     启动延迟的时间单位
     */
    public static ScheduledFuture schedule(Runnable runnable, long delay, TimeUnit unit) {
        return SCHEDULED_EXECUTOR.schedule(runnable, delay, unit);
    }

    /**
     * 调用线程池执行定时的周期任务
     *
     * @param runnable     任务
     * @param initialDelay 启动延迟, millseconds
     * @param period       执行周期
     * @param unit         执行周期时间单位
     */
    public static ScheduledFuture scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return SCHEDULED_EXECUTOR.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    /**
     * 调用线程池执行定时任务 (在守护线程中执行)
     *
     * @param runnable 任务
     * @param delay    启动延迟
     * @param unit     启动延迟的时间单位
     */
    public static ScheduledFuture scheduleInDaemon(Runnable runnable, long delay, TimeUnit unit) {
        return SCHEDULED_DAEMON_EXECUTOR.schedule(runnable, delay, unit);
    }

    /**
     * 调用线程池执行定时的周期任务 (在守护线程中执行)
     *
     * @param runnable     任务
     * @param initialDelay 启动延迟, millseconds
     * @param period       执行周期
     * @param unit         执行周期时间单位
     */
    public static ScheduledFuture scheduleAtFixedRateInDaemon(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return SCHEDULED_DAEMON_EXECUTOR.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    /**
     * 创建一个单线程线程池
     *
     * @param threadPoolName 线程池名称
     * @return 新创建的单线程线程池
     */
    public static ThreadPoolExecutor newSingletonExecutor(String threadPoolName) {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
            new NamingThreadFactory(threadPoolName));
    }

    /**
     * 创建一个定时任务程线程池
     *
     * @param threadPoolName 线程池名称
     * @param daemon         是否为守护线程
     * @return 新创建的定时任务线程池
     */
    public static ScheduledThreadPoolExecutor newScheduledExecutor(String threadPoolName, boolean daemon) {
        return new ScheduledThreadPoolExecutor(1, new NamingThreadFactory(threadPoolName, daemon));
    }

    /**
     * 取消任务
     *
     * @param future 待取消任务
     */
    public static void cancel(Future future) {
        future.cancel(true);
    }
}
