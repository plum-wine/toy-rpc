package com.github.test.filter;

import com.github.core.filter.Filter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;


public class SimpleFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public boolean before(Method method, Object processor, Object[] requestObjects) {
        LOGGER.info(StringUtils.center("[SimpleFilter##before]", 48, "*"));
        return true;
    }

    @Override
    public void after(Method method, Object processor, Object[] requestObjects) {
        LOGGER.info(StringUtils.center("[SimpleFilter##after]", 48, "*"));
    }

}

