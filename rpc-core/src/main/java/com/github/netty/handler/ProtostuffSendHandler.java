package com.github.netty.handler;

import com.github.netty.MessageSendHandler;
import com.github.serialize.protostuff.ProtostuffCodecUtil;
import com.github.serialize.protostuff.ProtostuffDecoder;
import com.github.serialize.protostuff.ProtostuffEncoder;
import io.netty.channel.ChannelPipeline;


public class ProtostuffSendHandler implements NettyRpcSendHandler {
    @Override
    public void handle(ChannelPipeline pipeline) {
        ProtostuffCodecUtil util = new ProtostuffCodecUtil();
        util.setRpcDirect(false);
        pipeline.addLast(new ProtostuffEncoder(util));
        pipeline.addLast(new ProtostuffDecoder(util));
        pipeline.addLast(new MessageSendHandler());
    }
}
