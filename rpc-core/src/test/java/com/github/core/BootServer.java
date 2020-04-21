package com.github.core;

import com.github.netty.MessageRecvExecutor;
import com.github.serialize.RpcSerializeProtocol;

/**
 * @author hangs.zhang
 * @date 2020/04/21 23:04
 * *****************
 * function:
 */
public class BootServer {

    public static void main(String[] args) {
        MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
        ref.setServerAddress("127.0.0.1:18887");
        ref.setEchoApiPort(Integer.parseInt("18888"));
        ref.setSerializeProtocol(Enum.valueOf(RpcSerializeProtocol.class, "PROTOSTUFFSERIALIZE"));
        ref.start();
    }

}
