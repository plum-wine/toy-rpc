package com.github.filter.support;

import com.github.core.ModuleInvoker;
import com.github.filter.ChainFilter;
import com.github.model.MessageRequest;


public class EchoChainFilter implements ChainFilter {
    @Override
    public Object invoke(ModuleInvoker<?> invoker, MessageRequest request) throws Throwable {
        Object o = null;
        try {
            System.out.println("EchoChainFilter##TRACE MESSAGE-ID:" + request.getMessageId());
            o = invoker.invoke(request);
            return o;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        }
    }
}

