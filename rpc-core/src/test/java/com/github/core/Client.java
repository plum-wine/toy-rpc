package com.github.core;

import com.github.core.entity.Student;
import com.github.core.service.HelloService;
import com.github.core.service.StudentService;
import com.github.netty.client.MessageSendExecutor;
import com.github.serialize.SerializeProtocol;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * @author hangs.zhang
 * @date 2020/4/22 下午12:45
 * *********************
 * function:
 */
public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    public void client() throws Exception {
        HelloService execute = MessageSendExecutor.getInstance().execute(HelloService.class);
        StudentService studentService = MessageSendExecutor.getInstance().execute(StudentService.class);

        MessageSendExecutor.getInstance().load(CommonConfig.ipAddr, SerializeProtocol.valueOf(CommonConfig.SERIALIZE));

        String result = execute.sayHello();
        LOGGER.info("result:{}", result);

        Student student = studentService.queryStudent(1);
        LOGGER.info("stu:{}", student);
    }

}
