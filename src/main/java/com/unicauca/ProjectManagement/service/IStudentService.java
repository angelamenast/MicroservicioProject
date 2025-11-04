package com.unicauca.ProjectManagement.service;

import com.unicauca.ProjectManagement.entity.Student;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface IStudentService {

    @Transactional
    Optional<Student> findById(Long id) throws Exception;

    @Transactional
    List<Student> findAllStudents() throws Exception;

}
