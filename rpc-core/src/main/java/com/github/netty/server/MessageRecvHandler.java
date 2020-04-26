package com.github.netty.server;

import com.github.model.MessageRequest;
import com.github.model.MessageResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * ChannelInboundHandlerAdapter netty的入站处理器接口
 */
public class MessageRecvHandler extends ChannelInboundHandlerAdapter {

    private final Map<String, Object> handlerMap;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public MessageRecvHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * 接受消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        MessageRequest request = (MessageRequest) msg;
        MessageResponse response = new MessageResponse();
        Callable<Boolean> recvTask = new MessageRecvInitializeTask(request, response, handlerMap);
        // 请求处理
        MessageRecvExecutor.submit(recvTask, ctx, request, response);
    }

    /**
     * catch异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("MessageRecvHandler.exceptionCaught", cause);
        ctx.close();
    }

}

