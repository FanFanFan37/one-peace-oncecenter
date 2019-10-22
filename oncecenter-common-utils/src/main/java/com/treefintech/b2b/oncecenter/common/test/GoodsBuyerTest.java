package com.treefintech.b2b.oncecenter.common.test;

import com.alibaba.fastjson.JSONObject;
import com.treefinance.b2b.common.utils.DateUtils;
import com.treefinance.b2b.common.utils.lang.NumberUtils;
import com.treefinance.b2b.common.utils.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangfan
 * @description
 * @date 2019/8/19 上午9:42
 * <p>
 * 题干：某电子商务网站要对某商品进行秒杀活动，请按照如下前提条件和场景要求编程模拟系统秒杀过程。
 * 假设：
 * 商品只有一种，所有商品已经加载到内存中。商品模型可简化为：List<{String goodsId;String buyerId;}>。goodsId是商品的唯一标识，buyerId是秒杀完成后成功秒杀到该商品的用户ID。
 * 发起秒杀的用户模型可简化为：List<{String buyerId}>。即只有唯一标识。
 * 一次秒杀动作简化为把买家的用户ID写入商品信息中，不考虑真实的下单、支付、发货...等流程
 * 要求：
 * 实现秒杀接口，模拟N个用户抢购M个商品的过程(N远大于M)，输入为：商品列表和用户列表。输出为：买家信息更新后的商品列表及耗时。
 * 一个商品被成功秒杀的定义是，该商品的buyerId字段不为空。一定保证一个商品只能被成功秒杀一次。
 * 一个用户成功秒杀到商品的定义为，该用户的buyerId已经存在于任意一件商品信息中。一定保证一个用户只能成功秒杀一次。
 * 商品被全部成功秒杀或者所有用户都秒杀到一件商品视为模拟过程结束，程序返回。
 * 不考虑存储开销，整个模拟过程耗时尽可能的短。
 * 设计风格良好的日志展示整个模拟过程。
 * 接口：
 * public class Goods{
 * String goodsId;
 * String buyerId;
 * }
 * <p>
 * public class Result{
 * List<Goods>  goodsList;
 * Long         elapsedTime;
 * }
 * <p>
 * public interface SecKill{
 * Result simulate(List<Goods> goods,List<String> userIds);
 * }
 * <p>
 * 要求：
 * 1、自选一道完成。
 * 2、时间一个小时。
 * 3、语言不限（优选JAVA）
 * 4、诚信原则，勿抄袭或求助他人
 * 5、题目没有标准答案，写出你认为的好代码即可。
 **/
public class GoodsBuyerTest {

    private static void initData(List<Goods> goodsList, List<String> buyerIdList) {
        for (int i = 1000; i < 1050; i++) {
            goodsList.add(new Goods(String.valueOf(i), ""));
        }

        for (int i = 0; i < 10000; i++) {
            buyerIdList.add(String.valueOf(i));
        }
    }


    private synchronized static void buyGoods(List<Goods> goodsList, String buyerId) {
        Goods goods = null;
        for (Goods needGoods : goodsList) {
            if (StringUtils.isBlank(needGoods.getBuyerId())) {
                goods = needGoods;
                break;
            }
        }
        if (goods == null) {
            System.out.println("buyerId = " + buyerId + ", 商品被抢购完毕");
            return;
        }
        System.out.println(buyerId + " 进来抢 " + goods.getGoodsId());
        goods.setBuyerId(goods.getBuyerId() + "_" + buyerId);
        System.out.println(buyerId + " 抢完 " + goods.getGoodsId());
    }


    public static Result simulate(List<Goods> goodsList, List<String> buyerIdList) {
        System.out.println("loading date for goodsList = " + JSONObject.toJSONString(goodsList) + ", buyIdList = " + JSONObject.toJSONString(buyerIdList));

        //创建线程池，模拟并发
        ExecutorService executorService = new ThreadPoolExecutor(10, 20, 5000L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000), (ThreadFactory) Thread::new);
        //抢购完毕，主线程立即返回结果，不管其他没抢到的线程
        Integer buyFinishCount = NumberUtils.lessThan(goodsList.size(), buyerIdList.size()) ? goodsList.size() : buyerIdList.size();
        CountDownLatch downLatch = new CountDownLatch(buyFinishCount);
        //保证所有线程同时启动
        CountDownLatch concurrentBuy = new CountDownLatch(buyerIdList.size());

        Long startDate = System.currentTimeMillis();
        for (String buyerId : buyerIdList) {
            executorService.execute(() -> {
                try {
                    concurrentBuy.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //购买商品
                buyGoods(goodsList, buyerId);
                downLatch.countDown();
            });

            if (StringUtils.equals(buyerId, String.valueOf(buyerIdList.size() - 1))) {
                try {
                    //等待主子线程准备完毕，再启动
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startDate = System.currentTimeMillis();
                System.out.println("start for Date = " + startDate + ", " + DateUtils.parse(new Date(startDate), DateUtils.DATE_TIME_FORMAT));
            }
            concurrentBuy.countDown();
        }

        try {
            downLatch.await();
        } catch (InterruptedException e) {
            System.out.println("downLatch await is error = " + e.getMessage());
        }

        Long endDate = System.currentTimeMillis();
        System.out.println("end for Date = " + endDate + ", " + DateUtils.parse(new Date(endDate), DateUtils.DATE_TIME_FORMAT));
        System.out.println("抢购结束");
        return new Result(goodsList, (endDate - startDate));
    }


    public static void main(String[] args) {
        List<Goods> goodsList = new ArrayList<>();
        List<String> buyerIdList = new ArrayList<>();
        initData(goodsList, buyerIdList);

        Result result = simulate(goodsList, buyerIdList);
        System.out.println("result = " + JSONObject.toJSONString(result));
    }
}


class Goods {
    private String goodsId;
    private String buyerId;

    Goods(String goodsId, String buyerId) {
        this.goodsId = goodsId;
        this.buyerId = buyerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }
}

class Result {
    private List<Goods> goodsList;
    private Long elapsedTime;

    Result(List<Goods> goodsList, Long elapsedTime) {
        this.goodsList = goodsList;
        this.elapsedTime = elapsedTime;
    }

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}



