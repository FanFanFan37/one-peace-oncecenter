##此页讲述 java并发包java.util.concurrent 中的 AbstractQueuedSynchronizer 抽象类。
### 此类是 java并罚保的基础工具类，是实现 ReentrantLock、CountDownLatch、Semaphore、FutureTask 等类的基础。


问题：
1、仔细看shouldParkAfterFailedAcquire(p, node)，我们可以发现，其实第一次进来的时候，一般都不会返回true的 ？
原因很简单，前驱节点的waitStatus=-1是依赖于后继节点设置的。也就是说，我都还没给前驱设置-1呢，怎么可能是true呢，
但是要看到，这个方法是套在循环里的，所以第二次进来的时候状态就是-1了。



##---AQS阻塞队列以及唤醒线程-----------------------------------------------------------------------------------------------
https://javadoop.com/post/AbstractQueuedSynchronizer

这里有一个概念: 可重入锁、不可重入锁。

AbstractQueuedSynchronizer 的四个属性：
```java
public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements java.io.Serializable {
    // 头结点，你直接把它当做 当前持有锁的线程 可能是最好理解的
    private transient volatile Node head;

    // 阻塞的尾节点，每个新的节点进来，都插入到最后，也就形成了一个链表
    private transient volatile Node tail;

    // 这个是最重要的，代表当前锁的状态，0代表没有被占用，大于 0 代表有线程持有当前锁
    // 这个值可以大于 1，是因为锁可以重入，每次重入都加上 1
    private volatile int state;

    // 代表当前持有独占锁的线程，举个最重要的使用例子，因为锁可以重入
    // reentrantLock.lock()可以嵌套调用多次，所以每次用这个来判断当前线程是否已经拥有了锁
    // if (currentThread == getExclusiveOwnerThread()) {state++}
    private transient Thread exclusiveOwnerThread; //继承自AbstractOwnableSynchronizer
}
```


node 数据结构：
```java
static final class Node {
    // 标识节点当前在共享模式下
    static final Node SHARED = new Node();
    // 标识节点当前在独占模式下
    static final Node EXCLUSIVE = null;

    // ======== 下面的几个int常量是给waitStatus用的 ===========
    /** waitStatus value to indicate thread has cancelled */
    // 代码此线程取消了争抢这个锁
    static final int CANCELLED =  1;
    /** waitStatus value to indicate successor's thread needs unparking */
    // 官方的描述是，其表示当前node的后继节点对应的线程需要被唤醒
    static final int SIGNAL    = -1;
    /** waitStatus value to indicate thread is waiting on condition */
    // 本文不分析condition，所以略过吧，下一篇文章会介绍这个
    static final int CONDITION = -2;
    /**
     * waitStatus value to indicate the next acquireShared should
     * unconditionally propagate
     */
    // 同样的不分析，略过吧
    static final int PROPAGATE = -3;
    // =====================================================

    // 取值为上面的1、-1、-2、-3，或者0(以后会讲到)
    // 这么理解，暂时只需要知道如果这个值 大于0 代表此线程取消了等待，
    //    ps: 半天抢不到锁，不抢了，ReentrantLock是可以指定timeouot的。。。
   /** 
    * waitStatus 代表的是后继节点的状态，
    * 所以每一个节点的 waitStatus 是由后继节点来设置的，所以 新加入到阻塞队列中的节点的waitStatus 为默认值0
    **/
    volatile int waitStatus;
    // 前驱节点的引用
    volatile Node prev;
    // 后继节点的引用
    volatile Node next;
    // 这个就是线程本尊
    volatile Thread thread;
}
```

##--------------------------------------------------------------------------------------------------
本文关注以下几点内容：（本文以及上文中，阻塞队列 = 同步队列）

（一）深入理解 ReentrantLock 公平锁和非公平锁的区别
（二）深入分析 AbstractQueuedSynchronizer 中的 ConditionObject
（三）深入理解 Java 线程中断和 InterruptedException 异常


（一）ReentrantLock 公平锁和非公平锁的区别：
ReentrantLock 在内部用了内部类 Sync 来管理锁：分为FairSync（公平锁）、NonfairSync（非公平锁）。默认使用 NonfairSync 。
总结：公平锁和非公平锁只有两处不同：

1、非公平锁在调用 lock 后，首先就会调用 CAS 进行一次抢锁，如果这个时候恰巧锁没有被占用，那么直接就获取到锁返回了。
2、非公平锁在 CAS（CompareAndSetState()） 失败后，和公平锁一样都会进入到 tryAcquire 方法，在 tryAcquire 方法中，如果发现锁这个时候被释放了（state == 0），非公平锁会直接 CAS 抢锁，但是公平锁会判断等待队列是否有线程处于等待状态，如果有则不去抢锁，乖乖排到后面。

公平锁和非公平锁就这两点区别，如果这两次 CAS 都不成功，那么后面非公平锁和公平锁是一样的，都要进入到阻塞队列等待唤醒。
相对来说，非公平锁会有更好的性能，因为它的吞吐量比较大。当然，非公平锁让获取锁的时间变得更加不确定，可能会导致在阻塞队列中的线程长期处于饥饿状态。



（二）ConditionObject
Condition 经常可以用在生产者-消费者的场景中。
Condition 是依赖于 ReentrantLock 的，不管是调用 await 进入等待还是 signal 唤醒，都必须获取到锁才能进行操作。

条件队列与同步队列关系：
![condition-2](https://www.javadoop.com/blogimages/AbstractQueuedSynchronizer-2/aqs2-2.png)

Condition 处理流程：
1、条件队列和同步队列的节点都是 Node 实例，需要将条件队列中的头结点放入在同步队列的尾节点。
2、可以使用 lock.newCondition() 方式生成多个 condition, 但是ConditionObject 只有两个属性 firstWaiter, lastWaiter.
3、每一个condition都有一个关联的条件队列，调用 condition.await() 将当期线程封装成 node 放置在条件队列中，然后阻塞，条件队列是一个单向链表。
    node 中的 Node nextWaiter 属性用于条件队列中，表示下一个waiter。
4、调用 condition.signal() 用于唤醒条件队列中的firstWaiter，将firstWaiter放入在同步队列的尾节点，等待获取锁。


condition.await() 方法详解，将线程放入到条件队列中。
```java
/**
 * await()方法是可以被中断的，不可被中断的方法是 awaitUninterruptibly()
 **/
public abstract class AbstractQueuedSynchronizer {
    public final void await() throws InterruptedException {
       /**
        * 由于await()方法是可以被中断的，所以需要判断中断状态。
        **/
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
       /**
        * addConditionWaiter() 该方法的作用是：
        * 1、将当期线程封装成一个node,并且放入到条件队列的对尾。
        * 2、将原本条件队列中 waitStatus != Node.CONDITION 的节点全部移除条件队列。
        **/
        Node node = addConditionWaiter();
       /**
        * 因为能进入 await() 方法说明都是带有锁的，需要把锁全部释放掉。
        * 
        * fullyRelease() 完全释放独占锁: 因为 ReentrantLock 是可以重入的，若该线程进行了2次 lock.lock() 操作，那么这个 node.state=2 ,
        * 这里的 await() 需要将 node.state 设置为0，然后再进入挂起状态，这样才能让别的线程获取锁。
        * 同样的这个线程被唤醒时，需要重新持有两把锁才能进行执行。
        * 
        * 若线程不持有lock.lock()，就去调用 condition.await() 
        * 它能进入条件队列，但是在这个方法中，由于它不持有锁，release(savedState) 这个方法肯定要返回 false，
        * 返回 false 的原因是(?)
        * 进入到异常分支，然后进入 finally 块设置 node.waitStatus = Node.CANCELLED，这个已经入队的节点之后会被后继的节点”请出去“。
        **/
        int savedState = fullyRelease(node);
        int interruptMode = 0;
       /**
        * 这里退出循环有两种情况，之后再仔细分析
        * 1. isOnSyncQueue(node) 返回 true，即当前 node 已经转移到同步队列了
        * 2. checkInterruptWhileWaiting(node) != 0 会到 break，然后退出循环，代表的是线程中断
        * 
        * 
        * isOnSyncQueue(node) 用于判断节点是否已经转移到 同步队列中。
        **/
        while (!isOnSyncQueue(node)) {
           /**
            * 线程挂起，等待使用 signal 唤醒线程，转移到同步队列
            **/
            LockSupport.park(this);
            
           /**
            * 上一步 LockSupport.park(this) 线程已经被挂起，下面代码不再执行
            * 等待线程 LockSupport.unpark(); 也就是使用 signal 唤醒线程，转移到同步队列
            **/
            
           
            if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) {
                break;
            }
        }
        if (acquireQueued(node, savedState) && interruptMode != THROW_IE) {
            interruptMode = REINTERRUPT;
        }
        if (node.nextWaiter != null) { // clean up if cancelled
            unlinkCancelledWaiters();
        }
        if (interruptMode != 0) {
            reportInterruptAfterWait(interruptMode);
        }
    }
}
```

判断 node 是否已经转移到同步队列中。
```java
public abstract class AbstractQueuedSynchronizer {
    final boolean isOnSyncQueue(Node node) {
        /**
        * 1、node.waitStatus == Node.CONDITION    
        * node 进入到条件队列, addConditionWaiter() 方法中，会将waitStatus = Node.CONDITION
        * 
        * 2、node.prev 
        * node.prev 在同步队列中才有值。
        **/
        if (node.waitStatus == Node.CONDITION || node.prev == null) {
            return false;
        }
        
        /**
        * node.next 在同步队列中才有值。
        * 不能用 node.prev != null 来判断 node 在同步队列中的原因是 {
        * 当node 加入到 同步队列的对尾时，会将 node.prev = tail (将自己的前置节点 设置为 当前同步队列的尾节点)
        * 然后再进行CAS操作，将自己设置为 tail, 但是这个CAS操作有可能是失败的。
        * }
        **/
        if (node.next != null) { // If has successor, it must be on queue
            return true;
        }
        /*
         * node.prev can be non-null, but not yet on queue because
         * the CAS to place it on queue can fail. So we have to
         * traverse from tail to make sure it actually made it.  It
         * will always be near the tail in calls to this method, and
         * unless the CAS failed (which is unlikely), it will be
         * there, so we hardly ever traverse much.
         */    
       /**
        * 从同步队列的队尾开始遍历
        * tail = node 方式判断是否存在。
        **/
        return findNodeFromTail(node);
    }
}
```







interruptMode 可以取值为 REINTERRUPT（1），THROW_IE（-1），0 。
1、REINTERRUPT（1）： 代表 await 返回的时候，需要重新设置中断状态
2、THROW_IE（-1）： 代表 await 返回的时候，需要抛出 InterruptedException 异常
3、0 ：说明在 await 期间，没有发生中断

有以下三种情况会让 LockSupport.park(this); 这句返回继续往下执行：

1、常规路径。signal -> 转移节点到阻塞队列 -> 获取了锁（unpark）
2、线程中断。在 park 的时候，另外一个线程对这个线程进行了中断
3、signal 的时候我们说过，转移以后的前驱节点取消了，或者对前驱节点的CAS操作失败了
4、假唤醒。这个也是存在的，和 Object.wait() 类似，都有这个问题


（三） 线程中断和 InterruptedException 异常

中断：程序在执行过程中，发生了某些非正常事件指示当前进程不能继续执行，应得到暂停或者终止，从而通知正在执行的进程暂停执行的操作。
中断是实现并发的基础，中断一个线程的执行，调度另一个线程执行。

##--------------------------------------------------------------------------------------------------

