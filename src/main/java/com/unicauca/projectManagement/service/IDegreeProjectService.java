package com.unicauca.projectManagement.service;

import com.unicauca.projectManagement.entity.DegreeProject;
import com.unicauca.projectManagement.infra.dto.DegreeProjectRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IDegreeProjectService {

    @Transactional
    public DegreeProject saveProject(DegreeProjectRequest degreeProjectRequest)throws Exception;

    @Transactional
    DegreeProject saveProject(DegreeProject degreeProject)throws Exception;

    @Transactional
    List<DegreeProject> findAllProjects() throws Exception;

}
