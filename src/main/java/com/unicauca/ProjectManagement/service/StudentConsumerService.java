package com.unicauca.ProjectManagement.service;

import com.unicauca.ProjectManagement.entity.EnumProgram;
import com.unicauca.ProjectManagement.entity.EnumRole;
import com.unicauca.ProjectManagement.entity.Student;
import com.unicauca.ProjectManagement.infra.config.RabbitMQConfig;
import com.unicauca.ProjectManagement.infra.dto.StudentEvent;
import com.unicauca.ProjectManagement.repository.StudentRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentConsumerService {
    @Autowired
    private final StudentRepository studentRepository;

    public StudentConsumerService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.STUDENT_QUEUE)
    public void receiveStudent(StudentEvent student) throws Exception {
        try {

            Student studentSaved = new Student();

            studentSaved.setId(student.getId());
            studentSaved.setName(student.getName());
            studentSaved.setLastName(student.getLastName());
            studentSaved.setPhoneNumber(student.getPhoneNumber());
            studentSaved.setProgram(EnumProgram.valueOf(student.getProgram()));
            studentSaved.setStudentCode(student.getStudentCode());
            studentSaved.setEmail(student.getUserRequest().getEmail());
            studentSaved.setPassword(student.getUserRequest().getPassword());

            List<EnumRole> rolesList = new ArrayList<>();

            for (String role : student.getUserRequest().getRoles()) {
                rolesList.add(EnumRole.valueOf(role));
            }

            studentSaved.setRoleType(rolesList);

            studentRepository.save(studentSaved);

            System.out.println("Student received");

        } catch(Exception e){
            throw new Exception("Error receiving a professor: " + e.getMessage());
        }
    }
}
