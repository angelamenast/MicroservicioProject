package com.unicauca.ProjectManagement.service;

import com.unicauca.ProjectManagement.entity.EnumProgram;
import com.unicauca.ProjectManagement.entity.EnumRole;
import com.unicauca.ProjectManagement.entity.Professor;
import com.unicauca.ProjectManagement.infra.config.RabbitMQConfig;
import com.unicauca.ProjectManagement.infra.dto.ProfessorEvent;
import com.unicauca.ProjectManagement.repository.ProfessorRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfessorConsumerService {
    @Autowired
    private final ProfessorRepository professorRepository;

    public ProfessorConsumerService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.PROFESSOR_QUEUE)
    public void receiveProfessor(ProfessorEvent professor) throws Exception {
        try {
            Professor professorSaved = new Professor();
            professorSaved.setId(professor.getId());
            professorSaved.setName(professor.getName());
            professorSaved.setLastName(professor.getLastName());
            professorSaved.setPhoneNumber(professor.getPhoneNumber());
            professorSaved.setProgram(EnumProgram.valueOf(professor.getProgram()));
            professorSaved.setOffice(professor.getOffice());
            professorSaved.setEmail(professor.getUserRequest().getEmail());
            professorSaved.setPassword(professor.getUserRequest().getPassword());

            List<EnumRole> rolesList = new ArrayList<>();

            for (String role : professor.getUserRequest().getRoles()) {
                rolesList.add(EnumRole.valueOf(role));
            }

            professorSaved.setRoleType(rolesList);

            professorRepository.save(professorSaved);

            System.out.println("Professor received");

        } catch (Exception e) {
            throw new Exception("Error receiving a professor: " + e.getMessage());
        }
    }
}
