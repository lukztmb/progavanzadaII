package domain.repository;

import domain.model.TaskComment;

import java.util.List;

public interface TaskCommentRepository {

    TaskComment save(TaskComment comment);
    List<TaskComment> findAllByTaskId(Long taskId);
}
