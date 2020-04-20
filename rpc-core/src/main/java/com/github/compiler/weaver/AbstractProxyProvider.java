package com.github.compiler.weaver;

import com.github.compiler.intercept.Interceptor;


public abstract class AbstractProxyProvider implements ClassProxy {

    @Override
    public <T> T createProxy(Object target, Interceptor interceptor, Class<?>... proxyClasses) {
        return createProxy(Thread.currentThread().getContextClassLoader(), target, interceptor, proxyClasses);
    }
}

