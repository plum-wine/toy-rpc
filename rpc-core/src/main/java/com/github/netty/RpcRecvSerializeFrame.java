package com.github.netty;

import com.github.netty.handler.NettyRpcRecvHandler;
import com.github.netty.handler.JdkNativeRecvHandler;
import com.github.netty.handler.ProtostuffRecvHandler;
import com.github.serialize.RpcSerializeFrame;
import com.github.serialize.RpcSerializeProtocol;
import io.netty.channel.ChannelPipeline;

import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

public class RpcRecvSerializeFrame implements RpcSerializeFrame {

    private Map<String, Object> handlerMap = null;

    public RpcRecvSerializeFrame(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    private static ClassToInstanceMap<NettyRpcRecvHandler> handler = MutableClassToInstanceMap.create();

    static {
        handler.putInstance(JdkNativeRecvHandler.class, new JdkNativeRecvHandler());
        handler.putInstance(ProtostuffRecvHandler.class, new ProtostuffRecvHandler());
    }

    @Override
    public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case JDKSERIALIZE: {
                handler.getInstance(JdkNativeRecvHandler.class).handle(handlerMap, pipeline);
                break;
            }
            case PROTOSTUFFSERIALIZE: {
                handler.getInstance(ProtostuffRecvHandler.class).handle(handlerMap, pipeline);
                break;
            }
            default: {
                break;
            }
        }
    }
}
