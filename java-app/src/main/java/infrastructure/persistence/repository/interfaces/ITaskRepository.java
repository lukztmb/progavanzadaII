package infrastructure.persistence.repository.interfaces;

import domain.model.TaskStatus;
import infrastructure.persistence.entities.ProjectEntity;
import infrastructure.persistence.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//import java.util.Optional;

@Repository
public interface ITaskRepository extends JpaRepository<TaskEntity, Long> {
    boolean existsByTitleAndProject(String title, ProjectEntity project);
    List<TaskEntity> findAllByStatus(TaskStatus status);
}
