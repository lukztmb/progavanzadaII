package application.dto.request;

import domain.model.ProjectStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ProjectRequestDTO(
    @NotBlank(message = "El nombre es requerido")
    String name,
    @NotNull(message = "La fecha de inicio es requerida")
    LocalDate startDate,
    @NotNull(message = "La fecha de fin es requerida")
    @FutureOrPresent(message = "La fecha de fin debe ser hoy o en el futuro")
    LocalDate endDate,
    @NotNull(message = "El estado es requerido")
    ProjectStatus status,
    String description
) { }
