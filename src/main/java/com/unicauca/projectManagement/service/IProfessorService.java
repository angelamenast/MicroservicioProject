package com.unicauca.projectManagement.service;

import com.unicauca.projectManagement.entity.Professor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface IProfessorService {

    @Transactional
    Optional<Professor> findById(Long id) throws Exception;

    @Transactional
    List<Professor> findAllProfessors() throws Exception;

}
