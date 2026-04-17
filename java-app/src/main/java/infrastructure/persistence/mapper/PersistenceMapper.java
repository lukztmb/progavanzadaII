package infrastructure.persistence.mapper;

import domain.model.*;
import infrastructure.persistence.entities.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersistenceMapper {
    //                      Project
    public Project toDomain(ProjectEntity entity) {
        Project domain = Project.create(
                entity.getName(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus(),
                Optional.ofNullable(entity.getDescription())
        );
        domain.setId(entity.getId());
        return domain;
    }

    public ProjectEntity toEntity(Project domain) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setStatus(domain.getStatus());
        entity.setDescription(domain.getDescription().orElse(null));
        return entity;
    }

    //                      Task
    public Task toDomain(TaskEntity entity) {
        Task domain = Task.create(
                entity.getId(),
                entity.getTitle(),
                toDomain(entity.getProject()), // Mapea el proyecto padre
                entity.getEstimatedHours(),
                entity.getAssignee(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getFinishedAt()
        );
        return domain;
    }

    public TaskEntity toEntity(Task domain) {
        TaskEntity entity = new TaskEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        entity.setProject(toEntity(domain.getProyect())); // Mapea el proyecto padre
        entity.setEstimatedHours(domain.getEstimatedHours());
        entity.setAssignee(domain.getAssignee());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setFinishedAt(domain.getFinishedAt());
        return entity;
    }

    //                          TaskComment
    public TaskComment toDomain(TaskCommentEntity entity) {
        TaskComment domain = TaskComment.create(
                toDomain(entity.getTask()), // Mapea la tarea padre
                entity.getText(),
                entity.getAuthor(),
                entity.getCreatedAt()
        );
        domain.setId(entity.getId());
        return domain;
    }

    public TaskCommentEntity toEntity(TaskComment domain) {
        TaskCommentEntity entity = new TaskCommentEntity();
        entity.setId(domain.getId());
        entity.setTask(toEntity(domain.getTask())); // Mapea la tarea padre
        entity.setText(domain.getText());
        entity.setAuthor(domain.getAuthor());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
