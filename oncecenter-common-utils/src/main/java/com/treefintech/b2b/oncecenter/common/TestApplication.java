package com.treefintech.b2b.oncecenter.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * @author zhangfan
 * @description
 * @date 2019/10/22 下午8:58
 **/
@SpringBootApplication
@ServletComponentScan
@ImportResource(value = {"spring/application.xml"})
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
