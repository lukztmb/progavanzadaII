package domain.model;

import static domain.model.ProjectStatus.*;
import static domain.model.TaskStatus.*;

// La importación de jakarta.validation.ValidationException es la correcta
// para el 'throw' dentro del método 'create' de TaskComment.
import jakarta.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad TaskComment, enfocados en el método factory 'create'.
 */
@ExtendWith(MockitoExtension.class)
public class TaskCommentTest {

    private Project testProject;
    private Task testTask;
    private LocalDateTime testTime;

    private final String validText = "Este es un comentario válido.";
    private final String validAuthor = "Autor Válido";

    @BeforeEach
    void setUp() {
        testProject = Project.create(
                "Test Project",
                LocalDate.now(), // Válido
                LocalDate.now().plusDays(10), // Válido
                ACTIVE,
                Optional.of("Descripción")
        );
        testProject.setId(1L);

        testTime = LocalDateTime.now().plusMinutes(5);
        LocalDateTime testEndTime = testTime.plusDays(1);

        testTask = Task.create(
                11L,
                "Test Task",
                testProject,
                10,
                "Pepe Botellas",
                TODO,
                testTime, // Válido
                testEndTime  // Válido
        );
    }

    @Test
    @DisplayName("Debería crear un TaskComment exitosamente con datos válidos")
    void createTaskComment_WithValidData_ShouldSucceed() {
        // Act
        TaskComment comment = TaskComment.create(testTask, validText, validAuthor, testTime);

        // Assert
        assertNotNull(comment);
        assertEquals(testTask, comment.getTask());
        assertEquals(validText, comment.getText());
        assertEquals(validAuthor, comment.getAuthor());
        assertEquals(testTime, comment.getCreatedAt());
    }

    @Test
    @DisplayName("Debería lanzar ValidationException si la Tarea (task) es nula")
    void createTaskComment_WithNullTask_ShouldThrowValidationException() {
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            TaskComment.create(null, validText, validAuthor, testTime);
        });

        assertEquals("Comment should be associated to a Task.", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar ValidationException si el Texto (text) es nulo")
    void createTaskComment_WithNullText_ShouldThrowValidationException() {
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            TaskComment.create(testTask, null, validAuthor, testTime);
        });

        assertEquals("Comment should have text.", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar ValidationException si el Texto (text) está vacío")
    void createTaskComment_WithEmptyText_ShouldThrowValidationException() {
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            TaskComment.create(testTask, "", validAuthor, testTime);
        });

        assertEquals("Comment should have text.", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar ValidationException si el Texto (text) solo contiene espacios")
    void createTaskComment_WithBlankText_ShouldThrowValidationException() {
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            TaskComment.create(testTask, "   ", validAuthor, testTime);
        });

        assertEquals("Comment should have text.", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar ValidationException si el Autor (author) es nulo")
    void createTaskComment_WithNullAuthor_ShouldThrowValidationException() {
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            TaskComment.create(testTask, validText, null, testTime);
        });

        assertEquals("Comment should have an author.", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar ValidationException si el Autor (author) está vacío")
    void createTaskComment_WithEmptyAuthor_ShouldThrowValidationException() {
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            TaskComment.create(testTask, validText, "", testTime);
        });

        assertEquals("Comment should have an author.", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar ValidationException si el Autor (author) solo contiene espacios")
    void createTaskComment_WithBlankAuthor_ShouldThrowValidationException() {
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            TaskComment.create(testTask, validText, "   ", testTime);
        });

        assertEquals("Comment should have an author.", exception.getMessage());
    }
}