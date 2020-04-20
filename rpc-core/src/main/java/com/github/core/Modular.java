package com.github.core;

import com.github.model.MessageRequest;


public interface Modular {
    <T> ModuleProvider<T> invoke(ModuleInvoker<T> invoker, MessageRequest request);
}

