package com.unicauca.ProjectManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicauca.ProjectManagement.entity.*;
import com.unicauca.ProjectManagement.infra.dto.*;
import com.unicauca.ProjectManagement.service.*;
import com.unicauca.ProjectManagement.entity.DegreeProject;
import com.unicauca.ProjectManagement.infra.dto.DegreeProjectRequest;
import com.unicauca.ProjectManagement.service.DegreeProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projectmanagement")
public class ProjectManagementController {

    @Autowired
    private DegreeProjectService projectService;

    @PostMapping("/SaveProject")
    public ResponseEntity<?> saveProject(@RequestBody DegreeProjectRequest degreeProjectRequest) {
        try {
            DegreeProject degreeProjectSaved = projectService.saveProject(degreeProjectRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(degreeProjectSaved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"Error saving project: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/reuploadProject")
    public ResponseEntity<?> reuploadProject(@RequestBody DegreeProjectEvent degreeProjectRequest) {
        try {
            DegreeProject degreeProjectSaved = projectService.reuploadProject(degreeProjectRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(degreeProjectSaved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"Error reuploading project: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/projectByEmail/{email}")
    public ResponseEntity<?> findProjectByEmail(@PathVariable String email) {
        try {
            DegreeProject degreeProject = projectService.getActiveProjectByStudentEmail(email);
            if (degreeProject == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Theren't projects registered for that student with email " + email);
            }
            return ResponseEntity.ok(degreeProject);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error getting project by email.\"}");
        }

    }

    @PostMapping("/updateProject")
    public ResponseEntity<?> updateProject(@RequestParam String action,@RequestBody DegreeProjectEvent projectEvent) {
        try {
            DegreeProject updated = projectService.updateProjectAndChangeState(projectEvent, action);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating project: " + e.getMessage());
        }
    }

    @PostMapping("/uploadPreliminaryProject")
    public ResponseEntity<?> uploadPreliminaryProject(@RequestBody Map<String, Object> request) {
        try {
            Long projectId = Long.valueOf(request.get("projectId").toString());
            ObjectMapper mapper = new ObjectMapper();
            FileEvent fileEvent = mapper.convertValue(request.get("file"), FileEvent.class);

            DegreeProject updated = projectService.addPreliminaryProjectFile(projectId, fileEvent);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating project: " + e.getMessage());
        }
    }


    @GetMapping("/projectsByFileType/{fileType}")
    public ResponseEntity<?> findProjectsByFileType(@PathVariable String fileType) {
        try {
            List<DegreeProject> projects = projectService.getActiveProjectByStudentEmailWithLastFile(fileType);

            if (projects == null || projects.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("There aren't projects registered with file type: " + fileType);
            }

            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error getting projects by file type: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/projects")
    public ResponseEntity<?> findAllProjects() {
        try {
            List<DegreeProject> DegreeProjects = projectService.findAllProjects();
            if (DegreeProjects.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Theren't projects registered");
            }
            return ResponseEntity.ok(DegreeProjects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error getting projects.\"}");
        }

    }

    @GetMapping("/approvedFormatA")
    public ResponseEntity<?> findProjectsWithApprovedFormatA() {
        try {
            List<DegreeProject> degreeProjects = projectService.findProjectsWithApprovedFormatA();

            if (degreeProjects == null || degreeProjects.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No projects found with approved FormatA.");
            }

            return ResponseEntity.ok(degreeProjects);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error getting projects with approved FormatA: " + e.getMessage() + "\"}");
        }
    }


    @GetMapping("/projectById/{id}")
    public ResponseEntity<?> findProjectsById(@PathVariable Long id) {
        try {
            DegreeProject DegreeProjects = projectService.findProjectById(id);
            if (DegreeProjects == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Could not find project with id " + id);
            }
            return ResponseEntity.ok(DegreeProjects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error getting project.\"}");
        }

    }

    @DeleteMapping("/deleteProjects")
    public ResponseEntity<?> deleteProjects() {
        try {
            projectService.deleteProjects();

            return ResponseEntity.status(HttpStatus.ACCEPTED).body("si");

        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error getting project.\"}");
        }
    }

}