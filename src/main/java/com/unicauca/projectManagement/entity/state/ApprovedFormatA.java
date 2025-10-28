package com.unicauca.projectManagement.entity.state;

import com.unicauca.projectManagement.entity.DegreeProject;
import jakarta.persistence.Entity;

@Entity
public class ApprovedFormatA extends State {
    @Override
    public void changeState(DegreeProject degreeProject,String answer) {

    }
}
