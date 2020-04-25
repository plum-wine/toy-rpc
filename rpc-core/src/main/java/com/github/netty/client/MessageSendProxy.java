package com.github.netty.client;

import com.github.core.MessageCallBack;
import com.github.model.MessageRequest;
import com.google.common.reflect.AbstractInvocationHandler;

import java.lang.reflect.Method;
import java.util.UUID;


public class MessageSendProxy<T> extends AbstractInvocationHandler {

    @Override
    public Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        // 封装请求参数
        MessageRequest request = new MessageRequest();
        request.setMessageId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setTypeParameters(method.getParameterTypes());
        request.setParametersVal(args);

        MessageSendHandler handler = ClientLoader.getInstance().getMessageSendHandler();
        MessageCallBack callBack = handler.sendRequest(request);
        return callBack.start();
    }

}

