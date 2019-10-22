package com.treefintech.b2b.oncecenter.common.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基本原子操作
 *
 * @author zhangfan
 * @description
 * @date 2019/8/23 下午5:45
 **/
public class AtomicTest {

    public static void main(String[] args) {

        final AtomicInteger[] count = {new AtomicInteger(0)};
        ExecutorService executorService = new ThreadPoolExecutor(5, 10, 1000L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), (ThreadFactory) Thread::new);

        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < 10; j++) {
                    System.out.println(count[0].incrementAndGet());
                }
            });
        }

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(count[0].get());
        executorService.shutdown();
    }
}
