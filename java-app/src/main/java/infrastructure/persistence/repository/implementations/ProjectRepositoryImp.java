package infrastructure.persistence.repository.implementations;

import domain.model.Project;
import domain.repository.ProjectRepository;
import infrastructure.persistence.entities.ProjectEntity;
import infrastructure.persistence.mapper.PersistenceMapper;
import infrastructure.persistence.repository.interfaces.IProjectRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProjectRepositoryImp implements ProjectRepository {
    private final IProjectRepository jpaRepository;
    private final PersistenceMapper mapper;

    public ProjectRepositoryImp(IProjectRepository jpaRepository, PersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public Project save(Project newProject) {
        //Convertir Dominio -> Entidad
        ProjectEntity entityToSave = mapper.toEntity(newProject);

        //Guardar con JPA
        ProjectEntity savedEntity = jpaRepository.save(entityToSave);

        //Convertir Entidad -> Dominio
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Project> findById(Long id) {
        Optional<ProjectEntity> optionalEntity = jpaRepository.findById(id);
        return optionalEntity.map(mapper::toDomain);
    }
}
