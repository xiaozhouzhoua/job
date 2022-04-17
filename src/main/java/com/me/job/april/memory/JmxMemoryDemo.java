package com.me.job.april.memory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * MemoryMXBean查看堆内存和非堆内存
 */
public class JmxMemoryDemo {
    public static void main(String[] args) {
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean() ;
        MemoryUsage heap = memBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memBean.getNonHeapMemoryUsage();

        System.out.println("Heap Used  Memory: " + heap.getUsed()/1024/1024 + "M");
        System.out.println("Heap Init  Memory: " + heap.getInit()/1024/1024 + "M");
        System.out.println("Heap Committed  Memory: " + heap.getCommitted()/1024/1024 + "M");
        System.out.println("Heap Max  Memory: " + heap.getMax()/1024/1024 + "M");

        System.out.println("NonHeap Used  Memory: " + nonHeap.getUsed()/1024/1024 + "M");
        System.out.println("NonHeap Init  Memory: " + nonHeap.getInit()/1024/1024 + "M");
        System.out.println("NonHeap Committed  Memory: " + nonHeap.getCommitted()/1024/1024 + "M");
    }
}
