package com.github.core;

import com.github.core.filter.SimpleFilter;
import com.github.core.service.HelloService;
import com.github.core.service.IHelloService;
import com.github.filter.ServiceFilterBinder;
import com.github.netty.MessageRecvExecutor;
import com.github.serialize.RpcSerializeProtocol;

/**
 * @author hangs.zhang
 * @date 2020/04/21 23:04
 * *****************
 * function:
 */
public class BootServer {

    public static void main(String[] args) {
        MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
        ref.setServerAddress(CommonConfig.ipAddr);
        ref.setEchoApiPort(Integer.parseInt(CommonConfig.echoApiPort));
        ref.setSerializeProtocol(Enum.valueOf(RpcSerializeProtocol.class, CommonConfig.SERIALIZE));
        ref.start();

        HelloService helloService = new IHelloService();
        ServiceFilterBinder binder = new ServiceFilterBinder();
        binder.setObject(helloService);
        SimpleFilter filter = new SimpleFilter();
        binder.setFilter(filter);
        MessageRecvExecutor.getInstance().getHandlerMap().put(HelloService.class.getCanonicalName(), binder);
    }

}
