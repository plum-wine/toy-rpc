package com.github.filter.support;

import com.github.core.ModuleInvoker;
import com.github.filter.ChainFilter;
import com.github.model.MessageRequest;


public class ClassLoaderChainFilter implements ChainFilter {
    @Override
    public Object invoke(ModuleInvoker<?> invoker, MessageRequest request) throws Throwable {
        ClassLoader ocl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(invoker.getInterface().getClassLoader());

        Object result = null;
        try {
            result = invoker.invoke(request);
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            Thread.currentThread().setContextClassLoader(ocl);
        }
    }
}

