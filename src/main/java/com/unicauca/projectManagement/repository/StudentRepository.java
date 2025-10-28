package com.unicauca.projectManagement.repository;

import com.unicauca.projectManagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,Long> {

}
