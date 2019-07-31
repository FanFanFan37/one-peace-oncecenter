package com.treefintech.b2b.oncecenter.common.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch  门栓
 * 相当于是一个门槛，可以初始化CountDownLatch设置为N个任务，分为多个线程处理，每一个任务只有都调用 downLatch.countDown(); 才能算成该任务已结束。
 * 调用 downLatch.await(); 方法可以让主线程阻塞，等待每一个任务完成之后，再执行主线程任务。
 *
 * @author zhangfan
 * @description
 * @date 2019/7/31 上午11:08
 **/
public class CountDownLatchFirstTest {

    public static void main(String[] args) throws InterruptedException {
        Integer count = 5;
        CountDownLatch downLatch = new CountDownLatch(count);
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), (ThreadFactory) Thread::new);

        for (int i = 1; i < count; i++) {
            Thread.sleep(1000L);
            executorService.execute(new WorkerRunnable(downLatch, i));
        }

        downLatch.await();

        System.out.println("main is run ");

    }

    static class WorkerRunnable implements Runnable {

        private final CountDownLatch downLatch;

        private final Integer integer;

        WorkerRunnable(CountDownLatch downLatch, Integer integer) {
            this.downLatch = downLatch;
            this.integer = integer;
        }

        @Override
        public void run() {
            doWork(integer);

            System.out.println("no = " + integer);
            downLatch.countDown();
            System.out.println("no = " + integer + ", downLatch count = " + downLatch.getCount());
        }
    }

    private static void doWork(Integer integer) {
//        System.out.println(integer);
    }

}
