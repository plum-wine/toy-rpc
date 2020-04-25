package com.github.netty.server.initialize;

import com.github.core.Modular;
import com.github.core.ModuleInvoker;
import com.github.core.ModuleProvider;
import com.github.core.RpcSystemConfig;
import com.github.model.MessageRequest;
import com.github.model.MessageResponse;
import com.github.netty.server.proxy.MethodInvoker;
import com.github.netty.server.proxy.MethodProxyAdvisor;
import com.github.spring.BeanFactoryUtils;
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

@Data
public abstract class AbstractMessageRecvInitializeTask implements Callable<Boolean> {

    protected MessageRequest request = null;

    protected MessageResponse response = null;

    protected Map<String, Object> handlerMap = null;

    protected static final String METHOD_MAPPED_NAME = "invoke";

    protected boolean returnNotNull = true;

    protected long invokeTimespan;

    protected Modular modular = BeanFactoryUtils.getBean("modular");

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public AbstractMessageRecvInitializeTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        this.request = request;
        this.response = response;
        this.handlerMap = handlerMap;
    }

    @Override
    public Boolean call() {
        try {
            acquire();
            response.setMessageId(request.getMessageId());
            injectInvoke();
            // 处理请求
            Object result = reflect(request);
            boolean isInvokeSucc = (!returnNotNull || result != null);
            if (isInvokeSucc) {
                response.setResult(result);
                response.setError("");
                response.setReturnNotNull(returnNotNull);
                injectSuccInvoke(invokeTimespan);
            } else {
                LOGGER.info(RpcSystemConfig.FILTER_RESPONSE_MSG);
                response.setResult(null);
                response.setError(RpcSystemConfig.FILTER_RESPONSE_MSG);
                injectFilterInvoke();
            }
            return Boolean.TRUE;
        } catch (Throwable t) {
            response.setError(getStackTrace(t));
            LOGGER.error("RPC Server invoke error!", t);
            injectFailInvoke(t);
            return Boolean.FALSE;
        } finally {
            release();
        }
    }

    private Object reflect(MessageRequest request) throws Throwable {
        ProxyFactory weaver = new ProxyFactory(new MethodInvoker());
        NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
        advisor.setMappedName(METHOD_MAPPED_NAME);
        advisor.setAdvice(new MethodProxyAdvisor(handlerMap));
        weaver.addAdvisor(advisor);
        // 完成织入
        MethodInvoker mi = (MethodInvoker) weaver.getProxy();
        Object obj = invoke(mi, request);
        invokeTimespan = mi.getInvokeTimespan();
        setReturnNotNull(((MethodProxyAdvisor) advisor.getAdvice()).isReturnNotNull());
        return obj;
    }

    private Object invoke(MethodInvoker mi, MessageRequest request) throws Throwable {
        if (modular != null) {
            ModuleProvider provider = modular.invoke(new ModuleInvoker() {

                @Override
                public Class<?> getInterface() {
                    return mi.getClass().getInterfaces()[0];
                }

                @Override
                public Object invoke(MessageRequest request) throws Throwable {
                    return mi.invoke(request);
                }

                @Override
                public void destroy() {

                }
            }, request);
            return provider.getInvoker().invoke(request);
        } else {
            return mi.invoke(request);
        }
    }

    public String getStackTrace(Throwable ex) {
        StringWriter buf = new StringWriter();
        ex.printStackTrace(new PrintWriter(buf));
        return buf.toString();
    }

    protected abstract void injectInvoke();

    protected abstract void injectSuccInvoke(long invokeTimespan);

    protected abstract void injectFailInvoke(Throwable error);

    protected abstract void injectFilterInvoke();

    protected abstract void acquire();

    protected abstract void release();

}

