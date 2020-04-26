package com.github.test.client;

import com.github.netty.client.MessageSendExecutor;
import com.github.serialize.SerializeProtocol;
import com.github.test.CommonConfig;
import com.github.test.entity.Student;
import com.github.test.service.ExceptionService;
import com.github.test.service.HelloService;
import com.github.test.service.StudentService;
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
    public void testRequest() {
        HelloService execute = MessageSendExecutor.getInstance().execute(HelloService.class);
        MessageSendExecutor.getInstance().load(CommonConfig.ipAddr, SerializeProtocol.valueOf(CommonConfig.SERIALIZE));
        String result = execute.sayHello();
        LOGGER.info("result:{}", result);
    }

    @Test
    public void testForeach() {
        StudentService studentService = MessageSendExecutor.getInstance().execute(StudentService.class);
        MessageSendExecutor.getInstance().load(CommonConfig.ipAddr, SerializeProtocol.valueOf(CommonConfig.SERIALIZE));
        for (int i = 0; i < 100; i++) {
            Student student = studentService.queryStudent(1);
            LOGGER.info("stu:{}", student);
        }
    }

    @Test
    public void testException() {
        ExceptionService execute = MessageSendExecutor.getInstance().execute(ExceptionService.class);
        MessageSendExecutor.getInstance().load(CommonConfig.ipAddr, SerializeProtocol.valueOf(CommonConfig.SERIALIZE));
        String result = execute.throwException();
        LOGGER.info("result:{}", result);
    }

}
