package persistence;


import application.dto.request.ProjectRequestDTO;
import application.dto.request.TaskRequestDTO;
import application.dto.response.ProjectResponseDTO;
import application.dto.response.TaskResponseDTO;
import application.usecase.CreateProjectUseCase;
import application.usecase.CreateTaskUseCase;
import domain.model.Project;
import domain.model.ProjectStatus;
import domain.model.TaskStatus;
import domain.repository.ProjectRepository;
import domain.repository.TaskRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import infrastructure.exception.DuplicateResourceException;
import jakarta.transaction.Transactional;
import model.integradorsinteclados.IntegradorSinTecladosApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = IntegradorSinTecladosApplication.class)
@Transactional
public class TaskPersistenceIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private CreateProjectUseCase  createProjectUseCase;
    @Autowired
    private CreateTaskUseCase createTaskUseCase;
    @Autowired
    private TaskRepository taskRepository;
    private ProjectRequestDTO requestProject;
    private Project proyectoGuardado;


    @Test
    @Order(1)
    @DisplayName("Crear una tarea y guardarla")
    void testUseCase_createTaskAndSave(){
        requestProject = new ProjectRequestDTO(
                "Proyecto de Integracion",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                ProjectStatus.PLANNED,
                "Test desde @SpringBootTest"
        );
        ProjectResponseDTO responseProject = createProjectUseCase.execute(requestProject);

        proyectoGuardado = projectRepository.findById(responseProject.id())
                .orElseThrow(() -> new RuntimeException("El proyecto no se guardó"));

        TaskRequestDTO requestTask = new TaskRequestDTO(
                null,
                "Funcionalidad Crear",
                12,
                "Joaquin del canto",
                TaskStatus.TODO,
                LocalDateTime.now().plusDays(15),
                LocalDateTime.now().plusMinutes(5)
        );
      
        TaskResponseDTO taskResponse = createTaskUseCase.execute(responseProject.id(), requestTask);
      
        assertNotNull(taskResponse);
        assertNotNull(taskResponse.id(), "El id no debe ser nulo");
        assertEquals(requestTask.title(), "Funcionalidad Crear");
        assertTrue(taskRepository.existByTitleAndProject("Funcionalidad Crear", proyectoGuardado));
    }

    @Test
    @Order(2)
    @DisplayName("No se puede agregar una tarea con proyecto cerrado")

    void testUseCase_YouCannotAddTaskToClosedProject(){
        ProjectRequestDTO requestProjectCerrado = new ProjectRequestDTO(
                "Proyecto cerrado",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                ProjectStatus.CLOSED,
                "Test desde @SpringBootTest"
        );
        ProjectResponseDTO responseProject = createProjectUseCase.execute(requestProjectCerrado);
        proyectoGuardado = projectRepository.findById(responseProject.id())
                .orElseThrow(() -> new RuntimeException("El proyecto no se guardó"));


        TaskRequestDTO requestTask = new TaskRequestDTO(
                null,
                "Tarea Fallida",
                12, // estimatedHours (Integer)
                "Joaquin del canto",
                TaskStatus.TODO,
                LocalDateTime.now().plusDays(15), // finishedAt (LocalDateTime)
                LocalDateTime.now().plusMinutes(5) // createdAt (LocalDateTime)
        );

        Exception exception = assertThrows(BusinessRuleViolationsException.class, () -> {
            createTaskUseCase.execute(responseProject.id(), requestTask);
        });

        assertEquals("No se puede agregar una tarea a un proyecto Cerrado (CLOSED).", exception.getMessage());

    }

    @Test
    @Order(3)
    @DisplayName("lazanr una exception por titulo de tarea duplicado")

    void testUseCase_throwExceptionDuplicateTaskTitle(){
        ProjectRequestDTO requestProjectPractico = new ProjectRequestDTO(
                "Proyecto Practico",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                ProjectStatus.ACTIVE,
                "Test desde @SpringBootTest"
        );
        ProjectResponseDTO responseProject = createProjectUseCase.execute(requestProjectPractico);
        proyectoGuardado = projectRepository.findById(responseProject.id())
                .orElseThrow(() -> new RuntimeException("El proyecto no se guardó"));


        TaskRequestDTO requestTask1 = new TaskRequestDTO(
                null,
                "Tarea Repetida",
                12,
                "Diego del canto",
                TaskStatus.TODO,
                LocalDateTime.now().plusDays(15),
                LocalDateTime.now().plusMinutes(5)
        );
       createTaskUseCase.execute(responseProject.id(), requestTask1);
       TaskRequestDTO requestTask2 = new TaskRequestDTO(
                null,
                "Tarea Repetida",
                12, // estimatedHours (Integer)
                "Joaquin del canto",
                TaskStatus.TODO,
                LocalDateTime.now().plusDays(15),
                LocalDateTime.now().plusMinutes(5)
        );

        Exception exception = assertThrows(DuplicateResourceException.class, () -> {
            createTaskUseCase.execute(responseProject.id(), requestTask2);
        });
      
        assertEquals("Ya existe una tarea con el mismo titulo en este proyecto.", exception.getMessage());

    }
}
