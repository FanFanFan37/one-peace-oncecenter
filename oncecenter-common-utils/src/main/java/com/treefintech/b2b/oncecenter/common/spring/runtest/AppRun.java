package com.treefintech.b2b.oncecenter.common.spring.runtest;

import com.treefintech.b2b.oncecenter.common.spring.annotationconditional.TestConditionConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhangfan
 * @description
 * @date 2019/10/9 下午3:07
 **/
public class AppRun {

    public static void main(String[] args) {
//        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/application.xml");
//        System.out.println("context is running");
//
//        MessageService messageService = context.getBean(MessageService.class);
//        System.out.println(messageService.getMessage());

//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(PeopleService.class);
//        System.out.println(ctx.getBean("man"));

        TestConditionConfig testConditionConfig = new TestConditionConfig();
        System.out.println(testConditionConfig.get() == null);
    }
}
