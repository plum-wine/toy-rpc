package com.github.netty.server.proxy;

import com.github.entity.MessageRequest;
import lombok.Data;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.time.StopWatch;

@Data
public class MethodInvoker {

    private Object serviceBean;

    private StopWatch sw = new StopWatch();

    public Object invoke(MessageRequest request) throws Throwable {
        String methodName = request.getMethodName();
        Object[] parameters = request.getParametersVal();
        sw.reset();
        sw.start();
        // 服务端执行请求
        Object result = MethodUtils.invokeMethod(serviceBean, methodName, parameters);
        sw.stop();
        return result;
    }

    public long getInvokeTimespan() {
        return sw.getTime();
    }

}

