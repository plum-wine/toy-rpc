package com.github;

import com.github.exception.InvokeTimeoutException;
import com.github.services.PersonManage;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PojoTimeoutCallTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-client.xml");

        PersonManage manage = (PersonManage) context.getBean("personManage");

        // TODO: 2017/10/28
        //NettyRPC default timeout is 30s.you can define it by rpc.default.msg.timeout environment variable.
        //if rpc call timeout,NettyRPC can throw InvokeTimeoutException.
        try {
            long timeout = 32L;
            manage.query(timeout);
        } catch (InvokeTimeoutException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            context.destroy();
        }
    }
}

