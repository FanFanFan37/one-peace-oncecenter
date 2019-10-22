package com.treefintech.b2b.oncecenter.common.thread;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author zhangfan
 * @description
 * @date 2019/10/21 下午8:22
 **/
public class ForkJoinTest {

    //获取逻辑处理器数量
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    static long calcSum;

    public static void main(String[] args) throws Exception {
        int[] array = buildRandomIntArray(20000000);
        System.out.println("cpu­num:" + NCPU);

        //单线程下计算数组数据总和
        calcSum = seqSum(array);
        System.out.println("seq sum=" + calcSum);

        //采用fork/join方式将数据求和任务进行拆分，最后合并结果
        LongSum ls = new LongSum(array, 0, array.length);
        //使用的线程数
        ForkJoinPool fjp  = new ForkJoinPool(4);
        ForkJoinTask<Long> result = fjp.submit(ls);
        System.out.println("fork join sum=" + result.get());

        Thread.sleep(1000L);
        System.out.println("fork join error=" + result.getException());

        fjp.shutdown();
    }
    static long seqSum(int[] array) {
        long sum = 0;
        for (int i = 0; i < array.length; ++i) {
            sum += array[i];
        }
        return sum;
    }


    private static int[] buildRandomIntArray(Integer count) {
        Random random = new Random();
        int[] arrays = new int[count];
        for (int i = 0; i < count; i++) {
            arrays[i] = random.nextInt(100);
        }
        return arrays;
    }



}



class LongSum extends RecursiveTask<Long> {
    static final int SEQUENTIAL_THRESHOLD = 1000;

    int low;
    int high;
    int[] array;
    LongSum(int[] arr, int lo, int hi) {
        array = arr;
        low = lo;
        high = hi;
    }

    @Override
    protected Long compute() {
        //任务被拆分到足够小时，则开始求和
        if ((high - low) <= SEQUENTIAL_THRESHOLD) {
            long sum = 0;
            for (int i = low; i < high; ++i) {
                sum += array[i];
            }
            return sum;
        } else {
            //如果任务任然过大，则继续拆分任务，本质就是递归拆分
            int mid = low + (high - low) / 2;
            LongSum left = new LongSum(array, low, mid);
            LongSum right = new LongSum(array, mid, high);
            left.fork();
            right.fork();
            long rightAns = right.join();
            long leftAns = left.join();
            return leftAns + rightAns;
        }
    }
}
