package com.unicauca.ProjectManagement.infra.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProfessorEvent {
     
    private Long id;
     
    private String name;
     
    private String lastName;
     
    private String phoneNumber;
     
    private String program;
     
    private UserRequest userRequest;
     
    private String office;

}
