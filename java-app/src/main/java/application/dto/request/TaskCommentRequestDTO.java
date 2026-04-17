package application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskCommentRequestDTO(
        @NotNull(message = "There can't be empty comments")
        @NotBlank(message = "There can't be empty comments")
        String text,

        @NotNull(message = "There can't be comments without an author")
        @NotBlank(message = "There can't be comments without an author")
        String author
){ }
