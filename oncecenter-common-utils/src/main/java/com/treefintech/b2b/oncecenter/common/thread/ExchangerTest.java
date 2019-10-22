package com.treefintech.b2b.oncecenter.common.thread;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 交换者
 * 当成对的线程使用exchange()方法到达同步点，就会进行交换数据。
 * 一定要是成对的线程，若不是成对的线程，则剩下的单个线程就会被阻塞。
 *
 * @author zhangfan
 * @description
 * @date 2019/8/20 下午8:23
 **/
public class ExchangerTest {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Exchanger<String> exchanger = new Exchanger<>();

        for (int i = 1; i <= 5; i++) {
            int finalI = i;
            executorService.execute(() -> exchangerTrade(String.valueOf(finalI), exchanger));
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }

    public static void exchangerTrade(String str, Exchanger<String> exchanger) {
        System.out.println(Thread.currentThread().getName() + "把 " + str + " 交易出去");
        String getThing = "";
        try {
            getThing = exchanger.exchange(str);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "得到 " + getThing);
    }
}
