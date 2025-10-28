package com.unicauca.projectManagement.entity.state;

import com.unicauca.projectManagement.entity.DegreeProject;

public class SecondCorrectionFormatA extends State{

    @Override
    public void changeState(DegreeProject degreeProject, String answer) {
        degreeProject.setState(new ThirdReviewFormatA());
    }
}
