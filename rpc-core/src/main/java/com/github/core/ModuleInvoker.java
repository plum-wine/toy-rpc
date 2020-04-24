package com.github.core;

import com.github.model.MessageRequest;

public interface ModuleInvoker<T> {

    Class<T> getInterface();

    Object invoke(MessageRequest request) throws Throwable;

    void destroy();

}

