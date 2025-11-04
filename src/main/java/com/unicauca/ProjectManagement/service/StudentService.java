package com.unicauca.ProjectManagement.service;

import com.unicauca.ProjectManagement.entity.*;
import com.unicauca.ProjectManagement.entity.Student;
import com.unicauca.ProjectManagement.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService implements IStudentService {

    @Autowired
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public Optional<Student> findById(Long id) throws Exception {
        try {
            return studentRepository.findById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public List<Student> findAllStudents() throws Exception {
        try {
            return studentRepository.findAll();
        } catch (Exception e) {
            throw new Exception("Error listing students: " + e.getMessage());
        }
    }


}
