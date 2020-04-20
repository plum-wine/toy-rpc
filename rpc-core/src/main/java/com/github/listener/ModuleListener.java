package com.github.listener;

import com.github.core.ModuleProvider;
import com.github.model.MessageRequest;


public interface ModuleListener {
    void exported(ModuleProvider<?> provider, MessageRequest request);

    void unExported(ModuleProvider<?> provider, MessageRequest request);
}

