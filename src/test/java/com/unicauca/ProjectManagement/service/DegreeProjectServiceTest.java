package com.unicauca.ProjectManagement.service;

import com.unicauca.ProjectManagement.entity.*;
import com.unicauca.ProjectManagement.entity.state.FirstReviewFormatA;
import com.unicauca.ProjectManagement.entity.state.State;
import com.unicauca.ProjectManagement.infra.config.RabbitMQConfig;
import com.unicauca.ProjectManagement.infra.dto.DegreeProjectEvent;
import com.unicauca.ProjectManagement.infra.dto.FileEvent;
import com.unicauca.ProjectManagement.repository.DegreeProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DegreeProjectServiceTest {

    @Mock
    private DegreeProjectRepository degreeProjectRepository;

    @Mock
    private ProfessorService professorService;

    @Mock
    private StudentService studentService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DegreeProjectService degreeProjectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateProjectAndChangeState_ShouldUpdateAndNotify() throws Exception {
        // Arrange
        DegreeProjectEvent projectEvent = new DegreeProjectEvent();
        projectEvent.setId(1L);

        FileEvent fileEvent = new FileEvent();
        fileEvent.setId(1L);
        fileEvent.setName("FormatoA_v1.pdf");
        fileEvent.setType("FormatoA");
        fileEvent.setVersion(1);
        fileEvent.setDocument(Base64.getEncoder().encodeToString("test".getBytes()));
        projectEvent.setFiles(List.of(fileEvent));

        DegreeProject project = new DegreeProject();
        project.setId(1L);
        project.setTitle("Test Project");
        project.setState(new FirstReviewFormatA());
        project.setModality(EnumModality.ProyectoInvestigacion); // 🔥 línea necesaria

        project.setFiles(new ArrayList<>());

        Professor prof = new Professor();
        prof.setName("Juan");
        prof.setLastName("Pérez");
        prof.setEmail("juan@uni.edu");
        prof.setOffice("B12");
        project.setDirector(prof);

        Student student = new Student();
        student.setName("Ana");
        student.setLastName("López");
        student.setEmail("ana@uni.edu");
        student.setStudentCode("123");
        project.setStudents(List.of(student));

        when(degreeProjectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(degreeProjectRepository.save(any(DegreeProject.class))).thenReturn(project);

        // Act
        DegreeProject result = degreeProjectService.updateProjectAndChangeState(projectEvent, "Approve");

        // Assert
        assertNotNull(result);
        verify(rabbitTemplate, times(1)).convertAndSend(eq(RabbitMQConfig.PROJECT_QUEUE), any(Object.class));
        verify(degreeProjectRepository, times(1)).save(any(DegreeProject.class));
    }
    @Test
    void updateProjectAndChangeState_ShouldThrowException_WhenProjectNotFound() {
        DegreeProjectEvent projectEvent = new DegreeProjectEvent();
        projectEvent.setId(999L);
        when(degreeProjectRepository.findById(999L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(Exception.class, () ->
                degreeProjectService.updateProjectAndChangeState(projectEvent, "Approve")
        );
        verify(degreeProjectRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(Optional.class));
    }
    @Test
    void updateProjectAndChangeState_ShouldAttachNewFile_WhenEventContainsFile() throws Exception {
        // Arrange
        DegreeProjectEvent event = new DegreeProjectEvent();
        event.setId(3L);

        FileEvent fileEvent = new FileEvent();
        fileEvent.setId(1L);
        fileEvent.setName("NuevoDocumento.pdf");
        fileEvent.setType("FormatoA");
        fileEvent.setVersion(1);
        fileEvent.setDocument(Base64.getEncoder().encodeToString("contenido".getBytes()));
        event.setFiles(List.of(fileEvent));
        DegreeProject project = new DegreeProject();
        project.setId(3L);
        project.setTitle("Proyecto con nuevo archivo");
        project.setFiles(new ArrayList<>());
        project.setModality(EnumModality.ProyectoInvestigacion);
        project.setState(new FirstReviewFormatA());
        Professor director = new Professor();
        director.setId(10L);
        director.setName("Dr. Mock");
        project.setDirector(director);
        when(degreeProjectRepository.findById(3L)).thenReturn(Optional.of(project));
        when(degreeProjectRepository.save(any(DegreeProject.class))).thenReturn(project);
        DegreeProject result = degreeProjectService.updateProjectAndChangeState(event, "Approve");
        assertNotNull(result);
        assertEquals(1, result.getFiles().size());
        verify(degreeProjectRepository, times(1)).save(any(DegreeProject.class));
    }



    @Test
    void getActiveProjectByStudentEmail_ShouldReturnActiveProject() throws Exception {
        // Arrange
        String email = "ana@uni.edu";
        DegreeProject project = new DegreeProject();
        project.setId(1L);
        project.setTitle("Proyecto Activo");
        project.setModality(EnumModality.ProyectoInvestigacion);
        State state = new FirstReviewFormatA(); // Estado válido
        project.setState(state);

        when(degreeProjectRepository.findProjectsByStudentEmail(email)).thenReturn(List.of(project));

        // Act
        DegreeProject result = degreeProjectService.getActiveProjectByStudentEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals("Proyecto Activo", result.getTitle());
        verify(degreeProjectRepository, times(1)).findProjectsByStudentEmail(email);
    }
@Test
    void getActiveProjectByStudentEmailWithLastFile_ShouldReturnFilteredProjects() throws Exception {
        // Arrange
        File file1 = new File();
        file1.setVersion(1);
        file1.setType(EnumFileType.Anteproyecto);

        File file2 = new File();
        file2.setVersion(2);
        file2.setType(EnumFileType.Anteproyecto);

        DegreeProject project = new DegreeProject();
        project.setFiles(List.of(file1, file2));
        State state = new FirstReviewFormatA();
    project.setModality(EnumModality.ProyectoInvestigacion);
    project.setState(state);

        when(degreeProjectRepository.findDegreeProjectsByFileType(EnumFileType.Anteproyecto))
                .thenReturn(List.of(project));

        // Act
        List<DegreeProject> result = degreeProjectService.getActiveProjectByStudentEmailWithLastFile("Anteproyecto");

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.get(0).getFiles().size());
        verify(degreeProjectRepository, times(1))
                .findDegreeProjectsByFileType(EnumFileType.Anteproyecto);
    }
}
