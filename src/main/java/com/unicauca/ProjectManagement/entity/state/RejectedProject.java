package com.unicauca.ProjectManagement.entity.state;

import com.unicauca.ProjectManagement.entity.DegreeProject;
import jakarta.persistence.Entity;

@Entity
public class RejectedProject extends State {

    public RejectedProject() {this.name = "RejectedProject";}
    @Override
    public void changeState(DegreeProject degreeProject, String answer) {

    }
}
