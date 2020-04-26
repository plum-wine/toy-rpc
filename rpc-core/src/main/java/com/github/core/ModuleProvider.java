package com.github.core;


public interface ModuleProvider<T> {

    ModuleInvoker<T> getInvoker();

    void destoryInvoker();

}

