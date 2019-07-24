package com.treefintech.b2b.oncecenter.common.designpattern;

/**
 * 装饰器模式  ---  结构型模式
 * 功能：动态地给一个对象添加一些额外的功能
 *
 * 场景: 若需要扩展一个类，但是又不想使用继承的方式。
 *
 */
public class DecoratorPatternTest {

    public static void main(String[] args) {
        WashHandsCook washHandsCook = new WashHandsCook(new ChineseCook());
        washHandsCook.cookDinner();
    }
}


interface Cook {

    void cookDinner();
}


class ChineseCook implements Cook {

    @Override
    public void cookDinner() {
        System.out.println("中国人做晚饭");
    }
}

abstract class AbstractFilterCook implements Cook {
    protected Cook cook;
}


class WashHandsCook extends AbstractFilterCook {

    public WashHandsCook(Cook cook) {
        this.cook = cook;
    }

    @Override
    public void cookDinner() {
        System.out.println("先洗手");
        cook.cookDinner();
    }
}






