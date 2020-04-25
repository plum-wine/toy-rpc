package com.github.netty;

import com.github.core.RpcSystemConfig;
import com.github.parallel.RpcThreadPool;
import com.github.serialize.RpcSerializeProtocol;
import com.google.common.util.concurrent.*;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class RpcServerLoader {

    private static volatile RpcServerLoader rpcServerLoader;

    private static final String DELIMITER = RpcSystemConfig.DELIMITER;

    // 默认序列化方式为native
    // private RpcSerializeProtocol serializeProtocol = RpcSerializeProtocol.JDK_SERIALIZE;

    private static final int PARALLEL = RpcSystemConfig.SYSTEM_PROPERTY_PARALLEL * 2;

    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(PARALLEL);

    private static final int threadNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS;

    private static final int queueNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS;

    private static final ListeningExecutorService threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) RpcThreadPool.getExecutor(threadNums, queueNums));

    private MessageSendHandler messageSendHandler = null;

    private final Lock lock = new ReentrantLock();

    private final Condition connectStatus = lock.newCondition();

    private final Condition handlerStatus = lock.newCondition();

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RpcServerLoader() {
    }

    public static RpcServerLoader getInstance() {
        if (rpcServerLoader == null) {
            synchronized (RpcServerLoader.class) {
                if (rpcServerLoader == null) {
                    rpcServerLoader = new RpcServerLoader();
                }
            }
        }
        return rpcServerLoader;
    }

    public void load(String serverAddress, RpcSerializeProtocol serializeProtocol) {
        String[] ipAddr = serverAddress.split(RpcServerLoader.DELIMITER);
        if (ipAddr.length == RpcSystemConfig.IPADDR_OPRT_ARRAY_LENGTH) {
            String host = ipAddr[0];
            int port = Integer.parseInt(ipAddr[1]);
            final InetSocketAddress remoteAddr = new InetSocketAddress(host, port);

            LOGGER.info("RPC Client start success!");
            LOGGER.info("ip:{},port:{},protocol:{}", host, port, serializeProtocol);

            ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(new MessageSendInitializeTask(eventLoopGroup, remoteAddr, serializeProtocol));

            Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    try {
                        lock.lock();
                        if (messageSendHandler == null) {
                            handlerStatus.await();
                        }
                        if (result.equals(Boolean.TRUE) && messageSendHandler != null) {
                            connectStatus.signalAll();
                        }
                    } catch (InterruptedException ex) {
                        LOGGER.info("error", ex);
                    } finally {
                        lock.unlock();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    LOGGER.error("send request onFailure", t);
                }
            }, threadPoolExecutor);
        } else {
            throw new RuntimeException();
        }
    }

    public void setMessageSendHandler(MessageSendHandler messageInHandler) {
        try {
            lock.lock();
            this.messageSendHandler = messageInHandler;
            handlerStatus.signal();
        } finally {
            lock.unlock();
        }
    }

    public MessageSendHandler getMessageSendHandler() throws InterruptedException {
        try {
            lock.lock();
            if (messageSendHandler == null) {
                connectStatus.await();
            }
            return messageSendHandler;
        } finally {
            lock.unlock();
        }
    }

    public void unLoad() {
        messageSendHandler.close();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }

}
