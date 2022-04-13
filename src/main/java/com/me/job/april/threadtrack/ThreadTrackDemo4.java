package com.me.job.april.threadtrack;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

/**
 * ThreadMXBean线程池追踪
 */
public class ThreadTrackDemo4 {
    public static void main(String[] args) {
        ThreadFactory threadFactory = r -> {
            System.out.println("new thread ...");
            return new Thread(r,"ThreadTrackDemo4-thread-" + System.currentTimeMillis());
        };

        // ExecutorService pool = Executors.newSingleThreadExecutor(threadFactory);
        // ExecutorService pool = Executors.newCachedThreadPool(threadFactory);
        ExecutorService pool = Executors.newFixedThreadPool(8, threadFactory);

        IntStream.rangeClosed(1, 10).forEach(i -> pool.execute(() -> System.out.println("running...")));

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] threadIds = threadMXBean.getAllThreadIds();
        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadIds);
        Arrays.stream(threadInfos).forEach(threadInfo ->
                System.out.println(threadInfo.getThreadId() + ": " + threadInfo.getThreadName()));

        pool.shutdown();
    }
}
