package com.github.spring;

import com.github.spring.annotation.RpcReference;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author hangs.zhang
 * @date 2020/04/21 22:20
 * *****************
 * function:
 */
public class RpcReferenceRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RpcReference.class.getName()));
        String interfaceName = annoAttrs.getString("interfaceName");
        String id = annoAttrs.getString("id");
        String ipAddr = annoAttrs.getString("ipAddr");
        String protocolType = annoAttrs.getString("protocol");

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().addPropertyValue("interfaceName", interfaceName);
        beanDefinition.getPropertyValues().addPropertyValue("ipAddr", ipAddr);
        beanDefinition.getPropertyValues().addPropertyValue("protocol", protocolType);
        // 注册该BeanDefinition
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, id);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, beanDefinitionRegistry);
    }

}
