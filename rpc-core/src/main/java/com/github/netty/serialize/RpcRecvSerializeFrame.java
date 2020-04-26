package com.github.netty.serialize;

import com.github.netty.handler.impl.NativeRecvHandler;
import com.github.netty.handler.NettyRpcRecvHandler;
import com.github.netty.handler.impl.ProtostuffRecvHandler;
import com.github.serialize.RpcSerializeFrame;
import com.github.serialize.SerializeProtocol;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import io.netty.channel.ChannelPipeline;

import java.util.Map;

public class RpcRecvSerializeFrame implements RpcSerializeFrame {

    private final Map<String, Object> handlerMap;

    public RpcRecvSerializeFrame(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    private static final ClassToInstanceMap<NettyRpcRecvHandler> handler = MutableClassToInstanceMap.create();

    static {
        handler.putInstance(NativeRecvHandler.class, new NativeRecvHandler());
        handler.putInstance(ProtostuffRecvHandler.class, new ProtostuffRecvHandler());
    }

    @Override
    public void select(SerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case NATIVE: {
                handler.getInstance(NativeRecvHandler.class).handle(handlerMap, pipeline);
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
