package com.github.serialize;

import io.netty.channel.ChannelPipeline;

public interface RpcSerializeFrame {
    void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline);
}

