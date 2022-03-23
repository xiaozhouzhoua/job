package com.me.job.march.threadlocal;

import lombok.Getter;
import lombok.Setter;

import java.util.stream.IntStream;

public class UseSynchronizedDemo {
    @Getter
    @Setter
    private String content;

    public static void main(String[] args) {
        NoThreadLocalDemo demo = new NoThreadLocalDemo();
        IntStream.rangeClosed(0, 5).forEach(i -> new Thread(() -> {
            synchronized (UseSynchronizedDemo.class) {
                demo.setContent(Thread.currentThread().getName() + "设置的数据");
                System.out.println("****************");
                System.out.println(Thread.currentThread().getName() + "获取的数据: " + demo.getContent());
            }
        }, String.valueOf(i)).start());
    }
}
