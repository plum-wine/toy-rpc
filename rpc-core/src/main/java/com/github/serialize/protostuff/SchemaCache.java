package com.github.serialize.protostuff;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class SchemaCache {

    private static class SchemaCacheHolder {
        private static final SchemaCache CACHE = new SchemaCache();
    }

    public static SchemaCache getInstance() {
        return SchemaCacheHolder.CACHE;
    }

    private final Cache<Class<?>, Schema<?>> CACHE = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    private Schema<?> get(final Class<?> cls, Cache<Class<?>, Schema<?>> cache) {
        try {
            return cache.get(cls, new Callable<RuntimeSchema<?>>() {
                @Override
                public RuntimeSchema<?> call() {
                    return RuntimeSchema.createFrom(cls);
                }
            });
        } catch (ExecutionException e) {
            return null;
        }
    }

    public Schema<?> get(final Class<?> cls) {
        return get(cls, CACHE);
    }

}

