package com.treefintech.b2b.oncecenter.common.designpattern;

import java.util.HashMap;
import java.util.Map;

/**
 * 享元模式 --- 结构型模式
 * 功能：减少创建的对象数量，若内存中存在，则直接返回，不创建新的对象。
 * 例如：数据库连接池，String常量池
 *
 * 享元模式有内部状态和外部状态。
 * 内部状态：可以共享，并且不会随环境的改变而改变
 * 外部状态：不可共享，并且会随环境的改变而改变
 */
public class FlyweightPatternTest {

    public static void main(String[] args) {
        Shape shape = FlyweightFactory.getShape("aaa");
        shape.draw();
        shape = FlyweightFactory.getShape("bbb");
        shape.draw();
        shape = FlyweightFactory.getShape("ccc");
        shape.draw();
        shape = FlyweightFactory.getShape("aaa");
        shape.draw();
        shape = FlyweightFactory.getShape("aaa");
        shape.draw();
        System.out.println(FlyweightFactory.getSize());
    }
}


interface Shape {
    void draw();
}


class Circle implements Shape{
    private String color;
    public Circle(String color){
        this.color = color;
    }

    @Override
    public void draw() {
        System.out.println("画了一个" + color +"的圆形");
    }

}


class FlyweightFactory {
    private static Map<String, Shape> shapeMap = new HashMap<>();

    public static Shape getShape(String key) {
        Shape shape = shapeMap.get(key);
        if (shape == null) {
            shape = new Circle(key);
            shapeMap.put(key, shape);
        }

        return shape;
    }

    public static Integer getSize() {
        return shapeMap.size();
    }
}


















