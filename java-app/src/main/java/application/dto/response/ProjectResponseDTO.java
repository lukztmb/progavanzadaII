package application.dto.response;

import domain.model.ProjectStatus;

import java.time.LocalDate;

public record ProjectResponseDTO(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        ProjectStatus status,
        String description
) {  }
