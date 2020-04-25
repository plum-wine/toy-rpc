package com.github.spring.annotation;

import com.github.spring.RpcServiceRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author hangs.zhang
 * @date 2020/04/21 22:08
 * *****************
 * function:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Import(RpcServiceRegistrar.class)
public @interface RpcService {

    String interfaceName();

    String ref();

    String filter();

}
