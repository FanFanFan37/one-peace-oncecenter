package com.treefintech.b2b.oncecenter.common.spring.annotationconditional;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author zhangfan
 * @description
 * @date 2019/10/22 下午8:55
 **/
@Component
public class Person implements InitializingBean, DisposableBean {

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
