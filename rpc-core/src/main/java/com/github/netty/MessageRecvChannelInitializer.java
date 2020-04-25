package com.github.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;

import com.github.serialize.SerializeProtocol;


public class MessageRecvChannelInitializer extends ChannelInitializer<SocketChannel> {

    private SerializeProtocol protocol;

    private RpcRecvSerializeFrame frame = null;

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
