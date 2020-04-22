package com.github.netty;

import com.github.netty.handler.JdkNativeSendHandler;
import com.github.netty.handler.NettyRpcSendHandler;
import com.github.netty.handler.ProtostuffSendHandler;
import com.github.serialize.RpcSerializeFrame;
import com.github.serialize.RpcSerializeProtocol;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import io.netty.channel.ChannelPipeline;


public class RpcSendSerializeFrame implements RpcSerializeFrame {

    private static ClassToInstanceMap<NettyRpcSendHandler> HANDLER = MutableClassToInstanceMap.create();

    static {
        HANDLER.putInstance(JdkNativeSendHandler.class, new JdkNativeSendHandler());
        HANDLER.putInstance(ProtostuffSendHandler.class, new ProtostuffSendHandler());
    }

    @Override
    public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case JDK_SERIALIZE: {
                HANDLER.getInstance(JdkNativeSendHandler.class).handle(pipeline);
                break;
            }
            case PROTOSTUFF_SERIALIZE: {
                HANDLER.getInstance(ProtostuffSendHandler.class).handle(pipeline);
                break;
            }
            default: {
                throw new RuntimeException();
            }
        }
    }
}

