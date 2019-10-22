package com.treefintech.b2b.oncecenter.common.spring.annotationconditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author zhangfan
 * @description
 * @date 2019/10/22 下午8:43
 **/
public class TestCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        //判断容器中是否存在TestAspect组件
        if (context.getBeanFactory().containsBean("TestAspectaaa")) {
            return true;
        }
        return false;
    }
}
