package com.treefintech.b2b.oncecenter.common.designpattern.single;

/**
 * 饿汉式
 * 在使用之前，就已经创建
 *
 * 问题：当使用反射 Class.forName() 方式获取的时候，就会再创建一个新的对象。
 * @author zhangfan
 * @description
 * @date 2019/7/9 上午10:00
 **/
public class HungrySingle {

    // 1
    private static HungrySingle hungrySingle = new HungrySingle();

    //2
//    private static HungrySingle hungrySingle;
//
//    static {
//        hungrySingle = new HungrySingle();
//    }

    private HungrySingle() {

    }

    public static HungrySingle getInstance() {
        return hungrySingle;
    }

}









