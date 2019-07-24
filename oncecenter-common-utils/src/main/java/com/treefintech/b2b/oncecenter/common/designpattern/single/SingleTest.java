package com.treefintech.b2b.oncecenter.common.designpattern.single;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 单例
 * @author zhangfan
 * @description
 * @date 2019/7/9 上午9:47
 **/
public class SingleTest {

    public static void main(String[] args) {
        //饿汉式
//        HungrySingle hungrySingle = HungrySingle.getInstance();
//        HungrySingle hungrySingle1 = HungrySingle.getInstance();
//
//        System.out.println(hungrySingle == hungrySingle1);
//        HungrySingle hungrySingle2;
//        try {
//            hungrySingle2 = (HungrySingle) Class.forName("HungrySingle").newInstance();
//        } catch (Exception e) {
//            throw new ServiceException(-1, e);
//        }
//        System.out.println(hungrySingle == hungrySingle2);

        //懒汉式
//        LazySingle lazySingle = LazySingle.getInstance();
//        LazySingle lazySingle1 = LazySingle.getInstance();
//        System.out.println(lazySingle == lazySingle1);


        ExecutorService executorService = new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100), (ThreadFactory) Thread::new);
        for (int i = 0; i < 10; i++) {
            Integer finalI = i;
            executorService.execute(() -> {
                EnumSingle enumSingle = EnumSingle.INSTANCE;
                enumSingle.putMap(String.valueOf(finalI), String.valueOf(finalI));
            });
        }
    }

}