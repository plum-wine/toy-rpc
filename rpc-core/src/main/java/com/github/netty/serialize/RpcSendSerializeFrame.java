package com.github.netty.serialize;

import com.github.netty.handler.impl.NativeSendHandler;
import com.github.netty.handler.NettyRpcSendHandler;
import com.github.netty.handler.impl.ProtostuffSendHandler;
import com.github.serialize.RpcSerializeFrame;
import com.github.serialize.SerializeProtocol;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import io.netty.channel.ChannelPipeline;


public class RpcSendSerializeFrame implements RpcSerializeFrame {

    private static final ClassToInstanceMap<NettyRpcSendHandler> HANDLER = MutableClassToInstanceMap.create();

    static {
        HANDLER.putInstance(NativeSendHandler.class, new NativeSendHandler());
        HANDLER.putInstance(ProtostuffSendHandler.class, new ProtostuffSendHandler());
    }

    @Override
    public void select(SerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case NATIVE: {
                HANDLER.getInstance(NativeSendHandler.class).handle(pipeline);
                break;
            }
            case PROTOSTUFF: {
                HANDLER.getInstance(ProtostuffSendHandler.class).handle(pipeline);
                break;
            }
            default: {
                throw new RuntimeException();
            }
        }
    }

}

