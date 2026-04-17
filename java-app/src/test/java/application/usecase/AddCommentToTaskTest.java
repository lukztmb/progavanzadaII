package application.usecase;

import application.dto.request.TaskCommentRequestDTO;
import application.dto.response.CommentResponseDTO;
import application.mapper.TaskCommentMapper;
import domain.model.Task;
import domain.model.TaskComment;
import domain.repository.TaskCommentRepository;
import domain.repository.TaskRepository;
import infrastructure.exception.ResourceNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitario para AddCommentToTaskUseCase.
 * Verifica la lógica de negocio aislando las dependencias.
 */
@ExtendWith(MockitoExtension.class)
public class AddCommentToTaskTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskCommentRepository commentRepository;

    @Mock
    private TaskCommentMapper commentMapper;

    @InjectMocks
    private AddCommentToTaskUseCase addCommentToTaskUseCase;

    @Captor
    private ArgumentCaptor<TaskComment> commentCaptor;

    @Test
    @DisplayName("Debe guardar un comentario si la tarea existe")
    void testExecute_ShouldSaveComment_WhenTaskExists() {
        // 1. Arrange
        Long taskIdFromUrl = 1L;

        TaskCommentRequestDTO requestDTO = new TaskCommentRequestDTO(
                "Este es un comentario de prueba",
                "Autor de Prueba"
        );

        Task mockTaskFound = mock(Task.class);
        TaskComment savedCommentMock = mock(TaskComment.class);

        CommentResponseDTO mockResponseDto = new CommentResponseDTO(
                100L,
                mockTaskFound,
                "Este es un comentario de prueba",
                "Autor de Prueba",
                LocalDateTime.now()
        );

        when(taskRepository.findById(taskIdFromUrl)).thenReturn(Optional.of(mockTaskFound));
        when(commentRepository.save(any(TaskComment.class))).thenReturn(savedCommentMock);
        when(commentMapper.toResponseDTO(savedCommentMock)).thenReturn(mockResponseDto);

        // 2. Act
        LocalDateTime beforeTest = LocalDateTime.now();
        CommentResponseDTO actualResponse = addCommentToTaskUseCase.execute(requestDTO, taskIdFromUrl);
        LocalDateTime afterTest = LocalDateTime.now();

        // 3. Assert
        assertNotNull(actualResponse);
        assertEquals(mockResponseDto.id(), actualResponse.id());
        assertEquals(mockResponseDto.text(), actualResponse.text());
        assertEquals(mockResponseDto.author(), actualResponse.author());
        assertEquals(mockTaskFound, actualResponse.task());
        assertNotNull(actualResponse.createdAt());

        verify(taskRepository).findById(taskIdFromUrl);
        verify(commentRepository).save(commentCaptor.capture());
        verify(commentMapper).toResponseDTO(savedCommentMock);

        TaskComment commentToSave = commentCaptor.getValue();
        assertNull(commentToSave.getId(), "El ID debe ser nulo antes de guardarse");
        assertEquals(mockTaskFound, commentToSave.getTask());
        assertEquals(requestDTO.text(), commentToSave.getText());
        assertEquals(requestDTO.author(), commentToSave.getAuthor());

        assertNotNull(commentToSave.getCreatedAt());
        assertTrue(commentToSave.getCreatedAt().isAfter(beforeTest.minusNanos(1)), "El Timestamp debe ser posterior al inicio del test");
        assertTrue(commentToSave.getCreatedAt().isBefore(afterTest.plusNanos(1)), "El Timestamp debe ser anterior al fin del test");
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si la tarea no existe")
    void testExecute_ShouldThrowException_WhenTaskNotFound() {
        // 1. Arrange
        Long invalidTaskId = 99L;
        TaskCommentRequestDTO requestDTO = new TaskCommentRequestDTO(
                "Comentario fallido",
                "Autor"
        );
        when(taskRepository.findById(invalidTaskId)).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            addCommentToTaskUseCase.execute(requestDTO, invalidTaskId);
        });

        assertEquals("Task not found with id: " + invalidTaskId, exception.getMessage());

        verify(taskRepository).findById(invalidTaskId);
        verify(commentRepository, never()).save(any());
        verify(commentMapper, never()).toResponseDTO(any());
    }
}