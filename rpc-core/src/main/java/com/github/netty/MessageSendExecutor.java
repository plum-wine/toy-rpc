package com.github.netty;

import com.google.common.reflect.Reflection;
import com.github.serialize.RpcSerializeProtocol;

public class MessageSendExecutor {

    private static class MessageSendExecutorHolder {
        private static final MessageSendExecutor INSTANCE = new MessageSendExecutor();
    }

    public static MessageSendExecutor getInstance() {
        return MessageSendExecutorHolder.INSTANCE;
    }

    private final RpcServerLoader loader = RpcServerLoader.getInstance();

    private MessageSendExecutor() {

    }

    public MessageSendExecutor(String serverAddress, RpcSerializeProtocol serializeProtocol) {
        loader.load(serverAddress, serializeProtocol);
    }

    public void setRpcServerLoader(String serverAddress, RpcSerializeProtocol serializeProtocol) {
        loader.load(serverAddress, serializeProtocol);
    }

    public void stop() {
        loader.unLoad();
    }

    public <T> T execute(Class<T> rpcInterface) {
        return Reflection.newProxy(rpcInterface, new MessageSendProxy<T>());
    }

}

