package com.unicauca.ProjectManagement.service;

import com.unicauca.ProjectManagement.entity.DegreeProject;
import com.unicauca.ProjectManagement.infra.dto.DegreeProjectEvent;
import com.unicauca.ProjectManagement.infra.dto.DegreeProjectRequest;
import com.unicauca.ProjectManagement.infra.dto.FileEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IDegreeProjectService {

    @Transactional
    DegreeProject saveProject(DegreeProjectRequest degreeProjectRequest)throws Exception;

    @Transactional
    DegreeProject reuploadProject(DegreeProjectEvent degreeProjectEvent)throws Exception;

    @Transactional
    List<DegreeProject> findAllProjects() throws Exception;

    @Transactional
    DegreeProject getActiveProjectByStudentEmail(String email) throws Exception;

    @Transactional
    List<DegreeProject> getActiveProjectByStudentEmailWithLastFile(String email) throws Exception;

    @Transactional
    DegreeProject updateProjectAndChangeState(DegreeProjectEvent projectUpdatedEvent, String action) throws Exception;

    @Transactional
    DegreeProject findProjectById(long id);

    @Transactional
    List<DegreeProject> findProjectsWithApprovedFormatA();

    @Transactional
    DegreeProject addPreliminaryProjectFile(Long projectId, FileEvent fileEvent)throws Exception;

    @Transactional
    void deleteProjects();
}
