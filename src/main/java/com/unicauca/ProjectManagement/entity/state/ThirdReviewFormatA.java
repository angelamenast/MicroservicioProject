package com.unicauca.ProjectManagement.entity.state;

import com.unicauca.ProjectManagement.entity.DegreeProject;
import jakarta.persistence.Entity;

@Entity
public class ThirdReviewFormatA extends State {

    public ThirdReviewFormatA() {this.name = "ThirdReviewFormatA";}
    @Override
    public void changeState(DegreeProject degreeProject, String answer) {
        if(answer.equals("Approved")){
            degreeProject.setState(new ApprovedFormatA());
        }
        if(answer.equals("Correction")){
            degreeProject.setState(new RejectedProject());
        }
    }

}
