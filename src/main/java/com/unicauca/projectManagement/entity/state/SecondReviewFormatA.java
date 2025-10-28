package com.unicauca.projectManagement.entity.state;

import com.unicauca.projectManagement.entity.DegreeProject;
import jakarta.persistence.Entity;

@Entity
public class SecondReviewFormatA extends State {
    @Override
    public void changeState(DegreeProject degreeProject,String answer) {
        if(answer.equals("Approved")){
            degreeProject.setState(new ApprovedFormatA());
        }
        if(answer.equals("Correction")){
            degreeProject.setState(new FirstCorrectionFormatA());
        }
    }
}
