package com.me.job.april.threadtrack;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;

/**
 * ThreadMXBean线程追踪
 */
public class ThreadTrackDemo {
    public static void main(String[] args) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] threadIds = threadMXBean.getAllThreadIds();
        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadIds);
        Arrays.stream(threadInfos).forEach(threadInfo ->
                System.out.println(threadInfo.getThreadId() + ": " + threadInfo.getThreadName()));
    }
}
