package com.github.core.service;

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
