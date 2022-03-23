package com.me.job.march.threadlocal;

import lombok.Getter;
import lombok.Setter;

import java.util.stream.IntStream;

public class SimpleThreadLocalDemo {
    @Getter
    @Setter
    private String content;

    public static void main(String[] args) {
        SimpleThreadLocalDemo demo = new SimpleThreadLocalDemo();
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        IntStream.rangeClosed(0, 5).forEach(i -> new Thread(() -> {
            threadLocal.set(Thread.currentThread().getName() + "设置的数据");
            System.out.println("****************");
            System.out.println(Thread.currentThread().getName() + "获取的数据: " + threadLocal.get());
            threadLocal.remove();
        }, String.valueOf(i)).start());
    }
}
