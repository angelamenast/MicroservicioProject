package com.unicauca.projectManagement.controller;

import com.unicauca.projectManagement.entity.*;
import com.unicauca.projectManagement.infra.dto.*;
import com.unicauca.projectManagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}