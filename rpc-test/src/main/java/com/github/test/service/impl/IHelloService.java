package com.github.test.service.impl;

import com.github.test.service.HelloService;

/**
 * @author hangs.zhang
 * @date 2020/04/22 00:21
 * *****************
 * function:
 */
public class IHelloService implements HelloService {
    @Override
    public String sayHello() {
        return "hello world";
    }
}
