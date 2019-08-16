package com.treefintech.b2b.oncecenter.common.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangfan
 * @description
 * @date 2019/7/24 下午8:49
 *
 *
 * volatile 用于多线程共用。
 *
 **/
public class VolatileTest {

    public static void main(String[] args) {
        ExecutorService executorService = new ThreadPoolExecutor(10, 20, 5L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), (ThreadFactory) Thread::new);
//        VolatileVO volatileVO = new VolatileVO();
//        executorService.execute(volatileVO::load);
//        try {
//            Thread.sleep(10000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        executorService.execute(volatileVO::save);

        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < 10; j++) {
                    VolatileVO.add();
                }
                System.out.println(Thread.currentThread().getName() + " is end");
            });
        }

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(VolatileVO.i);
    }





}

class VolatileVO{
    private volatile Boolean number = Boolean.FALSE;

    public void save() {
        this.number = Boolean.TRUE;
        String threadName = Thread.currentThread().getName();
        System.out.println("线程" + threadName + "修改number值为" + number);
    }

    public void load() {
        System.out.println("load 开始。。。");
        while (!this.number) {
            System.out.println("当前值为" + number);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String threadName = Thread.currentThread().getName();
        System.out.println("线程" + threadName + "查询到值为" + number);
    }



    public static volatile Integer i = 0;

    public static void add() {
        i++;
    }
}



