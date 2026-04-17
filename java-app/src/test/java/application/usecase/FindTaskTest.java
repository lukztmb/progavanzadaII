package application.usecase;

import application.dto.response.TaskResponseDTO;
import application.mapper.TaskMapper;
import domain.model.Task;
import domain.model.TaskStatus;
import domain.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FindTaskTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper  taskMapper;

    @InjectMocks
    private FindTaskUseCase findTaskUseCase;

    @Test
    void testExecute_ShouldReturnListOfTasks_WhenTasksExist() {
        TaskStatus status = TaskStatus.IN_PROGRESS;

        // Mockeamos las entidades de dominio
        Task task1 = mock(Task.class);
        Task task2 = mock(Task.class);
        List<Task> taskList = Arrays.asList(task1, task2);

        // Mockeamos los DTOs de respuesta
        TaskResponseDTO dto1 = mock(TaskResponseDTO.class);
        TaskResponseDTO dto2 = mock(TaskResponseDTO.class);

        // Configuramos el comportamiento del repositorio
        when(taskRepository.findByStatus(status)).thenReturn(taskList);

        // Configuramos el comportamiento del mapper
        when(taskMapper.toResponseDTO(task1)).thenReturn(dto1);
        when(taskMapper.toResponseDTO(task2)).thenReturn(dto2);

        List<TaskResponseDTO> result = findTaskUseCase.execute(status);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    void testExecute_ShouldReturnEmptyList_WhenNoTasksFound() {
        TaskStatus status = TaskStatus.TODO;

        // El repositorio devuelve lista vacía
        when(taskRepository.findByStatus(status)).thenReturn(Collections.emptyList());

        List<TaskResponseDTO> result = findTaskUseCase.execute(status);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        // El mapper nunca debió ser llamado porque la lista estaba vacía
        verifyNoInteractions(taskMapper);
    }
}
