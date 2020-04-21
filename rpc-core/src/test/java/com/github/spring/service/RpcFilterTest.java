package com.github.spring.service;

import com.github.exception.RejectResponeException;
import com.github.services.Cache;
import com.github.services.Store;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class RpcFilterTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-client.xml");

        Cache cache = (Cache) context.getBean("cache");

        for (int i = 0; i < 100; i++) {
            String obj = String.valueOf(i);
            try {
                cache.put(obj, obj);
            } catch (RejectResponeException ex) {
                System.out.println("trace:" + ex.getMessage());
            }
        }

        for (int i = 0; i < 100; i++) {
            String obj = String.valueOf(i);
            try {
                System.out.println((String) cache.get(obj));
            } catch (RejectResponeException ex) {
                System.out.println("trace:" + ex.getMessage());
            }
        }

        Store store = (Store) context.getBean("store");

        for (int i = 0; i < 100; i++) {
            String obj = String.valueOf(i);
            try {
                store.save(obj);
                store.save(i);
            } catch (RejectResponeException ex) {
                System.out.println("trace:" + ex.getMessage());
            }
        }

        context.destroy();
    }
}

