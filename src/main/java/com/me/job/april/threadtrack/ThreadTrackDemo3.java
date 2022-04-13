package com.me.job.april.threadtrack;

import java.util.Set;

/**
 * Thread线程追踪
 */
public class ThreadTrackDemo3 {
    public static void main(String[] args) {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        threadSet.forEach(thread -> System.out.println(thread.getId() + ": " + thread.getName()));
    }
}
