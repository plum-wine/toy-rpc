package com.github.compiler.weaver;


import com.github.compiler.intercept.Interceptor;

public interface ClassProxy {

    <T> T createProxy(Object target, Interceptor interceptor, Class<?>... proxyClasses);

    <T> T createProxy(ClassLoader classLoader, Object target, Interceptor interceptor, Class<?>... proxyClasses);

}

