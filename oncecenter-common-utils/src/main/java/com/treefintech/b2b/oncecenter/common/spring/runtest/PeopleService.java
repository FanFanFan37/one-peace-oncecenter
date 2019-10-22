package com.treefintech.b2b.oncecenter.common.spring.runtest;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * @author zhangfan
 * @description
 * @date 2019/10/22 下午8:31
 **/
@Service
public class PeopleService {

    @Bean
    public String man() {
        return "man";
    }
}
