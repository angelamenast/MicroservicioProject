package com.unicauca.ProjectManagement.infra.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NotificationProjectEvent {
    private String title;
    private String generalObjective;
    private List<String> specificObjectives = new ArrayList<>();
    private String modality;
    private List<NotificationStudentEvent> students = new ArrayList<>();
    private NotificationProfessorEvent director;
    private String eventType;

}
