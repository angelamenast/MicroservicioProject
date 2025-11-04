package com.unicauca.ProjectManagement.service;

import com.unicauca.ProjectManagement.entity.*;
import com.unicauca.ProjectManagement.entity.*;
import com.unicauca.ProjectManagement.entity.builder.DegreeProjectBuilder;
import com.unicauca.ProjectManagement.entity.builder.DegreeProjectBuilderConcrete;
import com.unicauca.ProjectManagement.entity.builder.DegreeProjectDirector;
import com.unicauca.ProjectManagement.entity.state.FirstCorrectionFormatA;
import com.unicauca.ProjectManagement.entity.state.FirstReviewFormatA;
import com.unicauca.ProjectManagement.entity.state.SecondCorrectionFormatA;
import com.unicauca.ProjectManagement.entity.state.State;
import com.unicauca.ProjectManagement.infra.config.RabbitMQConfig;
import com.unicauca.ProjectManagement.infra.dto.*;
import com.unicauca.ProjectManagement.repository.DegreeProjectRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class DegreeProjectService implements IDegreeProjectService {

    private final DegreeProjectRepository degreeProjectRepository;


    private final ProfessorService professorService;


    private final StudentService studentService;


    private RabbitTemplate rabbitTemplate;

    @Autowired
    public DegreeProjectService(DegreeProjectRepository degreeProjectRepository,
                                ProfessorService professorService,
                                StudentService studentService,
                                RabbitTemplate rabbitTemplate) {
        this.degreeProjectRepository = degreeProjectRepository;
        this.professorService = professorService;
        this.studentService = studentService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public DegreeProjectService(DegreeProjectRepository degreeProjectRepository, ProfessorService professorService, StudentService studentService) {
        this.degreeProjectRepository = degreeProjectRepository;
        this.professorService = professorService;
        this.studentService = studentService;
    }

    @Override
    @Transactional
    public List<DegreeProject> findProjectsWithApprovedFormatA() {
        return degreeProjectRepository.findProjectsWithApprovedFormatA();
    }

    @Override
    @Transactional
    public DegreeProject addPreliminaryProjectFile(Long projectId, FileEvent fileEvent) throws Exception {

        DegreeProject degreeProject = degreeProjectRepository.findById(projectId)
                .orElseThrow(() -> new Exception("No se encontró el proyecto con id " + projectId));

        // ---- Crear el archivo ----
        File newFile = new File();
        newFile.setName(fileEvent.getName());
        newFile.setVersion(fileEvent.getVersion());
        newFile.setType(EnumFileType.Anteproyecto);
        newFile.setDocument(Base64.getDecoder().decode(fileEvent.getDocument()));
        newFile.setDate(
                (fileEvent.getDate() != null && !fileEvent.getDate().isBlank())
                        ? LocalDate.parse(fileEvent.getDate())
                        : LocalDate.now()
        );

        // ---- Agregarlo al proyecto ----
        if (degreeProject.getFiles() == null) {
            degreeProject.setFiles(new ArrayList<>());
        }
        degreeProject.getFiles().add(newFile);

        // ---- Cambiar estado ----
        System.out.println("Estado antes: " + degreeProject.getState().getName());
        degreeProject.getState().changeState(degreeProject, "");
        System.out.println("Estado después: " + degreeProject.getState().getName());

        // ---- Guardar ----

        NotificationProjectEvent event = new NotificationProjectEvent();
        event.setTitle(degreeProject.getTitle());
        event.setGeneralObjective(degreeProject.getGeneralObjective());
        event.setSpecificObjectives(degreeProject.getSpecificObjectives());
        event.setModality(degreeProject.getModality().toString());

        NotificationProfessorEvent professorEvent = new NotificationProfessorEvent();

        professorEvent.setName(degreeProject.getDirector().getName());
        professorEvent.setEmail(degreeProject.getDirector().getEmail());
        professorEvent.setOffice(degreeProject.getDirector().getOffice());
        professorEvent.setLastName(degreeProject.getDirector().getLastName());
        event.setDirector(professorEvent);
        List<NotificationStudentEvent> studentsN = new ArrayList<>();

        for (Student student: degreeProject.getStudents()){
            NotificationStudentEvent notificationStudentEvent = new NotificationStudentEvent();
            notificationStudentEvent.setName(student.getName());
            notificationStudentEvent.setEmail(student.getEmail());
            notificationStudentEvent.setStudentCode(student.getStudentCode());
            notificationStudentEvent.setLastName(student.getLastName());
            studentsN.add(notificationStudentEvent);
        }
        event.setStudents(studentsN);
        event.setEventType("ANTEPROYECTO_SUBIDO");
        rabbitTemplate.convertAndSend(RabbitMQConfig.PROJECT_QUEUE, event);
        return degreeProjectRepository.save(degreeProject);
    }

    @Override
    @Transactional
    public DegreeProject updateProjectAndChangeState(DegreeProjectEvent projectUpdatedEvent, String action) throws Exception {

        DegreeProject degreeUpdatedProject = degreeProjectRepository.findById(projectUpdatedEvent.getId()).get();

        List<File> files = new ArrayList<>();
        if (projectUpdatedEvent.getFiles() != null) {
            for (FileEvent fileEvent : projectUpdatedEvent.getFiles()) {
                File file = new File();
                file.setId(fileEvent.getId());
                file.setName(fileEvent.getName());
                file.setVersion(fileEvent.getVersion());
                file.setDocument(Base64.getDecoder().decode(fileEvent.getDocument()));
                file.setType(EnumFileType.valueOf(fileEvent.getType())); // asegúrate que coincida con tu enum
                files.add(file);
                if (fileEvent.getDate() != null && !fileEvent.getDate().isBlank()) {
                    try {
                        file.setDate(LocalDate.parse(fileEvent.getDate()));
                    } catch (Exception e) {
                        System.err.println(" Fecha inválida en archivo: " + fileEvent.getDate());
                        file.setDate(LocalDate.now()); // o lo que prefieras
                    }
                } else {
                    file.setDate(LocalDate.now());
                }
            }
        }

        List<File> newfiles = degreeUpdatedProject.getFiles();
        int version = projectUpdatedEvent.getFiles().getFirst().getVersion();

        for(File file: files){
            if(file.getVersion() == version && file.getType().equals(EnumFileType.FormatoA)){
                file.setDocument(Base64.getDecoder().decode(projectUpdatedEvent.getFiles().getFirst().getDocument()));
            }
        }

        degreeUpdatedProject.setFiles(files);

        // Cambiar estado según la acción
        System.out.println("Estado antes: "+degreeUpdatedProject.getState().getName());

        degreeUpdatedProject.getState().changeState(degreeUpdatedProject, action);

        System.out.println("Estado despues : " + degreeUpdatedProject.getState().getName());

        NotificationProjectEvent event = new NotificationProjectEvent();
        event.setTitle(degreeUpdatedProject.getTitle());
        event.setGeneralObjective(degreeUpdatedProject.getGeneralObjective());
        event.setSpecificObjectives(degreeUpdatedProject.getSpecificObjectives());
        event.setModality(degreeUpdatedProject.getModality().toString());

        NotificationProfessorEvent professorEvent = new NotificationProfessorEvent();
        professorEvent.setName(degreeUpdatedProject.getDirector().getName());
        professorEvent.setEmail(degreeUpdatedProject.getDirector().getEmail());
        professorEvent.setOffice(degreeUpdatedProject.getDirector().getOffice());
        professorEvent.setLastName(degreeUpdatedProject.getDirector().getLastName());

        event.setDirector(professorEvent);
        List<NotificationStudentEvent> studentsN = new ArrayList<>();

        for (Student student: degreeUpdatedProject.getStudents()){
            NotificationStudentEvent notificationStudentEvent = new NotificationStudentEvent();
            notificationStudentEvent.setName(student.getName());
            notificationStudentEvent.setEmail(student.getEmail());
            notificationStudentEvent.setStudentCode(student.getStudentCode());
            notificationStudentEvent.setLastName(student.getLastName());
            studentsN.add(notificationStudentEvent);
        }
        event.setStudents(studentsN);
        event.setEventType("FORMATO_A_EVALUADO");
        rabbitTemplate.convertAndSend(RabbitMQConfig.PROJECT_QUEUE, event);
        return degreeProjectRepository.save(degreeUpdatedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public DegreeProject getActiveProjectByStudentEmail(String email) throws Exception {
        try {
            List<DegreeProject> projects = degreeProjectRepository.findProjectsByStudentEmail(email);

            if (projects == null || projects.isEmpty()) {
                return null;
            }
            // Buscar el proyecto que no esté en estado "Rejected"
            return projects.stream()
                    .filter(p -> p.getState() != null
                            && p.getState().getName() != null
                            && !p.getState().getName().equalsIgnoreCase("RejectedProject"))
                    .findFirst()
                    .orElse(null);

        } catch (Exception e) {
            throw new Exception("Error al obtener el proyecto activo del estudiante: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DegreeProject> getActiveProjectByStudentEmailWithLastFile(String type) throws Exception {
        try {
            List<DegreeProject> projects = degreeProjectRepository.findDegreeProjectsByFileType(EnumFileType.valueOf(type));

            if (projects == null || projects.isEmpty()) {
                return null;
            }

            List<DegreeProject> wantedProjects = new ArrayList<>();

            for(DegreeProject p : projects){
                File lastVersionFile = new File();
                List<File> files = new ArrayList<>();
                for(File f : p.getFiles()) {
                    int lastVersion = 0;
                    if(f.getVersion() > lastVersion){
                        lastVersion = f.getVersion();
                        lastVersionFile = f;
                    }
                }
                files.add(lastVersionFile);
                p.setFiles(files);
                if(!p.getState().getName().equals("RejectedProject") && !p.getState().getName().equals("ApprovedFormatA")){
                    wantedProjects.add(p);
                }
            }

            return wantedProjects;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error al obtener el proyecto activo del estudiante: " + e.getMessage(), e);
        }
    }

    @Override
    public DegreeProject saveProject(DegreeProjectRequest degreeProjectRequest) throws Exception {

        DegreeProjectDirector director = new DegreeProjectDirector();
        DegreeProjectBuilder builder = new DegreeProjectBuilderConcrete();
        director.setBuilder(builder);

        Professor profDirector = professorService.findById(degreeProjectRequest.getDirectorId())
                .orElseThrow(() -> new Exception("Director no encontrado"));


        List<Student> students = new ArrayList<>();
        for (Long id : degreeProjectRequest.getStudentsId()) {
            students.add(studentService.findById(id).get());
        }

        List<Professor> codirectors = new ArrayList<>();
        for (Long id : degreeProjectRequest.getCodirectorsId()) {
            codirectors.add(professorService.findById(id).get());
        }

        List<File> files = new ArrayList<>();
        for (FileRequest fr : degreeProjectRequest.getFiles()) {
            File f = new File();
            f.setName(fr.getName());
            f.setVersion(fr.getVersion());
            f.setType(EnumFileType.valueOf(fr.getType()));
            f.setDate(LocalDate.parse(fr.getDate()));
            f.setDocument(Base64.getDecoder().decode(fr.getDocument()));
            files.add(f);
        }

        String stateName = degreeProjectRequest.getState().getName();
        State state = null;
        if (stateName.equals("FirstReviewFormatA")) {
            state = new FirstReviewFormatA();
        }

        // Crear el proyecto con el builder
        DegreeProject project = director.constructProject(
                degreeProjectRequest.getTitle(),
                degreeProjectRequest.getGeneralObjective(),
                degreeProjectRequest.getSpecificObjectives(),
                degreeProjectRequest.getModality(),
                profDirector,
                codirectors,
                students,
                files,
                state
        );

        NotificationProjectEvent event = new NotificationProjectEvent();
        event.setTitle(project.getTitle());
        event.setGeneralObjective(project.getGeneralObjective());
        event.setSpecificObjectives(project.getSpecificObjectives());
        event.setModality(project.getModality().toString());

        NotificationProfessorEvent professorEvent = new NotificationProfessorEvent();
        professorEvent.setName(project.getDirector().getName());
        professorEvent.setLastName(project.getDirector().getLastName());
        professorEvent.setEmail(project.getDirector().getEmail());
        professorEvent.setOffice(project.getDirector().getOffice());

        event.setDirector(professorEvent);
        List<NotificationStudentEvent> studentsN = new ArrayList<>();

        for (Student student: project.getStudents()){
            NotificationStudentEvent notificationStudentEvent = new NotificationStudentEvent();
            notificationStudentEvent.setName(student.getName());
            notificationStudentEvent.setEmail(student.getEmail());
            notificationStudentEvent.setStudentCode(student.getStudentCode());
            notificationStudentEvent.setLastName(student.getLastName());
            studentsN.add(notificationStudentEvent);
        }
        event.setStudents(studentsN);
        event.setEventType("FORMATO_A_SUBIDO");
        rabbitTemplate.convertAndSend(RabbitMQConfig.PROJECT_QUEUE, event);
        return degreeProjectRepository.save(project);

    }

    @Override
    @Transactional
    public DegreeProject reuploadProject(DegreeProjectEvent degreeProjectEvent) throws Exception {
        DegreeProject degreeProject = new DegreeProject();
        degreeProject.setId(degreeProjectEvent.getId());
        degreeProject.setTitle(degreeProjectEvent.getTitle());
        degreeProject.setGeneralObjective(degreeProjectEvent.getGeneralObjective());
        degreeProject.setSpecificObjectives(degreeProjectEvent.getSpecificObjectives());
        degreeProject.setModality(EnumModality.valueOf(degreeProjectEvent.getModality()));
        degreeProject.setDirector(professorService.findById(degreeProjectEvent.getDirectorId()).get());

        String stateName = degreeProjectEvent.getState().getName();
        State state = null;

        switch (stateName) {
            case "FirstCorrectionFormatA" -> state = new FirstCorrectionFormatA();
            case "SecondCorrectionFormatA" -> state = new SecondCorrectionFormatA();
            case "ThirdReviewFormatA" -> {

                throw new Exception("No se permiten más subidas. Ya alcanzó el tercer intento.");
            }
            default -> throw new IllegalStateException("Estado no reconocido: " + stateName);
        }


        degreeProject.setState(state);

        degreeProject.getState().changeState(degreeProject, "");

        List<File> files = new ArrayList<>();
        List<Student> students = new ArrayList<>();

        for (Long studentId : degreeProjectEvent.getStudentsId()) {
            students.add(studentService.findById(studentId).get());
        }

        for (FileEvent fileRequest : degreeProjectEvent.getFiles()) {
            File file = new File();
            file.setId(fileRequest.getId());
            file.setName(fileRequest.getName());
            file.setType(EnumFileType.valueOf(fileRequest.getType()));
            file.setVersion(fileRequest.getVersion());
            file.setDate(LocalDate.parse(fileRequest.getDate()));
            file.setDocument(Base64.getDecoder().decode(fileRequest.getDocument()));
            files.add(file);
        }

        List<Professor> codirectors = new ArrayList<>();

        for (Long codirectorId : degreeProjectEvent.getCodirectorsId()) {
            codirectors.add(professorService.findById(codirectorId).get());
        }

        degreeProject.setCodirectors(codirectors);
        degreeProject.setFiles(files);
        degreeProject.setStudents(students);


        NotificationProjectEvent event = new NotificationProjectEvent();
        event.setTitle(degreeProject.getTitle());
        event.setGeneralObjective(degreeProject.getGeneralObjective());
        event.setSpecificObjectives(degreeProject.getSpecificObjectives());
        event.setModality(degreeProject.getModality().toString());

        NotificationProfessorEvent professorEvent = new NotificationProfessorEvent();
        professorEvent.setName(degreeProject.getDirector().getName());
        professorEvent.setLastName(degreeProject.getDirector().getLastName());
        professorEvent.setEmail(degreeProject.getDirector().getEmail());
        professorEvent.setOffice(degreeProject.getDirector().getOffice());


        event.setDirector(professorEvent);
        List<NotificationStudentEvent> studentsN = new ArrayList<>();

        for (Student student: degreeProject.getStudents()){
            NotificationStudentEvent notificationStudentEvent = new NotificationStudentEvent();

            notificationStudentEvent.setName(student.getName());
            System.out.println(student.getName());
            notificationStudentEvent.setEmail(student.getEmail());
            System.out.println(student.getEmail());
            notificationStudentEvent.setStudentCode(student.getStudentCode());
            notificationStudentEvent.setLastName(student.getLastName());
            studentsN.add(notificationStudentEvent);
        }
        event.setStudents(studentsN);
        event.setEventType("FORMATO_A_SUBIDO");
        rabbitTemplate.convertAndSend(RabbitMQConfig.PROJECT_QUEUE, event);

        return degreeProjectRepository.save(degreeProject);
    }

    @Override
    @Transactional
    public List<DegreeProject> findAllProjects() throws Exception {
        try {
            return degreeProjectRepository.findAll();
        } catch (Exception e) {
            throw new Exception("Error listing projects: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public DegreeProject findProjectById(long id){
        if(degreeProjectRepository.findById(id).isPresent()){
            return degreeProjectRepository.findById(id).get();
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProjects(){
        degreeProjectRepository.deleteAll();
    }

}
