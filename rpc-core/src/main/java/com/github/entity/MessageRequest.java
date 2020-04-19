package com.github.entity;

import lombok.Data;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;

@Data
public class MessageRequest implements Serializable {

    private String messageId;

    private String className;

    private String methodName;

    private Class<?>[] typeParameters;

    private Object[] parametersVal;

    private boolean invokeMetrics = true;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, "typeParameters", "parametersVal");
    }

}

