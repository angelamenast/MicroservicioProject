package com.unicauca.ProjectManagement.service;

import com.unicauca.ProjectManagement.entity.*;
import com.unicauca.ProjectManagement.entity.Professor;
import com.unicauca.ProjectManagement.repository.ProfessorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService implements IProfessorService {

    @Autowired
    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @Override
    @Transactional
    public Optional<Professor> findById(Long id) throws Exception{
        try {
            return professorRepository.findById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public List<Professor> findAllProfessors() throws Exception {
        try {
            return professorRepository.findAll();
        } catch (Exception e) {
            throw new Exception("Error listing professors: " + e.getMessage());
        }
    }
}
