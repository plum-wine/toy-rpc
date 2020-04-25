
package com.github.netty.server;

import com.github.compiler.AccessAdaptiveProvider;
import com.github.core.AbilityDetailProvider;
import com.github.core.RpcSystemConfig;
import com.github.model.MessageKeyVal;
import com.github.model.MessageRequest;
import com.github.model.MessageResponse;
import com.github.netty.resolver.ApiEchoResolver;
import com.github.parallel.NamedThreadFactory;
import com.github.parallel.RpcThreadPool;
import com.github.serialize.SerializeProtocol;
import com.google.common.util.concurrent.*;
import com.sun.istack.internal.NotNull;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.invoke.MethodHandles;
import java.nio.channels.spi.SelectorProvider;
import java.util.Map;
import java.util.concurrent.*;

@Data
public class MessageRecvExecutor implements ApplicationContextAware {

    private String serverAddress;

    private int echoApiPort;

    private SerializeProtocol serializeProtocol = SerializeProtocol.JDK_NATIVE;

    /**
     * 切割ip与port
     */
    private static final String DELIMITER = RpcSystemConfig.DELIMITER;

    private static final int PARALLEL = RpcSystemConfig.SYSTEM_PROPERTY_PARALLEL * 2;

    private static int threadNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS;

    private static int queueNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS;

    /**
     * guava的线程池,能够添加监听器
     * 用于处理请求
     */
    private static volatile ListeningExecutorService threadPoolExecutor;

    private Map<String, Object> handlerMap = new ConcurrentHashMap<>();

    private int numberOfEchoThreadsPool = 1;

    private ThreadFactory threadRpcFactory = new NamedThreadFactory("RPC-ThreadFactory");

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
                    threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) (RpcSystemConfig.isMonitorServerSupport() ?
                            RpcThreadPool.getExecutorWithJmx(threadNums, queueNums) : RpcThreadPool.getExecutor(threadNums, queueNums)));
                }
            }
        }

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
            public void onFailure(@NotNull Throwable throwable) {
                LOGGER.error("send response failure", throwable);
            }
        }, threadPoolExecutor);
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        try {
            MessageKeyVal keyVal = (MessageKeyVal) ctx.getBean(Class.forName("com.github.model.MessageKeyVal"));
            Map<String, Object> rpcServiceObject = keyVal.getMessageKeyVal();
            for (Map.Entry<String, Object> entry : rpcServiceObject.entrySet()) {
                handlerMap.put(entry.getKey(), entry.getValue());
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.error("error", ex);
        }
    }

    public void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new MessageRecvChannelInitializer(handlerMap).buildRpcSerializeProtocol(serializeProtocol))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] ipAddr = serverAddress.split(MessageRecvExecutor.DELIMITER);

            if (ipAddr.length == RpcSystemConfig.IPADDR_OPRT_ARRAY_LENGTH) {
                final String host = ipAddr[0];
                final int port = Integer.parseInt(ipAddr[1]);
                ChannelFuture future = bootstrap.bind(host, port).sync();
                // 启动监听
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            final ExecutorService executor = Executors.newFixedThreadPool(numberOfEchoThreadsPool);
                            ExecutorCompletionService<Boolean> completionService = new ExecutorCompletionService<>(executor);
                            completionService.submit(new ApiEchoResolver(host, echoApiPort));
                            LOGGER.info("RPC Server start success!");
                            LOGGER.info("ip:{} port:{} protocol:{}", host, port, serializeProtocol);
                        }
                    }
                });
            } else {
                LOGGER.info("RPC Server start fail!");
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
        handlerMap.put(RpcSystemConfig.RPC_COMPILER_SPI_ATTR, new AccessAdaptiveProvider());
        handlerMap.put(RpcSystemConfig.RPC_ABILITY_DETAIL_SPI_ATTR, new AbilityDetailProvider());
    }

}