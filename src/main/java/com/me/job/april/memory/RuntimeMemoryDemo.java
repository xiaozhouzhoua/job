package com.me.job.april.memory;

/**
 * 1.maxMemory() -- 为JVM的最大可用内存，可通过-Xmx设置，默认值为物理内存的1/4，设值不能高于计算机物理内存；
 * 2.totalMemory() -- 为当前JVM占用的内存总数，其值相当于当前JVM已使用的内存及freeMemory()的总和，会随着JVM使用内存的增加而增加；
 * 3.freeMemory() -- 为当前JVM空闲内存，因为JVM只有在需要内存时才占用物理内存使用，所以freeMemory()的值一般情况下都很小，
 * 而JVM实际可用内存并不等于freeMemory()，而应该等于 maxMemory()-totalMemory()+freeMemory()。及其设置JVM内存分配
 */
public class RuntimeMemoryDemo {
    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Free  Memory: " + runtime.freeMemory()/1024/1024 + "M");
        System.out.println("Total  Memory: " + runtime.totalMemory()/1024/1024 + "M");
        System.out.println("Max  Memory: " + runtime.maxMemory()/1024/1024 + "M");
        System.out.println("Available  Processors: " + runtime.availableProcessors());
    }
}
