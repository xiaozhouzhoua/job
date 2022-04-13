package com.me.job.april.lock;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定位死锁
 */
public class FindDeadLocks {
    public static void main(String[] args) throws InterruptedException {
        ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
        Runnable deadLockCheck = () -> {
            long[] threadIds = mbean.findDeadlockedThreads();
            if (threadIds != null) {
                ThreadInfo[] threadInfos = mbean.getThreadInfo(threadIds);
                System.out.println("Detected deadLock threads:");
                for (ThreadInfo threadInfo : threadInfos) {
                    System.out.println(threadInfo.getThreadName());
                }
            }
        };

        ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

        // 稍等5秒，然后每10秒进行一次死锁扫描
        pool.scheduleAtFixedRate(deadLockCheck, 5L, 10L, TimeUnit.SECONDS);

        String lockA = "lockA";
        String lockB = "lockB";
        DeadLockSample t1 = new DeadLockSample("Thread-1", lockA, lockB);
        DeadLockSample t2 = new DeadLockSample("Thread-2", lockB, lockA);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
