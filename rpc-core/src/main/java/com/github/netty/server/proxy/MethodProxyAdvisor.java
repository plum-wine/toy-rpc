package com.github.netty.server.proxy;

import com.github.core.filter.Filter;
import com.github.core.filter.ServiceFilterBinder;
import com.github.entity.MessageRequest;
import lombok.Data;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Method;
import java.util.Map;

@Data
public class MethodProxyAdvisor implements MethodInterceptor {

    private final Map<String, Object> HANDLERS;

    private boolean returnNotNull = true;

    public MethodProxyAdvisor(Map<String, Object> handlerMap) {
        this.HANDLERS = handlerMap;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] params = invocation.getArguments();
        if (params.length <= 0) {
            return null;
        }

        MessageRequest request = (MessageRequest) params[0];
        String className = request.getClassName();
        // 在织入过程中设置serviceBean
        Object serviceBean = HANDLERS.get(className);
        String methodName = request.getMethodName();
        Object[] parameters = request.getParametersVal();

        // 判断ServiceFilterBinder是否是service的父类
        boolean existFilter = ServiceFilterBinder.class.isAssignableFrom(serviceBean.getClass());
        ((MethodInvoker) invocation.getThis()).setServiceBean(existFilter ? ((ServiceFilterBinder) serviceBean).getObject() : serviceBean);

        if (existFilter) {
            ServiceFilterBinder processors = (ServiceFilterBinder) serviceBean;
            if (processors.getFilter() != null) {
                Filter filter = processors.getFilter();
                Object[] args = ArrayUtils.nullToEmpty(parameters);
                Class<?>[] parameterTypes = ClassUtils.toClass(args);
                Method method = MethodUtils.getMatchingAccessibleMethod(processors.getObject().getClass(), methodName, parameterTypes);
                if (filter.before(method, processors.getObject(), parameters)) {
                    Object result = invocation.proceed();
                    filter.after(method, processors.getObject(), parameters);
                    setReturnNotNull(result != null);
                    return result;
                } else {
                    return null;
                }
            }
        }
        Object result = invocation.proceed();
        setReturnNotNull(result != null);
        return result;
    }

}


