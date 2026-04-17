package infrastructure.persistence.repository.interfaces;

import infrastructure.persistence.entities.TaskCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITaskCommentRepository extends JpaRepository<TaskCommentEntity, Long> {
    List<TaskCommentEntity> findAllByTaskId(Long taskId);
}
