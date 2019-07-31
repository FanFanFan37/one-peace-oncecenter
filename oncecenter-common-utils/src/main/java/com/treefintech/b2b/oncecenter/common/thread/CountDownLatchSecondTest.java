package com.treefintech.b2b.oncecenter.common.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangfan
 * @description
 * @date 2019/7/31 下午1:38
 **/
public class CountDownLatchSecondTest {

    public static void main(String[] args) {
        CountDownLatch downLatch = new CountDownLatch(2);
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5), (ThreadFactory) Thread::new);

        executorService.execute(() -> {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            downLatch.countDown();
        });

        executorService.execute(() -> {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            downLatch.countDown();
        });

        executorService.execute(() -> {
            try {
                downLatch.await();
                System.out.println("线程1 await 方法结束");
            } catch (InterruptedException e) {
                System.out.println("线程1 await 方法中断");
                Thread.currentThread().interrupt();
            }
        });

        executorService.execute(() -> {
            try {
                downLatch.await();
                System.out.println("线程2 await 方法结束");
            } catch (InterruptedException e) {
                System.out.println("线程2 await 方法中断");
                Thread.currentThread().interrupt();
            }
        });
    }
}
