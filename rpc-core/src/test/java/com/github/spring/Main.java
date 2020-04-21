package com.github.spring;

import com.github.spring.config.RpcConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author hangs.zhang
 * @date 2020/04/21 22:52
 * *****************
 * function:
 */
public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(RpcConfiguration.class);
        applicationContext.refresh();
        RpcRegistery registery = applicationContext.getBean("registery", RpcRegistery.class);
    }

}
