package com.me.job.april.threadtrack;

import java.util.Arrays;

/**
 * ThreadGroup线程追踪
 */
public class ThreadTrackDemo2 {
    public static void main(String[] args) {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        ThreadGroup topGroup = group;
        while (group != null) {
            topGroup = group;
            group = group.getParent();
        }
        int activeThreads = topGroup.activeCount();
        Thread[] threads = new Thread[activeThreads];
        // enumerate方法用来将ThreadGroup线程组中的active线程全部复制到Thread类型的数组中
        topGroup.enumerate(threads);

        Arrays.stream(threads).forEach(thread ->
                System.out.println(thread.getId() + ": " + thread.getName()));
    }
}
