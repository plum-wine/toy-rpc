package com.github.netty.client;

import com.github.core.SystemConfig;
import com.github.serialize.SerializeProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


public class MessageSendInitializeTask implements Callable<Boolean> {

    private final EventLoopGroup eventLoopGroup;

    private final InetSocketAddress serverAddress;

    private final SerializeProtocol protocol;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    MessageSendInitializeTask(EventLoopGroup eventLoopGroup, InetSocketAddress serverAddress, SerializeProtocol protocol) {
        this.eventLoopGroup = eventLoopGroup;
        this.serverAddress = serverAddress;
        this.protocol = protocol;
    }

    @Override
    public Boolean call() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .remoteAddress(serverAddress);
        bootstrap.handler(new MessageSendChannelInitializer().buildRpcSerializeProtocol(protocol));

        ChannelFuture channelFuture = bootstrap.connect();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    MessageSendHandler handler = channelFuture.channel().pipeline().get(MessageSendHandler.class);
                    ClientLoader.getInstance().setMessageSendHandler(handler);
                } else {
                    eventLoopGroup.schedule(new Runnable() {
                        @Override
                        public void run() {
                            LOGGER.error("RPC server is down,start to reconnecting to: {}:{}", serverAddress.getAddress().getHostAddress(), serverAddress.getPort());
                            // 10s重试一次,不停重试
                            call();
                        }
                    }, SystemConfig.SYSTEM_PROPERTY_CLIENT_RECONNECT_DELAY, TimeUnit.SECONDS);
                }
            }
        });
        return Boolean.TRUE;
    }
}
