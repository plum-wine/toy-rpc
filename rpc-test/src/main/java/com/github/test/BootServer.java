package com.github.test;

import com.github.netty.server.MessageRecvExecutor;
import com.github.serialize.SerializeProtocol;
import com.github.core.filter.ServiceFilterBinder;
import com.github.test.filter.SimpleFilter;
import com.github.test.service.ExceptionService;
import com.github.test.service.HelloService;
import com.github.test.service.StudentService;
import com.github.test.service.impl.IExceptionService;
import com.github.test.service.impl.IHelloService;
import com.github.test.service.impl.IStudentService;

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

        ExceptionService exceptionService = new IExceptionService();
        ServiceFilterBinder binder3 = new ServiceFilterBinder();
        binder3.setObject(exceptionService);
        binder3.setFilter(filter);
        MessageRecvExecutor.getInstance().getHandlerMap().put(ExceptionService.class.getCanonicalName(), binder3);
    }

}
