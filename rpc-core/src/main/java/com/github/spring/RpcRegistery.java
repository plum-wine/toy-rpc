package com.github.spring;

import com.github.core.RpcSystemConfig;
import com.github.jmx.ModuleMetricsHandler;
import com.github.netty.MessageRecvExecutor;
import com.github.serialize.RpcSerializeProtocol;
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
    public void destroy() throws Exception {
        MessageRecvExecutor.getInstance().stop();
        if (RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT) {
            ModuleMetricsHandler handler = ModuleMetricsHandler.getInstance();
            handler.stop();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
        ref.setServerAddress(ipAddr);
        ref.setEchoApiPort(Integer.parseInt(echoApiPort));
        ref.setSerializeProtocol(Enum.valueOf(RpcSerializeProtocol.class, protocol));
        ref.start();
    }

}
