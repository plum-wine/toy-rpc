package com.github.filter;

import com.github.core.ModuleInvoker;
import com.github.model.MessageRequest;

public interface ChainFilter {
    Object invoke(ModuleInvoker<?> invoker, MessageRequest request) throws Throwable;
}

