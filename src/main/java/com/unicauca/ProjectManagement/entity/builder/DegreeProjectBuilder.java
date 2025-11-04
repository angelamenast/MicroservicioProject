package com.unicauca.ProjectManagement.entity.builder;

import com.unicauca.ProjectManagement.entity.*;
import com.unicauca.ProjectManagement.entity.state.State;
import lombok.Getter;

import java.util.List;

public abstract class DegreeProjectBuilder {
    @Getter
    protected DegreeProject project;

    public void createNewProject() {
        project = new DegreeProject();
    }

    public abstract void buildTitle(String title);
    public abstract void buildObjectives(String general, List<String> specifics);
    public abstract void buildModality(String modality);
    public abstract void buildDirector(Professor director);
    public abstract void buildCodirectors(List<Professor> codirectors);
    public abstract void buildStudents(List<Student> students);
    public abstract void buildFiles(List<File> files);
    public abstract void buildState(State state);
}
