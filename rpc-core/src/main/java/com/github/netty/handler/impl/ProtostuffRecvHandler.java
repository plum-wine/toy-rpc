package com.github.netty.handler.impl;

import com.github.netty.handler.NettyRpcRecvHandler;
import com.github.netty.server.MessageRecvHandler;
import com.github.serialize.protostuff.ProtostuffCodecUtil;
import com.github.serialize.protostuff.ProtostuffDecoder;
import com.github.serialize.protostuff.ProtostuffEncoder;
import io.netty.channel.ChannelPipeline;

import java.util.Map;


public class ProtostuffRecvHandler implements NettyRpcRecvHandler {

    @Override
    public void handle(Map<String, Object> handlerMap, ChannelPipeline pipeline) {
        ProtostuffCodecUtil util = new ProtostuffCodecUtil();
        util.setRpcDirect(true);

        // 设置netty的处理链条,Protostuff实现了netty的编/解码接口
        pipeline.addLast(new ProtostuffEncoder(util));
        pipeline.addLast(new ProtostuffDecoder(util));

        pipeline.addLast(new MessageRecvHandler(handlerMap));
    }

}

