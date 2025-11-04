package com.unicauca.ProjectManagement.repository;

import com.unicauca.ProjectManagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,Long> {

}
