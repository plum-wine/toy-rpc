package com.github.netty.server;

import com.github.core.SystemConfig;
import com.github.entity.MessageRequest;
import com.github.entity.MessageResponse;
import com.github.netty.server.proxy.MethodInvoker;
import com.github.netty.server.proxy.MethodProxyAdvisor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 处理客户端请求的task
 */
@Data
public class MessageRecvInitializeTask implements Callable<Boolean> {

    protected MessageRequest request = null;

    protected MessageResponse response = null;

    protected Map<String, Object> handlerMap = null;

    protected static final String METHOD_MAPPED_NAME = "invoke";

    protected boolean returnNotNull = true;

    protected long invokeTimespan;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public MessageRecvInitializeTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        this.request = request;
        this.response = response;
        this.handlerMap = handlerMap;
    }

    @Override
    public Boolean call() {
        try {
            response.setMessageId(request.getMessageId());
            // 执行客户端传来的请求
            Object result = reflect(request);
            boolean isInvokeSucc = (!returnNotNull || result != null);
            if (isInvokeSucc) {
                response.setResult(result);
                response.setError("");
                response.setReturnNotNull(returnNotNull);
            } else {
                LOGGER.info(SystemConfig.FILTER_RESPONSE_MSG);
                response.setResult(null);
                response.setError(SystemConfig.FILTER_RESPONSE_MSG);
            }
            return Boolean.TRUE;
        } catch (Throwable t) {
            response.setError(getStackTrace(t));
            LOGGER.error("RPC Server invoke error!", t);
            return Boolean.FALSE;
        }
    }

    private Object reflect(MessageRequest request) throws Throwable {
        ProxyFactory weaver = new ProxyFactory(new MethodInvoker());
        NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
        // 代理MethodInvoker的invoke方法
        advisor.setMappedName(METHOD_MAPPED_NAME);
        advisor.setAdvice(new MethodProxyAdvisor(handlerMap));
        weaver.addAdvisor(advisor);
        // 完成织入
        MethodInvoker methodInvoker = (MethodInvoker) weaver.getProxy();
        Object obj = invoke(methodInvoker, request);
        invokeTimespan = methodInvoker.getInvokeTimespan();
        setReturnNotNull(((MethodProxyAdvisor) advisor.getAdvice()).isReturnNotNull());
        return obj;
    }

    private Object invoke(MethodInvoker methodInvoker, MessageRequest request) throws Throwable {
        return methodInvoker.invoke(request);
    }

    public String getStackTrace(Throwable ex) {
        StringWriter buf = new StringWriter();
        ex.printStackTrace(new PrintWriter(buf));
        return buf.toString();
    }

}

