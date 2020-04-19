package com.github.entity;

import lombok.Data;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;

@Data
public class MessageResponse implements Serializable {

    private String messageId;

    private String error;

    private Object result;

    private boolean returnNotNull;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}

