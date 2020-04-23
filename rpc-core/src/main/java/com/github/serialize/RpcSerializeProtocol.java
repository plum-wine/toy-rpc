package com.github.serialize;

import lombok.Getter;

@Getter
public enum RpcSerializeProtocol {

    JDK_SERIALIZE("native"),
    PROTOSTUFF_SERIALIZE("protostuff");

    private final String serializeProtocol;

    RpcSerializeProtocol(String serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }

    @Override
    public String toString() {
        return serializeProtocol;
    }

}
