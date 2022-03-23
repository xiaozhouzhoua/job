### **谈谈你对ThreadLocal的理解**
ThreadLocal介绍

从Java官方文档中的描述：ThreadLocal类用来提供线程内部的局部变量。这种变量在多线程环境下访问（通过get和set方法访问）时能保证各个线程的变量相对独立于其他线程内的变量。

ThreadLocal实例通常来说都是private static类型的，用于关联线程和线程上下文。

我们可以得知ThreadLocal的作用是：提供线程内的局部变量，不同的线程之间不会相互干扰，这种变量在线程的生命周期内起作用，减少同一个线程内多个函数或组件之间一些公共变量传递的复杂度。

* 线程并发：在多线程并发的场景下
* 传递数据：我们可以通过ThreadLocal在同一线程，不同组件之间传递公共变量（**有点类似于Session**）
* 线程隔离：每个线程的变量都是独立的，不会互相影响

基本使用
在介绍Thread使用之前，我们首先认识几个Thread的常见方法


方法声明|描述
---|--- 
ThreadLocal()|创建ThreadLocal对象
public void set(T value)|设置当前线程绑定的局部变量
public T get()|获取当前线程绑定的局部变量
public void remove()|移除当前线程绑定的局部变量

**ThreadLocal与Synchronized的区别**

虽然ThreadLocal模式与Synchronized关键字都用于处理多线程并发访问变量的问题，不过两者处理问题的角度和思路不同。

| |Synchronized|ThreadLocal|
|---|---|---|
原理|同步机制采用以时间换空间的方式，只提供了一份变量，让不同的线程排队访问|ThreadLocal采用以空间换时间的概念，为每个线程都提供一份变量副本，从而实现同时访问而互不干扰
侧重点|多个线程之间访问资源的同步|多线程中让每个线程之间的数据相互隔离

总结：虽然使用ThreadLocal和Synchronized都能解决问题，但是使用ThreadLocal更为合适，因为这样可以使程序拥有更高的并发性。

**开启事务的注意点**

为了保证所有操作在一个事务中，应用中使用的连接必须是同一个；service层开启事务的connection需要跟dao层访问数据库的connection保持一致

线程并发情况下，每个线程只能操作各自的connection，也就是线程隔离

**常规解决方法**

基于上面给出的前提，大家通常想到的解决方法

* 从service层将connection对象向dao层传递
* 加锁

**常规解决方法的弊端**

* 提高代码的耦合度（因为我们需要从service 层 传入 connection参数）
* 降低程序的性能（加了同步代码块，失去了并发性）

这个时候就可以通过ThreadLocal和当前线程进行绑定，来降低代码之间的耦合

![transaction](https://gitee.com/zhouguangping/image/raw/master/markdown/threadlocal.png)

**使用ThreadLocal解决**

针对上面出现的情况，我们需要对原来的JDBC连接池对象进行更改

* 将原来从连接池中获取对象，改成直接获取当前线程绑定的连接对象
* 如果连接对象是空的，再去连接池中获取连接，将此连接对象跟当前线程进行绑定

```java
ThreadLocal<Connection> tl = new ThreadLocal();
public static Connection getConnection() {
    Connection conn = tl.get();
    if(conn == null) {
        conn = ds.getConnection();
        tl.set(conn);
    }
    return conn;
}
```
**ThreadLocal实现的好处**

从上述我们可以看到，在一些特定场景下，ThreadLocal方案有两个突出的优势：

* 传递数据：保存每个线程绑定的数据，在需要的地方可以直接获取，避免参数直接传递带来的代码耦合问题
* 线程隔离：各线程之间的数据相互隔离却又具备并发性，避免同步方式带来的性能损失

**ThreadLocal的内部结构**

JDK8中ThreadLocal的设计是：每个Thread维护一个ThreadLocalMap，这个Map的key是ThreadLocal实例本身，value才是真正要存储的值object。具体的过程是这样的：

* 每个Thread线程内部都有一个Map（ThreadLocalMap）
* Map里面存储ThreadLocal对象（key）和线程的变量副本（value）
* Thread内部的Map是由ThreadLocal维护的，由ThreadLocal负责向map获取和设置线程的变量值。
* 对于不同的线程，每次获取副本值时，别的线程并不能获取到当前线程的副本值，形成了副本的隔离，互不干扰。

![diff](https://gitee.com/zhouguangping/image/raw/master/markdown/threadLocaldiff.png)

从上面变成JDK8的设计有什么好处？

* 每个Map存储的Entry数量变少，因为原来的Entry数量是由Thread决定，而现在是由ThreadLocal决定的，真实开发中，Thread的数量远远大于ThreadLocal的数量
* 当Thread销毁的时候，ThreadLocalMap也会随之销毁，因为ThreadLocal是存放在Thread中的，随着Thread销毁而消失，能降低开销。

**基本结构**

ThreadLocalMap是ThreadLocal的内部类，没有实现Map接口，用独立的方式实现了Map的功能，其内部的Entry也是独立实现。

![threadlocalmap](https://gitee.com/zhouguangping/image/raw/master/markdown/threadlocalmap.png)

**成员变量**

```java
/**
* 初始容量 - 必须是2的整次幂
**/
private static final int INITIAL_CAPACITY = 16;

/**
*存放数据的table ，Entry类的定义在下面分析，同样，数组的长度必须是2的整次幂
**/
private Entry[] table;

/**
*数组里面entrys的个数，可以用于判断table当前使用量是否超过阈值
**/
private int size = 0;

/**
*进行扩容的阈值，表使用量大于它的时候进行扩容
**/
private int threshold; // Default to 0
```
跟HashMap类似，INITIAL_CAPACITY代表这个Map的初始容量；table是一个Entry类型的数组，用于存储数据；size代表表中的存储数目；threshold代表需要扩容时对应的size的阈值。

**存储结构 - Entry**

```java
/*
*Entry继承WeakRefefence，并且用ThreadLocal作为key.
如果key为nu11（entry.get（）==nu11），意味着key不再被引用，
*因此这时候entry也可以从table中清除。
*/
static class Entry extends weakReference<ThreadLocal<?>>{

object value；Entry（ThreadLocal<？>k，object v）{
    super(k);
    value = v;
}}
```
在ThreadLocalMap中，也是用Entry来保存K-V结构数据的。不过Entry中的key只能是ThreadLocal对象，这点在构造方法中已经限定死了。
另外，Entry继承WeakReference，也就是key（ThreadLocal）是弱引用，其目的是将ThreadLocal对象的生命周期和线程生命周期解绑。

**弱引用和内存泄漏**

有些程序员在使用ThreadLocal的过程中会发现有内存泄漏的情况发生，就猜测这个内存泄漏跟Entry中使用了弱引用的key有关系。这个理解其实是不对的。

我们先来回顾这个问题中涉及的几个名词概念，再来分析问题。

**内存泄漏相关概念**

* Memory overflow：内存溢出，没有足够的内存提供申请者使用。
* Memory leak：内存泄漏是指程序中己动态分配的堆内存由于某种原因程序未释放或无法释放，造成系统内存的浪费，导致程序运行速度减慢甚至系统溃等严重后果。内存泄漏的堆积终将导致内存溢出。

**弱引用相关概念**

Java中的引用有4种类型：强、软、弱、虚。当前这个问题主要涉及到强引用和弱引用：

* 强引用（"Strong"Reference），就是我们最常见的普通对象引用，只要还有强引用指向一个对象，就能表明对象还“活着”，垃圾回收器就不会回收这种对象。
* 弱引用（WeakReference），垃圾回收器一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。

**如果key使用强引用，那么会出现内存泄漏？**

假设ThreadLocalMap中的key使用了强引用，那么会出现内存泄漏吗？此时ThreadLocal的内存图（实线表示强引用）如下：

![ref](https://gitee.com/zhouguangping/image/raw/master/markdown/ref.png)
* 假设在业务代码中使用完ThreadLocal，threadLocal Ref被回收了
* 但是因为threadLocalMap的Entry强引用了threadLocal，造成threadLocal无法被回收。
* 在没有手动删除这个Entry以及CurrentThread依然运行的前提下，始终有强引用链 threadRef->currentThread->threadLocalMap->entry，Entry就不会被回收（Entry中包括了ThreadLocal实例和value），导致Entry内存泄漏。

也就是说，ThreadLocalMap中的key使用了强引用，是无法完全避免内存泄漏的。

**如果key使用弱引用，那么会出现内存泄漏？**

![ref2](https://gitee.com/zhouguangping/image/raw/master/markdown/ref2.png)
* 同样假设在业务代码中使用完ThreadLocal，threadLocal Ref被回收了。
* 由于ThreadLocalMap只持有ThreadLocal的弱引用，没有任何强引用指向threadlocal实例，所以threadloca就可以顺利被gc回收，此时Entry中的key=null。
* 但是在没有手动删除这个Entry以及CurrentThread依然运行的前提下，也存在有强引用链 threadRef->currentThread->threadLocalMap->entry-> value，value不会被回收，而这块value永远不会被访问到了，导致value内存泄漏。

也就是说，ThreadLocalMap中的key使用了弱引用，**也有可能内存泄漏。**

比较以上两种情况，我们就会发现，内存泄漏的发生跟ThreadLocalMap中的key是否使用弱引用是没有关系的。那么内存泄漏的的真正原因是什么呢？

在以上两种内存泄漏的情况中，都有两个前提：

* 没有手动删除这个Entry
* CurrentThread依然运行

第一点很好理解，只要在使用完ThreadLocal，调用其remove方法删除对应的Entry，就能避免内存泄漏。

第二点稍微复杂一点，由于ThreadLocalMap是Thread的一个属性，被当前线程所引用，所以它的生命周期跟Thread一样长。那么在使用完ThreadLocal的使用，如果当前Thread也随之执行结束，ThreadLocalMap自然也会被gc回收，从根源上避免了内存泄漏。

综上，ThreadLocal内存泄漏的根源是：由于ThreadLocalMap的生命周期跟Thread-样长，如果没有手动删除对应key就会导致内存泄漏。

**为什么要使用弱引用？**

根据刚才的分析，我们知道了：无论ThreadLocalMap中的key使用哪种类型引用都无法完全避免内存泄漏，跟使用弱引用没有关系。

要避免内存泄漏有两种方式：

* 使用完ThreadLocal，调用其remove方法删除对应的Entry
* 使用完ThreadLocal，当前Thread也随之运行结束

相对第一种方式，第二种方式显然更不好控制，特别是使用线程池的时候，线程结束是不会销毁的，而是接着放入了线程池中。

也就是说，只要记得在使用完ThreadLocal及时的调用remove，无论key是强引用还是弱引用都不会有问题。 那么为什么key要用弱引用呢？

事实上，在ThreadLocalMap中的 set / getEntry方法中，会对key为null（也即是ThreadLocal为null）进行判断，如果为null的话，那么是会对value置为nul的。

这就意味着使用完ThreadLocal，CurrentThread依然运行的前提下，就算忘记调用remove方法，弱引用比强引用可以多一层保障：弱引用的ThreadLocal会被回收，对应的value在下一次ThreadLocalMap调用set，get，remove中的任一方法的时候会被清除，从而避免内存泄漏。

**ThreadLocal使用场景**

ThreadLocal的作用主要是做数据隔离，填充的数据只属于当前线程，变量的数据对别的线程而言是相对隔离的，在多线程环境下，如何防止自己的变量被其它线程篡改。

例如，用于 Spring实现事务隔离级别的源码

Spring采用Threadlocal的方式，来保证单个线程中的数据库操作使用的是同一个数据库连接，同时，采用这种方式可以使业务层使用事务时不需要感知并管理connection对象，通过传播级别，巧妙地管理多个事务配置之间的切换，挂起和恢复。

Spring框架里面就是用的ThreadLocal来实现这种隔离，主要是在TransactionSynchronizationManager这个类里面，代码如下所示:

```java
private static final Log logger = LogFactory.getLog(TransactionSynchronizationManager.class);

private static final ThreadLocal<Map<Object, Object>> resources =
        new NamedThreadLocal<>("Transactional resources");

private static final ThreadLocal<Set<TransactionSynchronization>> synchronizations =
        new NamedThreadLocal<>("Transaction synchronizations");

private static final ThreadLocal<String> currentTransactionName =
        new NamedThreadLocal<>("Current transaction name");
```
Spring的事务主要是ThreadLocal和AOP去做实现的，我这里提一下，大家知道每个线程自己的链接是靠ThreadLocal保存的就好了。

除了源码里面使用到ThreadLocal的场景，还有其它的使用场景么？

>之前我们上线后发现部分用户的日期居然不对了，排查下来是SimpleDataFormat的锅，当时我们使用SimpleDataFormat的parse()方法，内部有一个Calendar对象，调用SimpleDataFormat的parse()方法会先调用Calendar.clear（），然后调用Calendar.add()，如果一个线程先调用了add()然后另一个线程又调用了clear()，这时候parse()方法解析的时间就不对了。其实要解决这个问题很简单，让每个线程都new 一个自己的 SimpleDataFormat就好了，但是1000个线程难道new1000个SimpleDataFormat？所以当时我们使用了线程池加上ThreadLocal包装SimpleDataFormat，再调用initialValue让每个线程有一个SimpleDataFormat的副本，从而解决了线程安全的问题，也提高了性能。

>我在项目中存在一个线程经常遇到横跨若干方法调用，需要传递的对象，也就是上下文（Context），它是一种状态，经常就是是用户身份、任务信息等，就会存在过渡传参的问题。使用到类似责任链模式，给每个方法增加一个context参数非常麻烦，而且有些时候，如果调用链有无法修改源码的第三方库，对象参数就传不进去了，所以我使用到了ThreadLocal去做了一下改造，这样只需要在调用前在ThreadLocal中设置参数，其他地方get一下就好了。很多场景的cookie，session等数据隔离都是通过ThreadLocal去做实现的。