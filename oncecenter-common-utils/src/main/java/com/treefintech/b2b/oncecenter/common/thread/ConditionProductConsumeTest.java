package com.treefintech.b2b.oncecenter.common.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition 生产者---消费者模式
 * @author zhangfan
 * @description
 * @date 2019/7/30 上午10:47
 **/
public class ConditionProductConsumeTest {

    static final Lock lock = new ReentrantLock();

    static final Condition notFull = lock.newCondition();
    static final Condition notEmpty = lock.newCondition();

    static final Object[] items = new Object[100];
    private static int putptr, takeptr, count;
    private static int number;

    private static void put(Object object) throws InterruptedException {
        lock.lock();

        try{
            while (count == items.length) {
                notFull.await();
            }
            items[putptr] = object;
            if (++putptr == items.length) {
                putptr = 0;
            }
            ++count;
            System.out.println("put = " + object + ", count = " + count);
            notEmpty.signal();
        }finally {
            lock.unlock();
        }
    }

    private static Object take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            Object object = items[takeptr];
            if (++takeptr == items.length) {
                takeptr = 0;
            }
            --count;
            System.out.println("take = " + object + ", count = " + count);
            notFull.signal();
            return object;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws Exception{
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), r -> {
            Thread thread = new Thread(r);
            thread.setName("测试thread");
            return thread;
        });

        executorService.execute(() -> {
            while (true) {
                number++;
                try {
                    put(number);
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    System.out.println("put is error = " + e.getMessage());
                }

            }
        });

        executorService.execute(() -> {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    take();
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    System.out.println("take is error = " + e.getMessage());
                }
            }
        });
    }
}
