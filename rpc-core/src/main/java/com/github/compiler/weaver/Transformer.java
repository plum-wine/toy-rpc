package com.github.compiler.weaver;

public interface Transformer {
    Class<?> transform(ClassLoader classLoader, Class<?>... proxyClasses);
}

