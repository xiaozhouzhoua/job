### **谈谈你对AQS的理解**
AQS：AbstractQueuedSynchronizer 抽象队列同步器

AQS是一个抽象类，是我们用到的锁的基础，例如我们经常用到的

* ReentrantLock
* Semaphore
* CountdownLatch
* ReentrantReadWriteLock
* .....

上述的提到的这些，其实内部都是基于AQS来实现的。

关于Synchronized加锁，其实不是一来就是添加的重量级锁，而是有一个锁升级的过程

* 首先第一个线程将会尝试添加一个偏向锁，也就是对当前获取的线程1有偏向的功能，即不进行复杂加锁校验等，在线程的头部信息中，是有这么一个记录用于记录偏向的线程的
* 但是假设有多个线程来访问这个资源的时候，偏向锁就会升级成 CAS轻量级锁，也就是我们所说的自旋锁，会不断的自旋操作来获取CPU资源
* 但是假设某个线程长期进行自旋操作，而没有获取到锁，一般原来的版本是可以指定自旋次数的，后面的JDK进行优化，引入了适应性自旋。当某个线程长期获取不到资源的时候，就会升级成重量级锁，这个时候只要其它线程过来后，获取不到资源就会直接阻塞。

**为什么已经有了Synchronized加锁了，在后面又引入了很多新的锁呢，如 ReentrantLock 等**

* Synchronized加锁是需要JVM调用底层的OS来进行加锁的，这样就存在一些开销，程序需要从 用户态 -> 内核态 进行切换，这一部分是比较消耗时间的。
* 因为ReentrantLock属于API层面，不需要从进行资源的切换，也就是不用从 用户态 -> 内核态 进行切换。

**AQS的核心思想：** 如果被请求的共享资源空闲，则将当前请求的资源的线程设置为有效的工作线程，并将共享资源设置为锁定状态，如果被请求的共享资源被占用，那么就需要一套线程阻塞等待以及唤醒时锁分配的机制，这个AQS是用CLH队列锁实现的，即将暂时获取不到的锁的线程加入到队列中。CLH队列是一个虚拟的双向队列，**虚拟的双向队列即不存在队列的实例，仅存在节点之间的关联关系。**

AQS是将每一条请求共享资源的线程封装成一个CLH锁队列的一个结点（Node），来实现锁的分配

用大白话来说，AQS就是基于CLH队列，用volatile修饰共享变量state，线程通过CAS去改变状态符，成功则获取锁成功，失败则进入等待队列，同时等待被唤醒。

注意：AQS是自旋锁，在等待唤醒的时候，经常会使用自旋的方式，不断的尝试获取锁，直到被其它线程获取成功

![CLH队列](https://gitee.com/zhouguangping/image/raw/master/markdown/image-2020.png)
如上图所示，AQS维护了一个volatile int state的变量 和 一个FIFO线程等待队列，多线程争用资源被阻塞的时候，就会进入这个队列中。state就是共享资源，其访问方式有如下三种：

* getState()
* setState()
* compareAndSetState()

AQS定义了两种资源共享方式

* Exclusive：独占，只有一个线程能执行，如ReentrantLock
* Share：共享，多个线程可以同时执行，如Semaphore、CountDownLatch、ReadWriteLock、CycleBarrier

不同的自定义同步器争用共享资源的方式也不同

**AQS底层实现**
AQS使用了基于模板方法的设计模式，如果需要自定义同步器，一般的方式如下

* 继承AbstractQueuedSynchronizer，并重写指定的方法，在这里重写的方法就是对共享资源state获取和释放
* 将AQS组合在自定义同步组件的实现中，并调用其模板方法，而这些模板方法会调用使用者重写的方法。

我们通过下面的代码，来进行查看
```java
/**
 * AQS
 */
public class Sync extends AbstractQueuedSynchronizer {

    @Override
    protected boolean tryAcquire(int arg) {
        // 使用自旋锁 ，同时CAS必须保证原子性
        // 目前的CPU底层汇编都有这条指令了，即支持原语操作
        if (compareAndSetState(0, 1)) {
            // 设置排它的拥有者，也就是互斥锁
            setExclusiveOwnerThread(Thread.currentThread());
            return true;
        }
        return false;
    }

    @Override
    protected boolean tryRelease(int arg) {
        assert arg == 1;
        if(!isHeldExclusively()) {
            throw new IllegalMonitorStateException();
        }
        // 释放锁
        setExclusiveOwnerThread(null);
        setState(0);
        return super.tryRelease(arg);
    }

    @Override
    protected boolean isHeldExclusively() {
        // 判断当前线程 是不是和排它锁的线程一样
        return getExclusiveOwnerThread() == Thread.currentThread();
    }
}
```
自定义同步器在实现的时候，只需要实现共享资源state的获取和释放即可，至于具体线程等待队列的维护，AQS已经在顶层实现好了。自定义同步器实现的时候，主要实现下面几种方法：

* isHeldExclusively()：该线程是否正在独占资源。只有用到condition才需要实现它
* tryAcquire(int)：独占方式。尝试获取资源，成功则返回true，失败则返回false。
* tryRelease(int)：独占方式，尝试释放资源，成功则返回true，失败则返回false
* tryAcquireShared(int)：共享方式，尝试获取资源。负数表示失败，0表示成功，但没有剩余可用资源；正数表示成功，且有剩余资源。
* tryReleaseShared(int)：共享方式。尝试释放资源，如果允许释放后允许唤醒后续等待节点返回true，否则返回false。

**ReentrantLock**

以ReentrantLock（可重入独占式锁）为例，state初始化为0，表示未锁定状态，A线程lock()时，会调用tryAcquire()独占锁，并将state + 1，之后其它线程在想通过tryAcquire的时候就会失败，知道A线程unlock() 到 state = 0 为止，其它线程才有机会获取到该锁。A释放锁之前，自己也是可以重复获取此锁（state累加），这就是可重入的概念。

> 注意：获取多少次锁就需要释放多少次锁，保证state是能够回到0

**CountDownLatch**

以CountDownLatch为例，任务分N个子线程执行，state就初始化为N，N个线程并行执行，每个线程执行完之后 countDown() 一次，state 就会CAS减1，当N子线程全部执行完毕(即state = 0), 会unpark() 主调动线程，主调用线程就会从await()函数返回，继续之后的动作。

**总结**

一般来说，自定义同步器要么独占方式，要么共享方式，他们也需要实现 tryAcquire 和 tryRelease、 tryAcquireShared 和 tryReleaseShared中的一种即可。但AQS也支持自定义同步器实现独占和共享两种方式，比如ReentrantLockReadWriteLock。

* acquire() 和 acquireShared() 两种方式下，线程在等待队列中都是忽略中断的
* acquireInterruptibly() 和 acquireSharedInterruptibly() 是支持响应中断的