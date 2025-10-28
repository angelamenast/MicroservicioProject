package com.unicauca.projectManagement.entity.state;

import com.unicauca.projectManagement.entity.DegreeProject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class State {
    @Id
    @Getter @Setter
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    int id;

    @Getter @Setter
    String name;

    public abstract void changeState(DegreeProject degreeProject, String answer);
}

