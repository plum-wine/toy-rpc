package com.github.netty.server;

import com.github.core.SystemConfig;
import com.github.entity.MessageRequest;
import com.github.entity.MessageResponse;
import com.github.parallel.NamedThreadFactory;
import com.github.parallel.RpcThreadPool;
import com.github.serialize.SerializeProtocol;
import com.google.common.util.concurrent.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.nio.channels.spi.SelectorProvider;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

@Data
public class MessageRecvExecutor {

    private String serverAddress;

    private SerializeProtocol serializeProtocol = SerializeProtocol.NATIVE;

    /**
     * 切割ip与port
     */
    private static final String DELIMITER = SystemConfig.DELIMITER;

    // 两倍处理器数量
    private static final int PARALLEL = SystemConfig.SYSTEM_PROPERTY_PARALLEL * 2;

    private static int threadNums = SystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS;

    private static int queueNums = SystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS;

    /**
     * guava的线程池,能够添加监听器
     * 用于处理请求
     */
    private static volatile ListeningExecutorService threadPoolExecutor;

    private Map<String, Object> handlerMap = new ConcurrentHashMap<>();

    private int numberOfEchoThreadsPool = 1;

    private ThreadFactory threadRpcFactory = new NamedThreadFactory("Toy-ThreadFactory");

    private EventLoopGroup boss = new NioEventLoopGroup();

    private EventLoopGroup worker = new NioEventLoopGroup(PARALLEL, threadRpcFactory, SelectorProvider.provider());

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private MessageRecvExecutor() {
        handlerMap.clear();
        register();
    }

    /**
     * 单例
     */
    private static class MessageRecvExecutorHolder {
        static final MessageRecvExecutor INSTANCE = new MessageRecvExecutor();
    }

    public static MessageRecvExecutor getInstance() {
        return MessageRecvExecutorHolder.INSTANCE;
    }

    public static void submit(Callable<Boolean> task, final ChannelHandlerContext ctx, final MessageRequest request, final MessageResponse response) {
        // 保证threadPoolExecutor的单例
        // 双重检查
        if (threadPoolExecutor == null) {
            synchronized (MessageRecvExecutor.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) RpcThreadPool.getExecutor(threadNums, queueNums));
                }
            }
        }

        // 提交任务
        ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(task);
        Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                // 将消息刷新到client
                ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        LOGGER.info("RPC Server Send message-id response:{}", request.getMessageId());
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error("send response failure", throwable);
            }
        }, threadPoolExecutor);
    }

    public void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new MessageRecvChannelInitializer(handlerMap).buildRpcSerializeProtocol(serializeProtocol))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] ipAddr = serverAddress.split(MessageRecvExecutor.DELIMITER);

            if (ipAddr.length == SystemConfig.IPADDR_OPRT_ARRAY_LENGTH) {
                final String host = ipAddr[0];
                final int port = Integer.parseInt(ipAddr[1]);
                ChannelFuture future = bootstrap.bind(host, port).sync();
                // 启动监听
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            LOGGER.info("RPC Server start success!");
                            LOGGER.info("ip:{} port:{} protocol:{}", host, port, serializeProtocol);
                        }
                    }
                });
            } else {
                throw new RuntimeException("配置错误,请检查");
            }
        } catch (InterruptedException e) {
            LOGGER.info("RPC Server start error!", e);
        }
    }

    /**
     * 关闭server
     */
    public void stop() {
        worker.shutdownGracefully();
        boss.shutdownGracefully();
    }

    private void register() {
    }

}
