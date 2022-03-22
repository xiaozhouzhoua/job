package com.me.job.march.lock;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {
    public static int counter = 0;
    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[100];
        // ReentrantLock默认是非公平锁
        ReentrantLock lock = new ReentrantLock();
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                lock.lock();
                try {
                    for (int j = 0; j < 100; j++) {
                        counter++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            });
        }

        for (Thread t: threads) {
            t.start();
        }
        // 等待所有线程结束
        for (Thread t: threads) {
            t.join();
        }
        System.out.println(counter);
    }
}
