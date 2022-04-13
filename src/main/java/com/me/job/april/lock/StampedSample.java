package com.me.job.april.lock;

import java.util.concurrent.locks.StampedLock;

/**
 * 在运行过程中，如果读锁试图锁定时，写锁是被某个线程持有，读锁将无法获得，
 * 而只好等待对方操作结束，这样就可以自动保证不会读取到有争议的数据。
 *
 * 读写锁看起来比 synchronized 的粒度似乎细一些，但在实际应用中，
 * 其表现也并不尽如人意，主要还是因为相对比较大的开销。
 *
 * 所以，JDK 在后期引入了 StampedLock，在提供类似读写锁的同时，
 * 还支持优化读模式。优化读基于假设，大多数情况下读操作并不会和写操作冲突，
 * 其逻辑是先试着读，然后通过 validate 方法确认是否进入了写模式，如果没有进入，
 * 就成功避免了开销；如果进入，则尝试获取读锁。
 */
public class StampedSample {
    private double x, y;
    private final StampedLock sl = new StampedLock();

    private void move(double moveX, double moveY) {
        // 使用写锁-独占模式
        long stamp = sl.writeLock();
        try {
            x += moveX;
            y += moveY;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    /**
     * 判断主内存的值是否已被其他线程通过move方法修改，
     * 如果validate返回值为true，证明(x, y)的值未被修改，可参与后续计算；
     * 否则，需加悲观读锁，再次从主内存加载(x,y)的最新值，然后再进行距离计算。
     * 其中，校验锁状态这步操作至关重要，需要判断锁状态是否发生改变，
     * 从而判断之前copy到线程工作内存中的值是否与主内存的值存在不一致。
     */
    private double calculateFromOrigin() {
        long stamp = sl.tryOptimisticRead();
        double currentX = x, currentY = y;
        if (!sl.validate(stamp)) {
            stamp = sl.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}
