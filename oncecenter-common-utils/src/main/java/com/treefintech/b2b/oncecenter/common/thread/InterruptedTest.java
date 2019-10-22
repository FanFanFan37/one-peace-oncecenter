package com.treefintech.b2b.oncecenter.common.thread;

/**
 * 中断机制
 *
 * @author zhangfan
 * @description
 * @date 2019/8/20 下午9:04
 **/
public class InterruptedTest {

    public static void main(String[] args) {

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println(thread.getState());
        thread.start();
        System.out.println(thread.getState());
        thread.interrupt();
        System.out.println(thread.getState());

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(thread.getState());
        System.out.println(thread.isInterrupted());
        System.out.println(thread.getState());
    }
}
