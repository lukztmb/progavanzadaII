package application.dto.response;

import domain.model.Project;
import domain.model.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponseDTO(
        Long id,
        String title,
        Project project,
        Integer estimatedHours,
        String assignee,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime finishedAt
){}
