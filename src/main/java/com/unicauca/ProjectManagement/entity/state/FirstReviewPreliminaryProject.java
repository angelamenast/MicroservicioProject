package com.unicauca.ProjectManagement.entity.state;

import com.unicauca.ProjectManagement.entity.DegreeProject;
import jakarta.persistence.Entity;

@Entity
public class FirstReviewPreliminaryProject extends State{

    public FirstReviewPreliminaryProject() {this.name =  "FirstReviewPreliminaryProject";}
    @Override
    public void changeState(DegreeProject degreeProject, String answer) {

    }
}
