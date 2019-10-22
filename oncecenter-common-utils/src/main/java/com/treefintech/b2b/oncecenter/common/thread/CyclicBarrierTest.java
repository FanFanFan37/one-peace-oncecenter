package com.treefintech.b2b.oncecenter.common.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 循环栅栏，每一个子线程到达屏障都会被阻塞，
 * parties数量的子线程全部到达屏障之后，才能继续执行。
 *
 * 当屏障阻塞数量设置为 5，现在有7个线程会到达屏障。
 * 结果：前5个到达屏障的线程，在第5个线程到达之后，继续执行。剩下2个线程达到屏障之后阻塞，因为不满5，所以不能继续执行。
 *
 * @author zhangfan
 * @description
 * @date 2019/8/20 下午7:44
 **/
public class CyclicBarrierTest {

    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5, () -> {
            System.out.println("cyclicBarrier is run");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("cyclicBarrier is sleep finish");
        });

        ExecutorService executorService = new ThreadPoolExecutor(10, 20, 5000L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), (ThreadFactory) Thread::new);
        for (int i = 0; i < 7; i++) {
            executorService.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " 已到达同步点");
                try {
                    cyclicBarrier.await();
                } catch (Exception e) {
                    System.out.println("cyclicBarrier is error = " + e.getMessage());
                }
                System.out.println(Thread.currentThread().getName() + " 已离开同步点");
            });
        }

    }
}
