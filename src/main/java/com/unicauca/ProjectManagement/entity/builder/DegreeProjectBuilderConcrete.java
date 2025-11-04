package com.unicauca.ProjectManagement.entity.builder;

import com.unicauca.ProjectManagement.entity.*;
import com.unicauca.ProjectManagement.entity.state.State;
import java.util.List;

public class DegreeProjectBuilderConcrete extends DegreeProjectBuilder {

    @Override
    public void buildTitle(String title) {
        project.setTitle(title);
    }

    @Override
    public void buildObjectives(String general, List<String> specifics) {
        project.setGeneralObjective(general);
        project.setSpecificObjectives(specifics);
    }

    @Override
    public void buildModality(String modality) {
        project.setModality(EnumModality.valueOf(modality));
    }

    @Override
    public void buildDirector(Professor director) {
        project.setDirector(director);
    }

    @Override
    public void buildCodirectors(List<Professor> codirectors) {
        project.setCodirectors(codirectors);
    }

    @Override
    public void buildStudents(List<Student> students) {
        project.setStudents(students);
    }

    @Override
    public void buildFiles(List<File> files) {
        project.setFiles(files);
    }

    @Override
    public void buildState(State state) {
        project.setState(state);
    }
}
