package application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import domain.model.Project;
import domain.model.TaskStatus;

import java.util.List;

/**
 * DTO especial que extiende la TaskResponseDTO para incluir
 * una lista de comentarios, solo si se solicitan (JsonInclude.Include.NON_EMPTY).
 */
public record TaskWithCommentsResponseDTO(
        Long id,
        String title,
        Project project,
        Integer estimatedHours,
        String assignee,
        TaskStatus status,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) // No incluir si la lista es nula o vacía
        List<CommentResponseDTO> comments
) {
    /**
     * Constructor auxiliar para crear este DTO a partir
     * de un TaskResponseDTO y una lista de comentarios.
     */
    public TaskWithCommentsResponseDTO(TaskResponseDTO task, List<CommentResponseDTO> comments) {
        this(
                task.id(),
                task.title(),
                task.project(),
                task.estimatedHours(),
                task.assignee(),
                task.status(),
                comments
        );
    }
}