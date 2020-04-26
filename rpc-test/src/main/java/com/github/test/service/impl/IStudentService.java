package com.github.test.service.impl;

import com.github.test.entity.Student;
import com.github.test.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.UUID;

/**
 * @author hangs.zhang
 * @date 2020/4/24 下午12:49
 * *********************
 * function:
 */
public class IStudentService implements StudentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public Student queryStudent(Integer id) {
        long start = System.currentTimeMillis();
        Student student = new Student(1, UUID.randomUUID().toString());
        LOGGER.info("cost time:{}", (System.currentTimeMillis() - start));
        return student;
    }

}
