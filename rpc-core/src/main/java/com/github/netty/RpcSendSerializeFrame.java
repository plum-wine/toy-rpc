package com.github.netty;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.github.netty.handler.*;
import com.github.serialize.RpcSerializeFrame;
import com.github.serialize.RpcSerializeProtocol;
import io.netty.channel.ChannelPipeline;


public class RpcSendSerializeFrame implements RpcSerializeFrame {
    private static ClassToInstanceMap<NettyRpcSendHandler> handler = MutableClassToInstanceMap.create();

    static {
        handler.putInstance(JdkNativeSendHandler.class, new JdkNativeSendHandler());
        handler.putInstance(ProtostuffSendHandler.class, new ProtostuffSendHandler());
    }

    @Override
    public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case JDKSERIALIZE: {
                handler.getInstance(JdkNativeSendHandler.class).handle(pipeline);
                break;
            }
            case PROTOSTUFFSERIALIZE: {
                handler.getInstance(ProtostuffSendHandler.class).handle(pipeline);
                break;
            }
            default: {
                break;
            }
        }
    }
}

