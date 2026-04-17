package application.usecase;

import application.dto.response.TaskResponseDTO;
import application.mapper.TaskMapper;
import domain.model.TaskStatus;
import domain.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import domain.model.Task;

@Service
public class FindTaskUseCase {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public FindTaskUseCase(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    /**
     * Ejecuta la busqueda para las tareas por un estado determinado
     *
     * @param status El estado de las tareas que se buscaran
     * @return una lista de TaskResponseDTO
     */
    public List<TaskResponseDTO> execute(TaskStatus status) {
        List<Task> tasks = taskRepository.findByStatus(status);
        return tasks.stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
