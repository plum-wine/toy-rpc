package com.github.netty;

import com.google.common.reflect.AbstractInvocationHandler;
import com.github.core.MessageCallBack;
import com.github.model.MessageRequest;

import java.lang.reflect.Method;
import java.util.UUID;


public class MessageSendProxy<T> extends AbstractInvocationHandler {

    @Override
    public Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        MessageRequest request = new MessageRequest();
        request.setMessageId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setTypeParameters(method.getParameterTypes());
        request.setParametersVal(args);

        MessageSendHandler handler = RpcServerLoader.getInstance().getMessageSendHandler();
        MessageCallBack callBack = handler.sendRequest(request);
        return callBack.start();
    }

}

