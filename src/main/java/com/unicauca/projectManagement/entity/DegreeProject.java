package com.unicauca.projectManagement.entity;

import com.unicauca.projectManagement.entity.state.State;
import jakarta.persistence.Entity;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
public class DegreeProject {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Getter @Setter
    private String title;
    @Getter @Setter
    private String generalObjective;
    @Getter @Setter
    private List<String> specificObjectives;
    @Getter @Setter
    @Enumerated(EnumType.STRING)
    private EnumModality modality;

    @OneToMany(cascade = CascadeType.ALL)
    @Getter @Setter
    @JoinColumn(name = "degree_project_id")
    private List<File> files = new ArrayList<File>();

    @ManyToMany
    @JoinTable
    (
        name = "degree_project_student",
        joinColumns = @JoinColumn(name = "degree_project_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @Getter @Setter
    private List<Student> students = new ArrayList<Student>();

    @ManyToOne(cascade = CascadeType.ALL)
    @Getter @Setter
    @JoinColumn(name = "professor_id")
    private Professor director;

    @ManyToMany
    @JoinTable
    (
        name = "degree_project_codirector",
        joinColumns = @JoinColumn(name = "degree_project_id"),
        inverseJoinColumns = @JoinColumn(name = "professor_id")
    )
    @Getter @Setter
    private List<Professor> codirectors = new ArrayList<Professor>();


    @Getter @Setter
    @OneToOne(cascade = CascadeType.ALL)
    private State state;

}
