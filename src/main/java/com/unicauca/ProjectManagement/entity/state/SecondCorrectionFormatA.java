package com.unicauca.ProjectManagement.entity.state;

import com.unicauca.ProjectManagement.entity.DegreeProject;
import jakarta.persistence.Entity;

@Entity
public class SecondCorrectionFormatA extends State{

    public SecondCorrectionFormatA() {this.name = "SecondCorrectionFormatA";}
    @Override
    public void changeState(DegreeProject degreeProject, String answer) {
        degreeProject.setState(new ThirdReviewFormatA());
    }
}
