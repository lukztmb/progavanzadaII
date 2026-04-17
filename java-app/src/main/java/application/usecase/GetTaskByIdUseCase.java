package application.usecase;

import application.dto.response.CommentResponseDTO;
import application.dto.response.TaskResponseDTO;
import application.dto.response.TaskWithCommentsResponseDTO;
import application.mapper.TaskCommentMapper;
import application.mapper.TaskMapper;
import infrastructure.exception.ResourceNotFoundException;
import domain.model.Task;
import domain.model.TaskComment;
import domain.repository.TaskCommentRepository;
import domain.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetTaskByIdUseCase {

    private final TaskRepository taskRepository;
    private final TaskCommentRepository commentRepository;
    private final TaskMapper taskMapper;
    private final TaskCommentMapper commentMapper;

    public GetTaskByIdUseCase(TaskRepository taskRepository, TaskCommentRepository commentRepository, TaskMapper taskMapper, TaskCommentMapper commentMapper) {
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.taskMapper = taskMapper;
        this.commentMapper = commentMapper;
    }

    /**
     * Ejecuta la búsqueda.
     * @param taskId El ID de la tarea.
     * @param withComments Flag para incluir o no los comentarios.
     * @return El DTO de la tarea, con o sin comentarios.
     * @throws ResourceNotFoundException si la tarea no existe.
     */
    public TaskWithCommentsResponseDTO execute(Long taskId, boolean withComments) {

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        TaskResponseDTO taskDto = taskMapper.toResponseDTO(task);

        List<CommentResponseDTO> commentDtoList = Collections.emptyList(); // Lista vacía por defecto

        if (withComments) {
            List<TaskComment> comments = commentRepository.findAllByTaskId(taskId);
            commentDtoList = comments.stream()
                    .map(commentMapper::toResponseDTO)
                    .collect(Collectors.toList());
        }

        return new TaskWithCommentsResponseDTO(taskDto, commentDtoList);
    }
}