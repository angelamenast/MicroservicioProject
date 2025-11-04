package com.unicauca.ProjectManagement.infra.dto;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class FileEvent {
    private long id;
    private String name;
    private String type;
    private int version;
    private String date;
    private String document;
}
