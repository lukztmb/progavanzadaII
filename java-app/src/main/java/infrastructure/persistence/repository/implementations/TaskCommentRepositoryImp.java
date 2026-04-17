package infrastructure.persistence.repository.implementations;

import domain.model.TaskComment;
import domain.repository.TaskCommentRepository;
import infrastructure.persistence.entities.TaskCommentEntity;
import infrastructure.persistence.mapper.PersistenceMapper;
import infrastructure.persistence.repository.interfaces.ITaskCommentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskCommentRepositoryImp implements TaskCommentRepository {
    private final ITaskCommentRepository jpaRepository;
    private final PersistenceMapper mapper;

    public TaskCommentRepositoryImp(ITaskCommentRepository jpaRepository, PersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public TaskComment save(TaskComment comment) {
        // 1. Convertir Dominio -> Entidad
        TaskCommentEntity entityToSave = mapper.toEntity(comment);

        // 2. Guardar con JPA
        TaskCommentEntity savedEntity = jpaRepository.save(entityToSave);

        // 3. Convertir Entidad -> Dominio y devolver
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<TaskComment> findAllByTaskId(Long taskId) {
        // 1. Buscar todas las Entidades con JPA
        List<TaskCommentEntity> entities = jpaRepository.findAllByTaskId(taskId);

        // 2. Mapear la lista de Entidades a lista de Dominio

        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
