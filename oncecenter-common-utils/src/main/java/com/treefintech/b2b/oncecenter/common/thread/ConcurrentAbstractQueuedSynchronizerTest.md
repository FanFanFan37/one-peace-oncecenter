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

线程抢锁：
```java
static final class FairSync extends Sync {
    private static final long serialVersionUID = -3000897897090466540L;

    final void lock() {
        acquire(1);
    }
    /**
     * 如果tryAcquire(arg) 返回true, 也就结束了。
     * 否则，acquireQueued方法会将线程压到队列中
     **/
    public final void acquire(int arg) {
        if (!tryAcquire(arg) 
           /**
            * 将线程放置在队列中
            **/
            && acquireQueued(addWaiter(Node.EXCLUSIVE), arg)) {
                selfInterrupt();
        }
    }
    
    /**
     * Fair version of tryAcquire.  Don't grant access unless
     * recursive call or no waiters or is first.
     */
    /**
     * 尝试直接获取锁，返回值是boolean，代表是否获取到锁
     * 返回true：1.没有线程在等待锁；2.重入锁，线程本来就持有锁，也就可以理所当然可以直接获取
     **/
    protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        /**
         * getState() == 0 表示当前没有线程获得锁
         * 但是由于是公平锁需要讲究先来后到原则，
         * 判断同步队列中是否还有节点
         **/
        if (c == 0) {
            if (!hasQueuedPredecessors() &&
                /**
                 * 如果同步队列中没有其他节点，那就用CAS尝试一下，成功了就获取到锁了，
                 * 不成功的话，只能说明一个问题，就在刚刚几乎同一时刻有个线程抢先了
                 * 因为刚刚还没人的，我判断过了
                 **/
                compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        /**
         * 如果 当前线程 == 持有锁的线程，因为可以重入锁，所以 state + 1
         **/
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0) {
                throw new Error("Maximum lock count exceeded");
            }
            setState(nextc);
            return true;
        }
        /**
         * 到这里，说明没有获取到锁
         * 
         * 返回到外面的方法，将线程放入到 同步队列中
         **/
        return false;
    }
    
    
    /**
     * 因为tryAcquire(arg) 失败，所以将节点放入到同步队列的尾节点
     **/
    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    } 
    
    
   /**
    * Inserts node into queue, initializing if necessary. See picture above.
    * @param node the node to insert
    * @return node's predecessor
    */
   // 采用自旋的方式入队
   // 之前说过，到这个方法只有两种可能：等待队列为空，或者有线程竞争入队，
   // 自旋在这边的语义是：CAS设置tail过程中，竞争一次竞争不到，我就多次竞争，总会排到的
    private Node enq(final Node node) {
        for (;;) {
           Node t = tail;
           // 之前说过，队列为空也会进来这里
           if (t == null) { // Must initialize
               // 初始化head节点
               // 细心的读者会知道原来 head 和 tail 初始化的时候都是 null 的
               // 还是一步CAS，你懂的，现在可能是很多线程同时进来呢
               if (compareAndSetHead(new Node())) {
                   // 给后面用：这个时候head节点的waitStatus==0, 看new Node()构造方法就知道了
                   // 这个时候有了head，但是tail还是null，设置一下，
                   // 把tail指向head，放心，马上就有线程要来了，到时候tail就要被抢了
                   // 注意：这里只是设置了tail=head，这里可没return哦，没有return，没有return
                   // 所以，设置完了以后，继续for循环，下次就到下面的else分支了
                   tail = head;
                }
           } else {
               // 下面几行，和上一个方法 addWaiter 是一样的，
               // 只是这个套在无限循环里，反正就是将当前线程排到队尾，有线程竞争的话排不上重复排
               node.prev = t;
               if (compareAndSetTail(t, node)) {
                   t.next = node;
                   return t;
               }
           }
        }
    }
    
   
   /**
    * 下面这个方法，参数node，经过addWaiter(Node.EXCLUSIVE)，此时已经进入阻塞队列
    * 注意一下：如果acquireQueued(addWaiter(Node.EXCLUSIVE), arg))返回true的话，
    * 意味着上面这段代码将进入selfInterrupt()，所以正常情况下，下面应该返回false
    * 这个方法非常重要，应该说真正的线程挂起，然后被唤醒后去获取锁，都在这个方法里了
    **/
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                // p == head 说明当前节点虽然进到了阻塞队列，但是是阻塞队列的第一个，因为它的前驱是head
                // 注意，阻塞队列不包含head节点，head一般指的是占有锁的线程，head后面的才称为阻塞队列
                // 所以当前节点可以去试抢一下锁
                // 这里我们说一下，为什么可以去试试：
                // 首先，它是队头，这个是第一个条件，其次，当前的head有可能是刚刚初始化的node，
                // enq(node) 方法里面有提到，head是延时初始化的，而且new Node()的时候没有设置任何线程
                // 也就是说，当前的head不属于任何一个线程，所以作为队头，可以去试一试，
                // tryAcquire已经分析过了, 忘记了请往前看一下，就是简单用CAS试操作一下state
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                // 到这里，说明上面的if分支没有成功，要么当前node本来就不是队头，
                // 要么就是tryAcquire(arg)没有抢赢别人，继续往下看
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        }finally {
            // 什么时候 failed 会为 true???
            // tryAcquire() 方法抛异常的情况
            if (failed) {
                cancelAcquire(node);
            }
        }
    }
    
    
    /**
     * Checks and updates status for a node that failed to acquire.
     * Returns true if thread should block. This is the main signal
     * control in all acquire loops.  Requires that pred == node.prev.
     *
     * @param pred node's predecessor holding status
     * @param node the node
     * @return {@code true} if thread should block
     */
   /**
    * 前驱节点 pred.waitStatus 是依赖后继节点设置的，现在还没有进行设置，所以 前驱节点 pred.waitStatus = 0
    * 所以正常情况下，该方法返回 false
    */
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
       /**
        * pred.waitStatus = Node.SIGNAL 说明前驱节点正常，需要挂起。
        **/
        if (ws == Node.SIGNAL) {
            /*
             * This node has already set status asking a release
             * to signal it, so it can safely park.
             */
            return true;
        }
       /**
        * pred.waitStatus > 0, 表名前驱节点放弃争抢锁，所以往前找正常的前驱节点
        **/
        if (ws > 0) {
            /*
             * Predecessor was cancelled. Skip over predecessors and
             * indicate retry.
             */
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            /*
             * waitStatus must be 0 or PROPAGATE.  Indicate that we
             * need a signal, but don't park yet.  Caller will need to
             * retry to make sure it cannot acquire before parking.
             */
           /**
            * 因为 node 刚进入到同步队列，pred.waitStatus = 0 需要设置状态为 Node.SIGNAL
            */
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }
    
    
    
    // private static boolean shouldParkAfterFailedAcquire(Node pred, Node node)
    // 这个方法结束根据返回值我们简单分析下：
    // 如果返回true, 说明前驱节点的waitStatus==-1，是正常情况，那么当前线程需要被挂起，等待以后被唤醒
    //        我们也说过，以后是被前驱节点唤醒，就等着前驱节点拿到锁，然后释放锁的时候叫你好了
    // 如果返回false, 说明当前不需要被挂起，为什么呢？往后看

    // 跳回到前面是这个方法
    // if (shouldParkAfterFailedAcquire(p, node) &&
    //                parkAndCheckInterrupt())
    //                interrupted = true;

    // 1. 如果shouldParkAfterFailedAcquire(p, node)返回true，
    // 那么需要执行parkAndCheckInterrupt():

    // 这个方法很简单，因为前面返回true，所以需要挂起线程，这个方法就是负责挂起线程的
    // 这里用了LockSupport.park(this)来挂起线程，然后就停在这里了，等待被唤醒=======
   /**
    * 能执行到这里，就说明 shouldParkAfterFailedAcquire() 方法有问题，需要中断挂起。
    **/
    private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }

    // 2. 接下来说说如果shouldParkAfterFailedAcquire(p, node)返回false的情况
    // 仔细看shouldParkAfterFailedAcquire(p, node)，我们可以发现，其实第一次进来的时候，一般都不会返回true的，原因很简单，前驱节点的waitStatus=-1是依赖于后继节点设置的。也就是说，我都还没给前驱设置-1呢，怎么可能是true呢，但是要看到，这个方法是套在循环里的，所以第二次进来的时候状态就是-1了。
    // 解释下为什么shouldParkAfterFailedAcquire(p, node)返回false的时候不直接挂起线程：
    // => 是为了应对在经过这个方法后，node已经是head的直接后继节点了。剩下的读者自己想想吧。
}
```




解锁操作：
```java
public abstract class AbstractQueuedSynchronizer {
// 唤醒的代码还是比较简单的，你如果上面加锁的都看懂了，下面都不需要看就知道怎么回事了
public void unlock() {
    sync.release(1);
}

public final boolean release(int arg) {
    // 往后看吧
    if (tryRelease(arg)) {
        Node h = head;
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}

// 回到ReentrantLock看tryRelease方法
protected final boolean tryRelease(int releases) {
    int c = getState() - releases;
    if (Thread.currentThread() != getExclusiveOwnerThread())
        throw new IllegalMonitorStateException();
    // 是否完全释放锁
    boolean free = false;
    // 其实就是重入的问题，如果c==0，也就是说没有嵌套锁了，可以释放了，否则还不能释放掉
    if (c == 0) {
        free = true;
        setExclusiveOwnerThread(null);
    }
    setState(c);
    return free;
}

/**
 * Wakes up node's successor, if one exists.
 *
 * @param node the node
 */
// 唤醒后继节点
// 从上面调用处知道，参数node是head头结点
private void unparkSuccessor(Node node) {
    /*
     * If status is negative (i.e., possibly needing signal) try
     * to clear in anticipation of signalling.  It is OK if this
     * fails or if status is changed by waiting thread.
     */
    int ws = node.waitStatus;
    // 如果head节点当前waitStatus<0, 将其修改为0
    if (ws < 0)
        compareAndSetWaitStatus(node, ws, 0);
    /*
     * Thread to unpark is held in successor, which is normally
     * just the next node.  But if cancelled or apparently null,
     * traverse backwards from tail to find the actual
     * non-cancelled successor.
     */
    // 下面的代码就是唤醒后继节点，但是有可能后继节点取消了等待（waitStatus==1）
    // 从队尾往前找，找到waitStatus<=0的所有节点中排在最前面的
    Node s = node.next;
    if (s == null || s.waitStatus > 0) {
        s = null;
        // 从后往前找，仔细看代码，不必担心中间有节点取消(waitStatus==1)的情况
        for (Node t = tail; t != null && t != node; t = t.prev)
            if (t.waitStatus <= 0)
                s = t;
    }
    if (s != null)
        // 唤醒线程
        LockSupport.unpark(s.thread);
}

//唤醒线程以后，被唤醒的线程将从以下代码中继续往前走：
  

private final boolean parkAndCheckInterrupt() {
    LockSupport.park(this); // 刚刚线程被挂起在这里了
    return Thread.interrupted();
}
// 又回到这个方法了：acquireQueued(final Node node, int arg)，这个时候，node的前驱是head了

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


(二.1.await())condition.await() 方法详解，将线程放入到条件队列中。
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
        
        /**
        * interruptMode 可以取值为 REINTERRUPT（1），THROW_IE（-1），0 。
          1、如果在 signal 之后中断, REINTERRUPT（1）： 代表 await 返回的时候，需要重新设置中断状态
          2、如果在 signal 之前已经中断, THROW_IE（-1）： 代表 await 返回的时候，需要抛出 InterruptedException 异常
          3、0 ：说明在 await 期间，没有发生中断
          
          有以下三种情况会让 LockSupport.park(this); 这句返回继续往下执行：
          
          1、常规路径。signal -> 转移节点到阻塞队列 -> 获取了锁（unpark）
          2、线程中断。在 park 的时候，另外一个线程对这个线程进行了中断
          3、signal 的时候我们说过，转移以后的前驱节点取消了，或者对前驱节点的CAS操作失败了
          4、假唤醒。这个也是存在的，和 Object.wait() 类似，都有这个问题
        **/
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
           
            
           /**
            * 根据 condition.signal() 方法，将条件队列中的节点转移到同步队列中。
            * 只要同步队列中的节点获取到锁，即可执行一下代码。
            **/
           
            if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) {
                break;
            }
        }
        
        /**
        * 由于 while 出来后，我们确定节点已经进入了阻塞队列，准备获取锁。
        * 这里的 acquireQueued(node, savedState) 的第一个参数 node 之前已经经过 enq(node) 进入了队列，
        * 参数 savedState 是之前释放锁前的 state，这个方法返回的时候，代表当前线程获取了锁，而且 state == savedState了。
        * 
        * 注意，前面我们说过，不管有没有发生中断，都会进入到阻塞队列，
        * 而 acquireQueued(node, savedState) 的返回值就是代表线程是否被中断。如果返回 true，说明被中断了，
        * 而且 interruptMode != THROW_IE，说明在 signal 之前就发生中断了，这里将 interruptMode 设置为 REINTERRUPT，用于待会重新中断。
        **/
        if (acquireQueued(node, savedState) && interruptMode != THROW_IE) {
            interruptMode = REINTERRUPT;
        }
        
        /**
        * signal 执行完成，node.nextWaiter = null,
        * 若在 signal 之前中断，只执行了 checkInterruptWhileWaiting 方法中调用 enq(node) 将节点加入到 同步队列，而没有设置 node.nextWaiter = null
        * 
        * 这边需要将 waitStatus != Node.CONDITION 的节点移出 条件队列。
        **/
        if (node.nextWaiter != null) { // clean up if cancelled
            unlinkCancelledWaiters();
        }
        
        /**
        * 0：什么都不做，没有被中断过；
        * THROW_IE：await 方法抛出 InterruptedException 异常，因为它代表在 await() 期间发生了中断；
        * REINTERRUPT：重新中断当前线程，因为它代表 await() 期间没有被中断，而是 signal() 以后发生的中断
        **/
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


检查中断状态
```java
public abstract class AbstractQueuedSynchronizer {
    /**
     * Checks for interrupt, returning THROW_IE if interrupted
     * before signalled, REINTERRUPT if after signalled, or
     * 0 if not interrupted.
     */
    /**
     * 1. 如果在 signal 之前已经中断，返回 THROW_IE，代表 await 返回的时候，需要抛出 InterruptedException 异常
     * 2. 如果是 signal 之后中断，返回 REINTERRUPT，代表 await 返回的时候，需要重新设置中断状态
     * 3. 没有发生中断，返回 0
     **/
    private int checkInterruptWhileWaiting(Node node) {
        return Thread.interrupted() ? (transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT) : 0;
    }
    
    
    /**
     * Transfers node, if necessary, to sync queue after a cancelled wait.
     * Returns true if thread was cancelled before being signalled.
     *
     * @param node the node
     * @return true if cancelled before the node was signalled
     */
    /**
     * 只有线程处于中断状态，才会调用此方法
     * 如果需要的话，将这个已经取消等待的节点转移到阻塞队列
     * 返回 true：如果此线程在 signal 之前被取消，
     **/
    final boolean transferAfterCancelledWait(Node node) {
        /**
        * 用 CAS 将节点状态从 Node.CONDITION 设置为 0 
        * 如果这步 CAS 成功，说明是 signal 方法之前发生的中断，
        * 因为 signal 之前节点是在条件队列中，waitStatus = Node.CONDITION, 
        * 并且 signal 先发生的话，signal 中会将 waitStatus 设置为 0 
        **/
        if (compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
            /**
            * CAS成功，说明在signal方法之前发生中断，需要执行自旋操作（ signal 中也用到类似的操作），将节点从条件队列--->同步队列
            **/
            enq(node);
            return true;
        }
        /*
         * If we lost out to a signal(), then we can't proceed
         * until it finishes its enq().  Cancelling during an
         * incomplete transfer is both rare and transient, so just
         * spin.
         */
        /**
        * 到这里是因为 CAS 失败，肯定是因为 signal 方法已经将 waitStatus 设置为了 0
        * signal 方法会将节点转移到阻塞队列，但是可能还没完成，这边自旋等待其完成
        * 当然，这种事情还是比较少的吧：signal 调用之后，没完成转移之前，发生了中断
        **/
        while (!isOnSyncQueue(node)) {
            /**
            * yield 即 “谦让”，也是 Thread 类的方法。
            * 它让掉当前线程 CPU 的时间片，使正在运行中的线程重新变成就绪状态，
            * 并重新竞争 CPU 的调度权。它可能会获取到，也有可能被其他线程获取到。
            * 
            * 使得 signal 的线程执行完成，将节点转移到同步队列中。
            **/
            Thread.yield();
        }
        return false;
    }
}
```



(二.2.signal())signal() 唤醒等待最久的线程，转移到同步队列。
等待同步队列中的节点获取到锁，即可执行 await()方法中的下半段。
```java
public abstract class AbstractQueuedSynchronizer {
    /**
    * 让条件队列中的节点，进入到同步队列。
    * 等待同步队列中的节点获取到锁，即可执行 await()方法中的下半段。
    **/
    public final void signal() {
        /**
        * isHeldExclusively() 判断当前线程是否拥有独占锁。
        **/
        if (!isHeldExclusively()) {
            throw new IllegalMonitorStateException();
        }
        /**
        * 唤醒条件队列的头结点
        **/
        Node first = firstWaiter;
        if (first != null) {
            doSignal(first);
        }
    }    
    
    /**
    * 条件队列从前往后遍历，找出第一个需要转移的节点。
    * 因为有些线程已经取消排队，但是有可能还在条件队列中。
    **/
    private void doSignal(Node first) {
        do {
            /**
            * 因为 firstwaiter 需要转移到同步队列中，所以要将 firstwaiter 指向当前firstwaiter的下一个节点。
            * 若firstwaiter的下一个节点为null, 则需要将lastWaiter置为null
            **/
            if ( (firstWaiter = first.nextWaiter) == null) {
                lastWaiter = null;
            }
            /**
            * 因为first节点需要转移到同步队列，所以需要与条件队列断绝关系。
            **/
            first.nextWaiter = null;
        } while (!transferForSignal(first) && (first = firstWaiter) != null);
    }
    
    /**
    * 将first节点放入到同步队列中。
    **/
    final boolean transferForSignal(Node node) {
        /*
         * If cannot change waitStatus, the node has been cancelled.
         */
        /**
        * CAS操作，将node.state 从 Node.CONDITION 设置为 0
        * 若CAS失败，则表示node.state != Node.CONDITION，说明该节点已经被取消。
        **/
        if (!compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
            return false;
        }
        /*
         * Splice onto queue and try to set waitStatus of predecessor to
         * indicate that thread is (probably) waiting. If cancelled or
         * attempt to set waitStatus fails, wake up to resync (in which
         * case the waitStatus can be transiently and harmlessly wrong).
         */
        /**
        * 使用自旋方式，将node 加入到同步队列中
        * Node p = enq(node); 返回的 p 表示之前的同步队列尾节点tail。
        **/
        Node p = enq(node);
        int ws = p.waitStatus;
        /**
        * p.waitStatus > 0 表示 p.waitStatus = 1 (CANCELLED) ,node的前置节点取消争抢锁，所以直接将node节点唤醒。
        * compareAndSetWaitStatus(p, ws, Node.SIGNAL) = false, node的前置节点CAS失败，直接将node节点唤醒，原因看下一节。
        * 
        * 
        * 正常情况下，ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL) 这句中，
        * ws <= 0，而且 compareAndSetWaitStatus(p, ws, Node.SIGNAL) 会返回 true，
        * 所以一般也不会进去 if 语句块中唤醒 node 对应的线程。然后这个方法返回 true，也就意味着 signal 方法结束了，节点进入了阻塞队列。
        **/
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL)) {
            /**
            * 唤醒线程
            **/
            LockSupport.unpark(node.thread);
        }
         return true;
    }
}
```



带超时机制的 await :
超时的思路还是很简单的，不带超时参数的 await 是 park，然后等待别人唤醒。
而现在就是调用 parkNanos 方法来休眠指定的时间，醒来后判断是否 signal 调用了，调用了就是没有超时，否则就是超时了。超时的话，自己来进行转移到阻塞队列，然后抢锁。
```java
public interface Condition {
    long awaitNanos(long nanosTimeout) throws InterruptedException;
    boolean await(long time, TimeUnit unit) throws InterruptedException;
    boolean awaitUntil(Date deadline) throws InterruptedException;
}



public abstract class AbstractQueuedSynchronizer {
    
    /**
     * Implements timed condition wait.
     * <ol>
     * <li> If current thread is interrupted, throw InterruptedException.
     * <li> Save lock state returned by {@link #getState}.
     * <li> Invoke {@link #release} with saved state as argument,
     *      throwing IllegalMonitorStateException if it fails.
     * <li> Block until signalled, interrupted, or timed out.
     * <li> Reacquire by invoking specialized version of
     *      {@link #acquire} with saved state as argument.
     * <li> If interrupted while blocked in step 4, throw InterruptedException.
     * <li> If timed out while blocked in step 4, return false, else true.
     * </ol>
     */
    public final boolean await(long time, TimeUnit unit) throws InterruptedException {
       /**
        * 等待时间
        **/
        long nanosTimeout = unit.toNanos(time);
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Node node = addConditionWaiter();
        int savedState = fullyRelease(node);
       /**
        * 过期时间 = 当前时间 + 等待时间
        */
        final long deadline = System.nanoTime() + nanosTimeout;
       /**
        * 超时状态
        **/
        boolean timedout = false;
        int interruptMode = 0;
        while (!isOnSyncQueue(node)) {
           
            if (nanosTimeout <= 0L) {
               /**
                * 这里因为要 break 取消等待了。取消等待的话一定要调用 transferAfterCancelledWait(node) 这个方法
                * 如果这个方法返回 true，在这个方法内，将节点转移到阻塞队列成功
                * 返回 false 的话，说明 signal 已经发生，signal 方法将节点转移了。也就是说没有超时嘛
                **/
                timedout = transferAfterCancelledWait(node);
                break;
            }
            
           /**
            * spinForTimeoutThreshold 的值是 1000 纳秒，也就是 1 毫秒
            * 也就是说，如果不到 1 毫秒了，那就不要选择 parkNanos 了，自旋的性能反而更好
            **/
            if (nanosTimeout >= spinForTimeoutThreshold) {
                LockSupport.parkNanos(this, nanosTimeout);
            }
            if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) {
                break;
            }
            
           /**
            * 得到剩余时间
            **/
            nanosTimeout = deadline - System.nanoTime();
        }
        if (acquireQueued(node, savedState) && interruptMode != THROW_IE) {
            interruptMode = REINTERRUPT;
        }
        if (node.nextWaiter != null) {
            unlinkCancelledWaiters();
        }
        if (interruptMode != 0) {
            reportInterruptAfterWait(interruptMode);
        }
        return !timedout;
    }
}
```


AbstractQueuedSynchronizer 独占锁的取消排队:





（三） 线程中断和 InterruptedException 异常

中断：程序在执行过程中，发生了某些非正常事件指示当前进程不能继续执行，应得到暂停或者终止，从而通知正在执行的进程暂停执行的操作。
中断是实现并发的基础，中断一个线程的执行，调度另一个线程执行。

##--------------------------------------------------------------------------------------------------

