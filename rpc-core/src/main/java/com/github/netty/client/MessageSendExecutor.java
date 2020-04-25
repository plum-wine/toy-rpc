package com.github.netty.client;

import com.github.serialize.SerializeProtocol;
import com.google.common.reflect.Reflection;

/**
 * 请求发送执行器
 */
public class MessageSendExecutor {

    private static class MessageSendExecutorHolder {
        private static final MessageSendExecutor INSTANCE = new MessageSendExecutor();
    }

    public static MessageSendExecutor getInstance() {
        return MessageSendExecutorHolder.INSTANCE;
    }

    private MessageSendExecutor() {
    }

    private final ClientLoader loader = ClientLoader.getInstance();

    public void load(String serverAddress, SerializeProtocol serializeProtocol) {
        loader.load(serverAddress, serializeProtocol);
    }

    public void stop() {
        loader.unLoad();
    }

    public <T> T execute(Class<T> rpcInterface) {
        return Reflection.newProxy(rpcInterface, new MessageSendProxy<T>());
    }

}

