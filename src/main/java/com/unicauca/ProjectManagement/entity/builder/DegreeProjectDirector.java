package com.unicauca.ProjectManagement.entity.builder;

import com.unicauca.ProjectManagement.entity.*;
import com.unicauca.ProjectManagement.entity.state.State;
import lombok.Setter;

import java.util.List;

public class DegreeProjectDirector {
    @Setter
    private DegreeProjectBuilder builder;

    public DegreeProject constructProject(
            String title,
            String generalObjective,
            List<String> specifics,
            String modality,
            Professor director,
            List<Professor> codirectors,
            List<Student> students,
            List<File> files,
            State state) {

        builder.createNewProject();
        builder.buildTitle(title);
        builder.buildObjectives(generalObjective, specifics);
        builder.buildModality(modality);
        builder.buildDirector(director);
        builder.buildCodirectors(codirectors);
        builder.buildStudents(students);
        builder.buildFiles(files);
        builder.buildState(state);
        return builder.getProject();
    }
}
