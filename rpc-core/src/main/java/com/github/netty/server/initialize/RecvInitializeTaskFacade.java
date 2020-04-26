package com.github.netty.server.initialize;

import com.github.model.MessageRequest;
import com.github.model.MessageResponse;

import java.util.Map;
import java.util.concurrent.Callable;


public class RecvInitializeTaskFacade {

    private final MessageRequest request;

    private final MessageResponse response;

    private final Map<String, Object> handlerMap;

    public RecvInitializeTaskFacade(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        this.request = request;
        this.response = response;
        this.handlerMap = handlerMap;
    }

    public Callable<Boolean> getTask() {
        return new MessageRecvInitializeTaskAdapter(request, response, handlerMap);
    }

}

