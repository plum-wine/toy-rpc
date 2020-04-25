package com.github.netty.server.initialize;

import com.github.core.RpcSystemConfig;
import com.github.model.MessageRequest;
import com.github.model.MessageResponse;

import java.util.Map;
import java.util.concurrent.Callable;


public class RecvInitializeTaskFacade {

    private final MessageRequest request;

    private final MessageResponse response;

    private final Map<String, Object> handlerMap;

    private final boolean isMetrics = RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT;

    private final boolean jmxMetricsHash = RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_HASH_SUPPORT;

    public RecvInitializeTaskFacade(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        this.request = request;
        this.response = response;
        this.handlerMap = handlerMap;
    }

    public Callable<Boolean> getTask() {
        return isMetrics ? getMetricsTask() : new MessageRecvInitializeTaskAdapter(request, response, handlerMap);
    }

    private Callable<Boolean> getMetricsTask() {
        return jmxMetricsHash ? new HashMessageRecvInitializeTask(request, response, handlerMap) : new MessageRecvInitializeTask(request, response, handlerMap);
    }

}

