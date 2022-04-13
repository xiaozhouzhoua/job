package com.me.job.april.threadfactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 判断创建了几次ThreadFactory
 */
public class ThreadFactoryCreate {
    public static void main(String[] args) {
        ThreadFactory threadFactory = r -> {
            System.out.println("new thread ...");
            return new Thread(r,"Thread-"+System.currentTimeMillis());
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor(threadFactory);

        executorService.execute(()-> System.out.println("A Run"));

        executorService.execute(()->{
            System.out.println("B Run Exception");
            throw new RuntimeException();
        });

        executorService.execute(() -> System.out.println("C Run"));

        executorService.shutdown();
    }
}
