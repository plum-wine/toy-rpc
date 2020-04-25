package com.github.serialize;

import io.netty.channel.ChannelPipeline;

public interface RpcSerializeFrame {
    void select(SerializeProtocol protocol, ChannelPipeline pipeline);
}

