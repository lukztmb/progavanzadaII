package application.mapper;

import application.dto.request.ProjectRequestDTO;
import application.dto.response.ProjectResponseDTO;
import domain.model.Project;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProjectMapper {
    /**
     * Convertimos el DTO (solicitud) a una entidad del dominio.
     *  Utilizamos el .crate de Project
     */
    public Project toDomain(ProjectRequestDTO dto){
        if (dto == null){
            return null;
        }
        return Project.create(dto.name(),
                dto.startDate(),
                dto.endDate(),
                dto.status(),
                Optional.ofNullable(dto.description()));
    }

    /**
     * Convertimos la entidad del dominio a un DTO
     */
    public ProjectResponseDTO toResponseDTO(Project project){
        if (project == null) {
            return null;
        }
        return new ProjectResponseDTO(project.getId(),
                project.getName(),
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus(),
                project.getDescription().orElse(null));
    }
}
