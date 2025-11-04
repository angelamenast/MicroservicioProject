package com.unicauca.ProjectManagement.entity.state;

import com.unicauca.ProjectManagement.entity.DegreeProject;
import jakarta.persistence.Entity;

@Entity
public class FirstReviewFormatA extends State {

    public FirstReviewFormatA() {
        this.name = "FirstReviewFormatA";
    }

    @Override
    public void changeState(DegreeProject degreeProject, String answer) {

        if (answer.equals("Approved")) {
            degreeProject.setState(new ApprovedFormatA());
        }
        if (answer.equals("Correction")) {
            degreeProject.setState(new FirstCorrectionFormatA());
        }

    }
}
