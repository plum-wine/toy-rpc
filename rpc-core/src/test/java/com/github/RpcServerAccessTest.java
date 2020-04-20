package com.github;

import com.google.common.io.CharStreams;
import com.github.compiler.AccessAdaptive;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;


public class RpcServerAccessTest {

    public static void main(String[] args) {
        try {
            DefaultResourceLoader resource = new DefaultResourceLoader();
            Reader input = new InputStreamReader(resource.getResource("AccessProvider.tpl").getInputStream(), "UTF-8");
            String javaSource = CharStreams.toString(input);

            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-client.xml");

            AccessAdaptive provider = (AccessAdaptive) context.getBean("access");

            String result = (String) provider.invoke(javaSource, "getRpcServerTime", new Object[]{new String("XiaoHaoBaby")});
            System.out.println(result);

            provider.invoke(javaSource, "sayHello", new Object[0]);

            input.close();
            context.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

