package com.github.listener.support;

import com.github.core.ModuleProvider;
import com.github.listener.ModuleListener;
import com.github.model.MessageRequest;
import org.apache.commons.lang3.StringUtils;

public class ModuleListenerAdapter implements ModuleListener {
    @Override
    public void exported(ModuleProvider<?> provider, MessageRequest request) {
        System.out.println(StringUtils.center("[ModuleListenerAdapter##exported]", 48, "*"));
    }

    @Override
    public void unExported(ModuleProvider<?> provider, MessageRequest request) {
        System.out.println(StringUtils.center("[ModuleListenerAdapter##unExported]", 48, "*"));
    }
}

