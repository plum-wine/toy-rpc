package com.github.core.service;

import com.github.core.entity.Student;

import java.util.UUID;

/**
 * @author hangs.zhang
 * @date 2020/4/24 下午12:49
 * *********************
 * function:
 */
public class IStudentService implements StudentService {

    @Override
    public Student queryStudent(Integer id) {
        return new Student(1, UUID.randomUUID().toString());
    }

}
