package com.github.test.service.impl;

import com.github.test.service.ExceptionService;

/**
 * @author hangs.zhang
 * @date 2020/04/27 00:04
 * *****************
 * function:
 */
public class IExceptionService implements ExceptionService {

    @Override
    public String throwException() {
        throw new RuntimeException("test exception");
    }

}
