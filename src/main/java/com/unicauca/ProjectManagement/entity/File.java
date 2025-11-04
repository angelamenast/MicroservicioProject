package com.unicauca.ProjectManagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Entity
public class File {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    @Enumerated(EnumType.STRING)
    private EnumFileType type;
    @Getter @Setter
    private int version;
    @Getter @Setter
    private LocalDate date;
    @Getter @Setter
    @Lob
    private byte[] document;


}
