package com.github.core;

import com.github.exception.InvokeModuleException;
import com.github.exception.InvokeTimeoutException;
import com.github.exception.RejectResponeException;
import com.github.model.MessageRequest;
import com.github.model.MessageResponse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageCallBack {

    private MessageRequest request;
    private MessageResponse response;
    private Lock lock = new ReentrantLock();
    private Condition finish = lock.newCondition();

    public MessageCallBack(MessageRequest request) {
        this.request = request;
    }

    public Object start() {
        try {
            lock.lock();
            await();
            if (this.response != null) {
                boolean isInvokeSucc = getInvokeResult();
                if (isInvokeSucc) {
                    if (this.response.getError().isEmpty()) {
                        return this.response.getResult();
                    } else {
                        throw new InvokeModuleException(this.response.getError());
                    }
                } else {
                    throw new RejectResponeException(RpcSystemConfig.FILTER_RESPONSE_MSG);
                }
            } else {
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

    public void over(MessageResponse response) {
        try {
            lock.lock();
            finish.signal();
            this.response = response;
        } finally {
            lock.unlock();
        }
    }

    private void await() {
        boolean timeout = false;
        try {
            timeout = finish.await(RpcSystemConfig.SYSTEM_PROPERTY_MESSAGE_CALLBACK_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!timeout) {
            throw new InvokeTimeoutException(RpcSystemConfig.TIMEOUT_RESPONSE_MSG);
        }
    }

    private boolean getInvokeResult() {
        return (!this.response.getError().equals(RpcSystemConfig.FILTER_RESPONSE_MSG) &&
                (!this.response.isReturnNotNull() || (this.response.isReturnNotNull() && this.response.getResult() != null)));
    }
}
