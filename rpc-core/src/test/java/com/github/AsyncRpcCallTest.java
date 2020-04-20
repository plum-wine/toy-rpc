
package com.github;

import com.github.async.AsyncCallObject;
import com.github.async.AsyncCallback;
import com.github.async.AsyncInvoker;
import com.github.services.CostTimeCalculate;
import com.github.services.pojo.CostTime;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class AsyncRpcCallTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-client.xml");

        final CostTimeCalculate calculate = (CostTimeCalculate) context.getBean("costTime");

        long start = 0, end = 0;
        start = System.currentTimeMillis();

        AsyncInvoker invoker = new AsyncInvoker();

        CostTime elapse0 = invoker.submit(new AsyncCallback<CostTime>() {
            @Override
            public CostTime call() {
                return calculate.calculate();
            }
        });

        CostTime elapse1 = invoker.submit(new AsyncCallback<CostTime>() {
            @Override
            public CostTime call() {
                return calculate.calculate();
            }
        });

        CostTime elapse2 = invoker.submit(new AsyncCallback<CostTime>() {
            @Override
            public CostTime call() {
                return calculate.calculate();
            }
        });

        System.out.println("1 async rpc call:[" + "result:" + elapse0 + ", status:[" + ((AsyncCallObject) elapse0)._getStatus() + "]");
        System.out.println("2 async rpc call:[" + "result:" + elapse1 + ", status:[" + ((AsyncCallObject) elapse1)._getStatus() + "]");
        System.out.println("3 async rpc call:[" + "result:" + elapse2 + ", status:[" + ((AsyncCallObject) elapse2)._getStatus() + "]");

        end = System.currentTimeMillis();

        System.out.println("rpc async calculate time:" + (end - start));

        context.destroy();
    }
}

