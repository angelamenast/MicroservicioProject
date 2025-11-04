package com.unicauca.ProjectManagement.entity.state;


import com.unicauca.ProjectManagement.entity.DegreeProject;
import jakarta.persistence.Entity;

@Entity
public class FirstCorrectionFormatA extends State {

    public FirstCorrectionFormatA() {this.name = "FirstCorrectionFormatA";}

    @Override
    public void changeState(DegreeProject degreeProject,String answer) {
            degreeProject.setState(new SecondReviewFormatA());
    }
}
