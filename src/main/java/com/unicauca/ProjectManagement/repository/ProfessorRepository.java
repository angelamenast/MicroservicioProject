package com.unicauca.ProjectManagement.repository;

import com.unicauca.ProjectManagement.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor,Long> {
    

}
