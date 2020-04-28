package com.github.netty.client;

import com.github.core.MessageCallBack;
import com.github.entity.MessageRequest;
import com.github.entity.MessageResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MessageSendHandler extends ChannelInboundHandlerAdapter {

    private final ConcurrentHashMap<String, MessageCallBack> callBackMap = new ConcurrentHashMap<>();

    private volatile Channel channel;

    private SocketAddress remoteAddr;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        remoteAddr = channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        channel = ctx.channel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        MessageResponse response = (MessageResponse) msg;
        String messageId = response.getMessageId();
        // 根据messageId操作future
        MessageCallBack callBack = callBackMap.get(messageId);
        if (callBack != null) {
            callBackMap.remove(messageId);
            // 收到服务端响应
            callBack.over(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    public MessageCallBack sendRequest(MessageRequest request) {
        MessageCallBack callBack = new MessageCallBack();
        callBackMap.put(request.getMessageId(), callBack);
        channel.writeAndFlush(request);
        return callBack;
    }

}
