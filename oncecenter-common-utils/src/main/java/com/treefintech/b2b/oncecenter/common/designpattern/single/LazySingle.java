package com.treefintech.b2b.oncecenter.common.designpattern.single;

import java.io.Serializable;


/**
 * 懒汉式
 * 在第一次使用的时候，才创建。
 *
 * 普通懒汉式 问题：多线程同时进入if语句，就会创建两个甚至多个对象。
 *
 * (1,2) 加同步锁之后 问题：  隐患的原因主要和Java内存模型（JMM）有关
 *      线程A发现变量没有被初始化, 然后它获取锁并开始变量的初始化。
 *      由于某些编程语言的语义，编译器生成的代码允许在线程A执行完变量的初始化之前，更新变量并将其指向部分初始化的对象。
 *      线程B发现共享变量已经被初始化，并返回变量。由于线程B确信变量已被初始化，它没有获取锁。
 *      如果在A完成初始化之前共享变量对B可见（这是由于A没有完成初始化或者因为一些初始化的值还没有穿过B使用的内存(缓存一致性)），程序很可能会崩溃。
 *
 * (3) 使用volatile：用volatile声明的变量被修改后，会强制写入内存中。（不用volatile声明的变量一般都是先缓存，这样就造成 A线程中变量修改后的值，B线程是变量修改前的值）
 *     问题： 反序列化时会存在问题。
 *
 * (4) 实现 Serializable、增加readResolve()方法，解决序列化问题。
 *      增加readResolve()方法：在jdk中ObjectInputStream的类中有readUnshared()方法，有说明。
 *
 * @author zhangfan
 * @description
 * @date 2019/7/9 上午10:36
 **/
//4、实现Serializable
public class LazySingle implements Serializable{

    //3、 增加 volatile
    private static volatile LazySingle lazySingle;

    private LazySingle() {

    }

    //1、在方法上加 同步锁。
    //2、锁范围缩小，在创建的时候加锁。
    public static LazySingle getInstance() {
        if (lazySingle == null) {
            synchronized (LazySingle.class) {

                //3、增加判断
                if (lazySingle == null) {
                    lazySingle = new LazySingle();
                }
            }
        }
        return lazySingle;
    }

    //4、ObjectInputStream 中readUnshared()方法有说明。
    private Object readResolve() {
        return getInstance();
    }
}
