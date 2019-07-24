package com.treefintech.b2b.oncecenter.common.thread;

import com.treefinance.b2b.common.concurrent.ExecutorServiceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 * Semaphore 信号灯
 * 用于控制最大并发线程数。
 * 当设置最大并发线程数为1时，则可以作为同步锁。
 *
 * @author zhangfan
 * @description
 * @date 2019/5/24 下午3:27
 **/
public class SemaphoreTest {

    public static void main(String[] args) throws Exception {
        SemaphoreTest semaphoreTest = new SemaphoreTest();
        semaphoreTest.execute();
    }

    private void execute() throws Exception {
        final Semaphore semaphore = new Semaphore(6);

        List<Future> futureList = new ArrayList<>();
        //模拟20个线程，需要获取数据库连接
        for (int i = 0; i < 20; i++) {
            Future future = ExecutorServiceUtils.submit(() -> {
                saveDate(semaphore, String.valueOf(Thread.currentThread().getId()));
                return null;
            });
            futureList.add(future);
        }
        for (Future future : futureList) {
            future.get();
        }
    }

    private void saveDate(Semaphore semaphore, String threadId) {
        try {
            semaphore.acquire();
            System.out.println("threadId = " + threadId + ", to save");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 一定要在finally中释放信号量。
            semaphore.release();
        }
    }
}

