package com.treefintech.b2b.democenter.main.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by loujianpeng on 2018/4/19.
 */
@SpringBootApplication
@ServletComponentScan
@ImportResource(value = {"spring/treefintech-b2b-oncecenter-main-spring.xml"})
@EnableScheduling
public class OnceCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoCenterApplication.class, args);
    }

}
