package com.github.spring;

import com.github.netty.server.MessageRecvExecutor;
import com.github.serialize.SerializeProtocol;
import lombok.Data;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author hangs.zhang
 * @date 2020/04/21 22:27
 * *****************
 * function:
 */
@Data
public class RpcRegistery implements InitializingBean, DisposableBean {

    private String ipAddr;

    private String protocol;

    private String echoApiPort;

    @Override
    public void destroy() {
        MessageRecvExecutor.getInstance().stop();
    }

    @Override
    public void afterPropertiesSet() {
        MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
        ref.setServerAddress(ipAddr);
        ref.setEchoApiPort(Integer.parseInt(echoApiPort));
        ref.setSerializeProtocol(Enum.valueOf(SerializeProtocol.class, protocol));
        ref.start();
    }

}
