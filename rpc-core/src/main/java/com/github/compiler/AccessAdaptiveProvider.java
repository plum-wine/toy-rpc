package com.github.compiler;

import com.github.compiler.intercept.SimpleMethodInterceptor;
import com.github.core.ReflectionUtils;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;


public class AccessAdaptiveProvider extends AbstractAccessAdaptive implements AccessAdaptive {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    protected Class<?> doCompile(String clsName, String javaSource) {
        File tempFileLocation = Files.createTempDir();
        compiler = new NativeCompiler(tempFileLocation);
        Class<?> type = compiler.compile(clsName, javaSource);
        tempFileLocation.deleteOnExit();
        return type;
    }

    @Override
    public Object invoke(String javaSource, String method, Object[] args) {
        if (StringUtils.isEmpty(javaSource) || StringUtils.isEmpty(method)) {
            return null;
        } else {
            try {
                Class<?> type = compile(javaSource, Thread.currentThread().getContextClassLoader());
                Object object = ReflectionUtils.newInstance(type);
                Thread.currentThread().getContextClassLoader().loadClass(type.getName());
                Object proxy = getFactory().createProxy(object, new SimpleMethodInterceptor(), new Class<?>[]{type});
                return MethodUtils.invokeMethod(proxy, method, args);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                LOGGER.error("invoke error", e);
            }
        }
        return null;
    }

}
