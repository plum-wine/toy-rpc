package com.github.spring.config;

import com.github.spring.RpcRegistery;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

/**
 * @author hangs.zhang
 * @date 2020/04/21 22:49
 * *****************
 * function:
 */
@Configurable
public class RpcConfiguration {

    @Bean(name = "registery")
    public RpcRegistery rpcRegistery() {
        RpcRegistery rpcRegistery = new RpcRegistery();
        rpcRegistery.setIpAddr("127.0.0.1:18887");
        rpcRegistery.setEchoApiPort("18886");
        rpcRegistery.setProtocol("PROTOSTUFFSERIALIZE");
        return rpcRegistery;
    }

}
