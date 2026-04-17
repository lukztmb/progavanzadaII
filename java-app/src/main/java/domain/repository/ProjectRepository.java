package domain.repository;

import domain.model.Project;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;

public interface ProjectRepository {
    boolean existsByName(@NotBlank(message = "El nombre es requerido") String name);

    Project save(Project newProject);

    Optional<Project> findById(Long id);
}
