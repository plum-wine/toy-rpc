package com.github.core;

import com.github.core.filter.SimpleFilter;
import com.github.core.service.HelloService;
import com.github.core.service.IHelloService;
import com.github.core.service.IStudentService;
import com.github.core.service.StudentService;
import com.github.filter.ServiceFilterBinder;
import com.github.netty.server.MessageRecvExecutor;
import com.github.serialize.SerializeProtocol;

/**
 * @author hangs.zhang
 * @date 2020/04/21 23:04
 * *****************
 * function:
 */
public class BootServer {

    public static void main(String[] args) {
        MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
        ref.setServerAddress(CommonConfig.ipAddr);
        ref.setEchoApiPort(Integer.parseInt(CommonConfig.echoApiPort));
        ref.setSerializeProtocol(Enum.valueOf(SerializeProtocol.class, CommonConfig.SERIALIZE));
        ref.start();

        SimpleFilter filter = new SimpleFilter();

        HelloService helloService = new IHelloService();
        ServiceFilterBinder binder = new ServiceFilterBinder();
        binder.setObject(helloService);
        binder.setFilter(filter);
        MessageRecvExecutor.getInstance().getHandlerMap().put(HelloService.class.getCanonicalName(), binder);

        StudentService studentService = new IStudentService();
        ServiceFilterBinder binder2 = new ServiceFilterBinder();
        binder2.setObject(studentService);
        binder2.setFilter(filter);
        MessageRecvExecutor.getInstance().getHandlerMap().put(StudentService.class.getCanonicalName(), binder2);
    }

}
