package com.github.netty.client;

import com.github.netty.serialize.RpcSendSerializeFrame;
import com.github.serialize.SerializeProtocol;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


public class MessageSendChannelInitializer extends ChannelInitializer<SocketChannel> {

    private SerializeProtocol protocol;

    private final RpcSendSerializeFrame frame = new RpcSendSerializeFrame();

    MessageSendChannelInitializer buildRpcSerializeProtocol(SerializeProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        frame.select(protocol, pipeline);
    }

}
