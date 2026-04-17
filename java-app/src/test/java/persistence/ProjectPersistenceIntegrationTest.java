package persistence;

import application.dto.request.ProjectRequestDTO;
import application.dto.response.ProjectResponseDTO;
import application.usecase.CreateProjectUseCase;
import domain.model.Project;
import domain.model.ProjectStatus;
import domain.repository.ProjectRepository;
import infrastructure.exception.DuplicateResourceException;
import model.integradorsinteclados.IntegradorSinTecladosApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Este test comprueba la integración COMPLETA de la persistencia para Project.
 * Carga todo el contexto de Spring, incluyendo la base de datos H2 en memoria.
 */
@SpringBootTest(classes = IntegradorSinTecladosApplication.class) //Carga el contexto COMPLETO de Spring
@Transactional //Hace rollback de la DB después de cada test
public class ProjectPersistenceIntegrationTest {

    @Autowired
    private CreateProjectUseCase createProjectUseCase;  //Inyecta el Caso de Uso REAL (con todas sus dependencias reales)

    @Autowired
    private ProjectRepository projectRepository; //Inyecta el Repositorio REAL (para hacer verificaciones)

    @Test
    void testUseCase_ShouldSaveToDatabase() {
        ProjectRequestDTO request = new ProjectRequestDTO(
                "Proyecto de Integracion",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                ProjectStatus.PLANNED,
                "Test desde @SpringBootTest"
        );

        // Llama al caso de uso, que llamará a la Impl,
        // que llamará al Mapper, que llamará a JPA
        ProjectResponseDTO response = createProjectUseCase.execute(request);

        assertNotNull(response);
        assertNotNull(response.id(), "El ID no deberia ser nulo si se guardo en la DB");
        assertEquals("Proyecto de Integracion", response.name());

        //Comprueba que el repositorio de dominio puede encontrarlo
        assertTrue(projectRepository.existsByName("Proyecto de Integracion"));
    }

    @Test
    void testUseCase_ShouldFailOnDuplicateName() {
        ProjectRequestDTO request1 = new ProjectRequestDTO(
                "Proyecto Duplicado",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                ProjectStatus.PLANNED,
                "Primer insert"
        );
        ProjectRequestDTO request2 = new ProjectRequestDTO(
                "Proyecto Duplicado",
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                ProjectStatus.ACTIVE,
                "Segundo insert (debe fallar)"
        );

        //El primero debe funcionar
        createProjectUseCase.execute(request1);

        //El segundo debe fallar (prueba la regla de negocio + DB)
        assertThrows(DuplicateResourceException.class, () -> {
            createProjectUseCase.execute(request2);
        });
    }

    @Test
    void testRepository_SaveAndFindById() {
        // Este test prueba la capa de repositorio directamente (Impl -> Mapper -> JPA)

        Project domainProject = Project.create(
                "Proyecto de Test Directo",
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                ProjectStatus.ACTIVE,
                Optional.of("Descripcion directa")
        );

        Project savedProject = projectRepository.save(domainProject);

        //Verificar que se guardó
        assertNotNull(savedProject);
        assertNotNull(savedProject.getId(), "El ID debe ser asignado por la DB");
        assertEquals("Proyecto de Test Directo", savedProject.getName());

        //Verificar que se puede encontrar
        Optional<Project> foundProjectOpt = projectRepository.findById(savedProject.getId());

        //Verificar que se encontró
        assertTrue(foundProjectOpt.isPresent(), "El proyecto no se encontro en la DB");
        assertEquals("Proyecto de Test Directo", foundProjectOpt.get().getName());
    }
}