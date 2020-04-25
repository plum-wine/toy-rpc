package com.github.netty.server;

import com.github.netty.serialize.RpcRecvSerializeFrame;
import com.github.serialize.SerializeProtocol;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;


public class MessageRecvChannelInitializer extends ChannelInitializer<SocketChannel> {

    private SerializeProtocol protocol;

    private final RpcRecvSerializeFrame frame;

    MessageRecvChannelInitializer buildRpcSerializeProtocol(SerializeProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    MessageRecvChannelInitializer(Map<String, Object> handlerMap) {
        frame = new RpcRecvSerializeFrame(handlerMap);
    }

    /**
     * netty的初始化channel
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        frame.select(protocol, pipeline);
    }

}
