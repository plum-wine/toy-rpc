package com.github.netty.server;

import com.github.netty.handler.NettyRpcRecvHandler;
import com.github.netty.handler.JdkNativeRecvHandler;
import com.github.netty.handler.ProtostuffRecvHandler;
import com.github.serialize.RpcSerializeFrame;
import com.github.serialize.SerializeProtocol;
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
    public void select(SerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case JDK_NATIVE: {
                handler.getInstance(JdkNativeRecvHandler.class).handle(handlerMap, pipeline);
                break;
            }
            case PROTOSTUFF: {
                handler.getInstance(ProtostuffRecvHandler.class).handle(handlerMap, pipeline);
                break;
            }
            default: {
                break;
            }
        }
    }
}
