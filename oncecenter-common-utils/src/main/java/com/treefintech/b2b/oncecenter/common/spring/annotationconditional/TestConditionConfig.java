package com.treefintech.b2b.oncecenter.common.spring.annotationconditional;

import com.treefintech.b2b.oncecenter.common.datastructure.tree.binarytree.BinaryNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

/**
 * @author zhangfan
 * @description
 * @date 2019/10/22 下午8:45
 **/
public class TestConditionConfig {

    @Bean
    public TestAspect getAspect() {
        return new TestAspect();
    }

    @Bean
    @Conditional(value = TestCondition.class)
    public BinaryNode get() {
        return new BinaryNode();
    }
}
