package com.github.netty.server.initialize;

import com.github.core.ReflectionUtils;
import com.github.event.AbstractInvokeEventBus.ModuleEvent;
import com.github.event.*;
import com.github.filter.ServiceFilterBinder;
import com.github.jmx.ModuleMetricsHandler;
import com.github.jmx.ModuleMetricsVisitor;
import com.github.model.MessageRequest;
import com.github.model.MessageResponse;
import com.github.netty.server.initialize.AbstractMessageRecvInitializeTask;
import com.github.parallel.SemaphoreWrapperFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class MessageRecvInitializeTask extends AbstractMessageRecvInitializeTask {

    private final AtomicReference<ModuleMetricsVisitor> visitor = new AtomicReference<>();

    private final AtomicReference<InvokeEventBusFacade> facade = new AtomicReference<>();

    private final AtomicReference<InvokeEventWatcher> watcher = new AtomicReference<>(new InvokeEventWatcher());

    private final SemaphoreWrapperFactory factory = SemaphoreWrapperFactory.getInstance();

    public MessageRecvInitializeTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        super(request, response, handlerMap);
    }

    @Override
    protected void injectInvoke() {
        Class<?> cls = handlerMap.get(request.getClassName()).getClass();
        boolean binder = ServiceFilterBinder.class.isAssignableFrom(cls);
        if (binder) {
            cls = ((ServiceFilterBinder) handlerMap.get(request.getClassName())).getObject().getClass();
        }

        ReflectionUtils utils = new ReflectionUtils();

        try {
            Method method = ReflectionUtils.getDeclaredMethod(cls, request.getMethodName(), request.getTypeParameters());
            utils.listMethod(method, false);
            String signatureMethod = utils.getProvider().toString();
            visitor.set(ModuleMetricsHandler.getInstance().visit(request.getClassName(), signatureMethod));
            facade.set(new InvokeEventBusFacade(ModuleMetricsHandler.getInstance(), visitor.get().getModuleName(), visitor.get().getMethodName()));
            watcher.get().addObserver(new InvokeObserver(facade.get(), visitor.get()));
            watcher.get().watch(ModuleEvent.INVOKE_EVENT);
        } finally {
            utils.clearProvider();
        }
    }

    @Override
    protected void injectSuccInvoke(long invokeTimespan) {
        watcher.get().addObserver(new InvokeSuccObserver(facade.get(), visitor.get(), invokeTimespan));
        watcher.get().watch(ModuleEvent.INVOKE_SUCC_EVENT);
    }

    @Override
    protected void injectFailInvoke(Throwable error) {
        watcher.get().addObserver(new InvokeFailObserver(facade.get(), visitor.get(), error));
        watcher.get().watch(ModuleEvent.INVOKE_FAIL_EVENT);
    }

    @Override
    protected void injectFilterInvoke() {
        watcher.get().addObserver(new InvokeFilterObserver(facade.get(), visitor.get()));
        watcher.get().watch(ModuleEvent.INVOKE_FILTER_EVENT);
    }

    @Override
    protected void acquire() {
        factory.acquire();
    }

    @Override
    protected void release() {
        factory.release();
    }

}
