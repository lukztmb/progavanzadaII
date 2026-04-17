package infrastructure.persistence.repository.interfaces;

import infrastructure.persistence.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProjectRepository extends JpaRepository<ProjectEntity, Long> {
    boolean existsByName(String name);
}
