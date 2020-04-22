package com.github.serialize;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum RpcSerializeProtocol {

    JDK_SERIALIZE("native"),
    PROTOSTUFF_SERIALIZE("protostuff");

    private String serializeProtocol;

    RpcSerializeProtocol(String serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }

}
