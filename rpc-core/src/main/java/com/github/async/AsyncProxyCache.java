package com.github.async;

import com.google.common.collect.Maps;

import java.util.Map;

public class AsyncProxyCache {

    private static final Map<String, Class<?>> CACHE = Maps.newConcurrentMap();

    public static Class<?> get(String key) {
        return CACHE.get(key);
    }

    public static void save(String key, Class<?> proxyClass) {
        if (!CACHE.containsKey(key)) {
            synchronized (CACHE) {
                if (!CACHE.containsKey(key)) {
                    CACHE.put(key, proxyClass);
                }
            }
        }
    }

}

