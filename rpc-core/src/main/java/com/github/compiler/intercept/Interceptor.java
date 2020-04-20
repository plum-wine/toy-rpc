package com.github.compiler.intercept;


public interface Interceptor {
    Object intercept(Invocation invocation) throws Throwable;
}

