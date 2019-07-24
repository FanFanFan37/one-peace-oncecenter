package com.treefintech.b2b.oncecenter.common.designpattern;

import java.math.BigDecimal;

/**
 * 模板模式 --- 行为型模式 （产融贷中的大面积使用到，各种Abstract类）
 * 功能：抽象的父类: 定义了骨架，也就是定义了这个流程的每一个步骤
 *      子类: 实现父类中的某几个步骤。
 *
 */
public class TemplatePatternTest {
    public static void main(String[] args) {
        Param param = new Param();
        param.setBalance(null);
        System.out.println(BigDecimal.valueOf(0L).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP));
    }
}


class Param{
    private Long balance;

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}