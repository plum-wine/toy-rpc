package com.github.core;

import com.github.core.service.HelloService;
import com.github.netty.MessageSendExecutor;
import com.github.serialize.RpcSerializeProtocol;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * @author hangs.zhang
 * @date 2020/4/22 下午12:45
 * *********************
 * function:
 */
public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    public void test() throws Exception {
        HelloService execute = MessageSendExecutor.getInstance().execute(HelloService.class);
        MessageSendExecutor.getInstance().setRpcServerLoader(CommonConfig.ipAddr, RpcSerializeProtocol.valueOf(CommonConfig.SERIALIZE));
        String result = execute.sayHello();
        LOGGER.info("result:{}", result);
    }

}
