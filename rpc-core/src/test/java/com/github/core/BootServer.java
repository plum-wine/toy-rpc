package com.github.core;

import com.github.core.service.HelloService;
import com.github.event.ClientStopEventListener;
import com.github.netty.MessageRecvExecutor;
import com.github.netty.MessageSendExecutor;
import com.github.serialize.RpcSerializeProtocol;
import com.google.common.eventbus.EventBus;

/**
 * @author hangs.zhang
 * @date 2020/04/21 23:04
 * *****************
 * function:
 */
public class BootServer {

    public static void main(String[] args) throws Exception {
        MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
        ref.setServerAddress(CommonConfig.ipAddr);
        ref.setEchoApiPort(Integer.parseInt(CommonConfig.echoApiPort));
        ref.setSerializeProtocol(Enum.valueOf(RpcSerializeProtocol.class, CommonConfig.SERIALIZE));
        ref.start();

        EventBus eventBus = new EventBus();
        HelloService execute = MessageSendExecutor.getInstance().execute(HelloService.class);
        MessageSendExecutor.getInstance().setRpcServerLoader(CommonConfig.ipAddr, RpcSerializeProtocol.valueOf(CommonConfig.SERIALIZE));
        ClientStopEventListener listener = new ClientStopEventListener();
        eventBus.register(listener);
    }

}
