package com.github.listener;

import com.github.core.ModuleInvoker;
import com.github.core.ModuleProvider;
import com.github.model.MessageRequest;

import java.util.List;


public class ModuleProviderWrapper<T> implements ModuleProvider<T> {

    private final ModuleProvider<T> provider;

    private final MessageRequest request;

    private final List<ModuleListener> listeners;

    public ModuleProviderWrapper(ModuleProvider<T> provider, List<ModuleListener> listeners, MessageRequest request) {
        if (provider == null) {
            throw new IllegalArgumentException("provider is null");
        }
        this.provider = provider;
        this.listeners = listeners;
        this.request = request;
        if (listeners != null && listeners.size() > 0) {
            RuntimeException exception = null;
            for (ModuleListener listener : listeners) {
                if (listener != null) {
                    try {
                        listener.exported(this, request);
                    } catch (RuntimeException t) {
                        exception = t;
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
        }
    }

    @Override
    public ModuleInvoker<T> getInvoker() {
        return provider.getInvoker();
    }

    @Override
    public void destoryInvoker() {
        try {
            provider.destoryInvoker();
        } finally {
            if (listeners != null && listeners.size() > 0) {
                RuntimeException exception = null;
                for (ModuleListener listener : listeners) {
                    if (listener != null) {
                        try {
                            listener.unExported(this, request);
                        } catch (RuntimeException t) {
                            exception = t;
                        }
                    }
                }
                if (exception != null) {
                    throw exception;
                }
            }
        }
    }

}

