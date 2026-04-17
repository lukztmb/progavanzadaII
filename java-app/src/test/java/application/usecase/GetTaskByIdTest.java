package application.usecase;

import application.dto.response.CommentResponseDTO;
import application.dto.response.TaskResponseDTO;
import application.dto.response.TaskWithCommentsResponseDTO;
import application.mapper.TaskCommentMapper;
import application.mapper.TaskMapper;
import domain.model.Project;
import domain.model.Task;
import domain.model.TaskComment;
import domain.model.TaskStatus;
import domain.repository.TaskCommentRepository;
import domain.repository.TaskRepository;
import infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario para GetTaskByIdUseCase.
 * Verifica la lógica de negocio aislando las dependencias.
 */
@ExtendWith(MockitoExtension.class)
public class GetTaskByIdTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskCommentRepository commentRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private TaskCommentMapper commentMapper;

    @InjectMocks
    private GetTaskByIdUseCase getTaskByIdUseCase;

    private Task mockTask;
    private TaskResponseDTO mockTaskDTO;
    private Long taskId = 1L;

    @BeforeEach
    void setUp() {
        mockTask = mock(Task.class);
        when(mockTask.getProyect()).thenReturn(mock(Project.class));

        mockTaskDTO = new TaskResponseDTO(
                taskId,
                "Tarea de Prueba",
                mockTask.getProyect(),
                8,
                "Asignado",
                TaskStatus.TODO,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(20)
        );


    }

    @Test
    @DisplayName("Debe devolver la tarea SIN comentarios cuando withComments es false")
    void testExecute_ShouldReturnTask_WithoutComments() {
        when(taskMapper.toResponseDTO(mockTask)).thenReturn(mockTaskDTO);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));

        TaskWithCommentsResponseDTO result = getTaskByIdUseCase.execute(taskId, false);

        assertNotNull(result);
        assertEquals(taskId, result.id());
        assertEquals("Tarea de Prueba", result.title());
        assertNotNull(result.comments(), "La lista de comentarios no debe ser nula");
        assertTrue(result.comments().isEmpty(), "La lista de comentarios debe estar vacía");

        verify(taskRepository).findById(taskId);
        verify(taskMapper).toResponseDTO(mockTask);
        verify(commentRepository, never()).findAllByTaskId(anyLong());
        verify(commentMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Debe devolver la tarea CON comentarios cuando withComments es true")
    void testExecute_ShouldReturnTask_WithComments() {
        TaskComment mockComment1 = mock(TaskComment.class);
        TaskComment mockComment2 = mock(TaskComment.class);
        List<TaskComment> commentList = Arrays.asList(mockComment1, mockComment2);

        CommentResponseDTO mockCommentDTO1 = new CommentResponseDTO(10L, mockTask, "Texto 1", "Autor 1", LocalDateTime.now());
        CommentResponseDTO mockCommentDTO2 = new CommentResponseDTO(11L, mockTask, "Texto 2", "Autor 2", LocalDateTime.now());

        when(taskMapper.toResponseDTO(mockTask)).thenReturn(mockTaskDTO);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));
        when(commentRepository.findAllByTaskId(taskId)).thenReturn(commentList);
        when(commentMapper.toResponseDTO(mockComment1)).thenReturn(mockCommentDTO1);
        when(commentMapper.toResponseDTO(mockComment2)).thenReturn(mockCommentDTO2);

        TaskWithCommentsResponseDTO result = getTaskByIdUseCase.execute(taskId, true);

        assertNotNull(result);
        assertEquals(taskId, result.id());
        assertNotNull(result.comments());
        assertEquals(2, result.comments().size(), "Debe haber dos comentarios");
        assertTrue(result.comments().contains(mockCommentDTO1));
        assertTrue(result.comments().contains(mockCommentDTO2));

        verify(taskRepository).findById(taskId);
        verify(taskMapper).toResponseDTO(mockTask);
        verify(commentRepository).findAllByTaskId(taskId); // Se debe llamar al repo de comentarios
        verify(commentMapper, times(2)).toResponseDTO(any(TaskComment.class)); // Se debe llamar al mapper de comentarios
    }

    @Test
    @DisplayName("Debe devolver la tarea con lista vacía si withComments es true pero no hay comentarios")
    void testExecute_ShouldReturnTask_WithEmptyCommentList() {
        when(taskMapper.toResponseDTO(mockTask)).thenReturn(mockTaskDTO);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));
        when(commentRepository.findAllByTaskId(taskId)).thenReturn(Collections.emptyList());

        TaskWithCommentsResponseDTO result = getTaskByIdUseCase.execute(taskId, true);

        assertNotNull(result);
        assertEquals(taskId, result.id());
        assertNotNull(result.comments());
        assertTrue(result.comments().isEmpty(), "La lista de comentarios debe estar vacía");

        verify(taskRepository).findById(taskId);
        verify(taskMapper).toResponseDTO(mockTask);
        verify(commentRepository).findAllByTaskId(taskId);
        verify(commentMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si la tarea no existe")
    void testExecute_ShouldThrowException_WhenTaskNotFound() {
        Long invalidTaskId = 99L;
        when(taskRepository.findById(invalidTaskId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            getTaskByIdUseCase.execute(invalidTaskId, false);
        });

        assertEquals("Task not found with id: " + invalidTaskId, exception.getMessage());

        verify(taskRepository).findById(invalidTaskId);
        verify(taskMapper, never()).toResponseDTO(any());
        verify(commentRepository, never()).findAllByTaskId(anyLong());
    }
}