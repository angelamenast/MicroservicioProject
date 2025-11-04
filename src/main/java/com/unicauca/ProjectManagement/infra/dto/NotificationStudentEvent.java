package com.unicauca.ProjectManagement.infra.dto;

import lombok.Getter;
import lombok.Setter;

public class NotificationStudentEvent {
    @Getter
    @Setter
    private String name;
    @Getter @Setter
    private String lastName;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private String studentCode;
}
