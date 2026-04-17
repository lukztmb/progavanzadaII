package persistence;

import domain.model.*;
import domain.repository.ProjectRepository;
import domain.repository.TaskCommentRepository;
import domain.repository.TaskRepository;
import model.integradorsinteclados.IntegradorSinTecladosApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Este test comprueba la integración COMPLETA de la persistencia para TaskComment.
 * Carga todo el contexto de Spring
 * La base de datos H2 (en memoria).
 * Prueba la cadena: Repository -> Mapper -> JPA -> H2.
 */
@SpringBootTest(classes = IntegradorSinTecladosApplication.class)
@Transactional

public class TaskCommentPersistenceIntegrationTest {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    private Project savedProject;
    private Task savedTask;

    /**
     * Prepara un Proyecto y una Tarea válidos en la base de datos
     * antes de cada test de comentarios.
     * Usamos BeforeEach para aislar los datos de cada test.
     */
    @BeforeEach
    void setUp() {
        Project project = Project.create(
                "Father Project",
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                ProjectStatus.ACTIVE,
                Optional.of("The Project that rules it all, the tests I mean...")
        );
        savedProject = projectRepository.save(project);
        assertNotNull(savedProject.getId());

        LocalDateTime taskStart = LocalDateTime.now().plusMinutes(1);
        LocalDateTime taskEnd = LocalDateTime.now().plusDays(2);

        Task task = Task.create(
                null,
                "Tarea para Comentar",
                savedProject,
                8,
                "Tester",
                TaskStatus.IN_PROGRESS,
                taskStart,
                taskEnd
        );
        savedTask = taskRepository.save(task);
        assertNotNull(savedTask.getId());
    }

    @Test
    @DisplayName("Shall save and find a TaskComment object successfully")
    void testRepository_SaveAndFindComment() {
        // 1. Arrange
        String commentText = "Test comment for a test task";
        TaskComment comment = TaskComment.create(
                savedTask,
                commentText,
                "xXxauthor'snamexXx",
                LocalDateTime.now()
        );

        // 2. Act
        TaskComment savedComment = taskCommentRepository.save(comment);

        // 3. Assert
        assertNotNull(savedComment);
        assertNotNull(savedComment.getId(), "ID should not be null if inyected to the DB");
        assertEquals(commentText, savedComment.getText());

        List<TaskComment> foundComments = taskCommentRepository.findAllByTaskId(savedTask.getId());

        assertNotNull(foundComments);
        assertEquals(1, foundComments.size(), "TaskComment expected to be found");
        assertEquals(commentText, foundComments.getFirst().getText());
        assertEquals(savedComment.getId(), foundComments.getFirst().getId());
    }

    @Test
    @DisplayName("Shall add multiple comments to a single task")
    void testRepository_SaveMultipleComments() {
        // 1. Arrange
        TaskComment comment1 = TaskComment.create(
                savedTask,
                "Test Comment A, for another test task",
                "Author A, the anonymous",
                LocalDateTime.now().minusMinutes(10)
        );
        TaskComment comment2 = TaskComment.create(
                savedTask,
                "Test Comment B, for the same test task as Test Comment A",
                "Author B, the beast",
                LocalDateTime.now()
        );

        // 2. Act
        taskCommentRepository.save(comment1);
        taskCommentRepository.save(comment2);

        // 3. Assert
        List<TaskComment> foundComments = taskCommentRepository.findAllByTaskId(savedTask.getId());

        assertNotNull(foundComments);
        assertEquals(2, foundComments.size(), "Multiple comments expected to be found");

        assertTrue(foundComments.stream().anyMatch(c -> c.getText().equals("Test Comment A, for another test task")));
        assertTrue(foundComments.stream().anyMatch(c -> c.getText().equals("Test Comment B, for the same test task as Test Comment A")));
    }

    @Test
    @DisplayName("Shall return an empty list if no comments are found")
    void testRepository_ReturnEmptyList_WhenNoComments() {
        // 1. Arrange

        // (Commentless task situation)

        // 2. Act
        List<TaskComment> foundComments = taskCommentRepository.findAllByTaskId(savedTask.getId());

        // 3. Assert
        assertNotNull(foundComments);
        assertTrue(foundComments.isEmpty(), "Empty List expected to be found");
    }

    @Test
    @DisplayName("Shall return an empty list if there is no task associated to the given ID")
    void testRepository_ReturnEmptyList_WhenTaskIdIsInvalid() {
        // 1. Arrange
        Long invalidTaskId = 9999L;

        // 2. Act
        List<TaskComment> foundComments = taskCommentRepository.findAllByTaskId(invalidTaskId);

        // 3. Assert
        assertNotNull(foundComments);
        assertTrue(foundComments.isEmpty(), "Empty List expected to be found");
    }
}
