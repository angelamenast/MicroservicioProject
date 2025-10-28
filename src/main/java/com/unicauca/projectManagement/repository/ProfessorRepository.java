package com.unicauca.projectManagement.repository;

import com.unicauca.projectManagement.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor,Long> {
    

}
