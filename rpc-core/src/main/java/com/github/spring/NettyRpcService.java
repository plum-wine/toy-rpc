package com.github.spring;

import com.github.event.ServerStartEvent;
import com.github.filter.Filter;
import com.github.filter.ServiceFilterBinder;
import com.github.netty.MessageRecvExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;


public class NettyRpcService implements ApplicationContextAware, ApplicationListener {
    private String interfaceName;
    private String ref;
    private String filter;
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        ServiceFilterBinder binder = new ServiceFilterBinder();

        if (StringUtils.isBlank(filter) || !(applicationContext.getBean(filter) instanceof Filter)) {
            binder.setObject(applicationContext.getBean(ref));
        } else {
            binder.setObject(applicationContext.getBean(ref));
            binder.setFilter((Filter) applicationContext.getBean(filter));
        }

        MessageRecvExecutor.getInstance().getHandlerMap().put(interfaceName, binder);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
        applicationContext.publishEvent(new ServerStartEvent(new Object()));
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
}
