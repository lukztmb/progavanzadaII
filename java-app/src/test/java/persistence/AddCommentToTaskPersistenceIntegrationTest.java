package persistence;

import application.usecase.AddCommentToTaskUseCase;
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

@ExtendWith(MockitoExtension.class)
public class AddCommentToTaskPersistenceIntegrationTest {
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
    @DisplayName("Shall save a comment if a given task exists")
    void testExecute_ShouldSaveComment_WhenTaskExists_ImprovedLogic() {
        // 1. Arrange

        Long taskIdFromUrl = 1L;

        TaskCommentRequestDTO requestDTO = new TaskCommentRequestDTO(
                "Test Comment, for a, supposedly, existing Task",
                "yetanotherauthor"
        );

        Task mockTaskFound = mock(Task.class);

        TaskComment savedCommentMock = mock(TaskComment.class);

        CommentResponseDTO mockResponseDto = new CommentResponseDTO(
                100L,
                mockTaskFound,
                "Test Comment, for a, supposedly, existing Task",
                "yetanotherauthor",
                LocalDateTime.now() // Esta fecha es solo para el mock
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
        assertNull(commentToSave.getId(), "ID expected to be null before saving");
        assertEquals(mockTaskFound, commentToSave.getTask());
        assertEquals(requestDTO.text(), commentToSave.getText());
        assertEquals(requestDTO.author(), commentToSave.getAuthor());

        assertNotNull(commentToSave.getCreatedAt());
        assertTrue(commentToSave.getCreatedAt().isAfter(beforeTest.minusNanos(1)), "Timestamp should be after the test started");
        assertTrue(commentToSave.getCreatedAt().isBefore(afterTest.plusNanos(1)), "Timestamp should be before the test finished");
    }

    @Test
    @DisplayName("Shall throw ResourceNotFoundException if there is no task associated to the given ID")
    void testExecute_ShouldThrowException_WhenTaskNotFound_ImprovedLogic() {
        // 1. Arrange
        Long invalidTaskId = 99L;

        // El DTO (simplificado)
        TaskCommentRequestDTO requestDTO = new TaskCommentRequestDTO(
                "Untestable Comment, for an untestable Task",
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
