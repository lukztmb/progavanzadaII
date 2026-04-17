package application.dto.response;

import domain.model.Task;

import java.time.LocalDateTime;

public record CommentResponseDTO (
        Long id,
        Task task,
        String text,
        String author,
        LocalDateTime createdAt
){ }
