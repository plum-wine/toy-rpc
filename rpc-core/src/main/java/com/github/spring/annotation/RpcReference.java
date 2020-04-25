package com.github.spring.annotation;

import com.github.spring.RpcReferenceRegister;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author hangs.zhang
 * @date 2020/04/21 22:19
 * *****************
 * function:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Import(RpcReferenceRegister.class)
public @interface RpcReference {

    String id();

    String interfaceName();

    String protocol();

    String ipAddr();

}
