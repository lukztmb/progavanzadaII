package application.usecase;

import application.dto.request.ProjectRequestDTO;
import application.dto.response.ProjectResponseDTO;
import application.mapper.ProjectMapper;
import domain.model.Project;
import domain.model.ProjectStatus;
import domain.repository.ProjectRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import infrastructure.exception.DuplicateResourceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateProjectTest {

    @Mock //Simulacion de repo
    private ProjectRepository repo;

    @Mock //Simulacion de mapper
    private ProjectMapper mapper;

    @InjectMocks // Creamos una instancia del CU
    private CreateProjectUseCase useCase;

    private ProjectRequestDTO requestDTO;
    private Project savedProject;
    private ProjectResponseDTO expectedResponse;

    /*
        Preparamos los datos para los test
     */
    @BeforeEach
    void setUp(){
        // Preparamos los datos de entrada DTO
        requestDTO = new ProjectRequestDTO(
                "Proyecto pepe",
                LocalDate.now(),
                LocalDate.now().plusDays(30), //le damos 30 dias como fecha de fin
                ProjectStatus.PLANNED,
                "Proyecto pepe suma importancia"
        );

        // Entidad a guardar (lo que nos da el repo con el id)
        savedProject = Project.create(
                requestDTO.name(),
                requestDTO.startDate(),                                                                                                                                                                                                                                                                                                                                                 
                requestDTO.endDate(),
                requestDTO.status(),
                Optional.ofNullable(requestDTO.description())
        );
        savedProject.setId(1L); //ID asignado por la BD

        // Respuesta final
        expectedResponse = new ProjectResponseDTO(
                1L,
                savedProject.getName(),
                savedProject.getStartDate(),
                savedProject.getEndDate(),
                savedProject.getStatus(),
                savedProject.getDescription().orElse(null)
        );
    }

    @Test
    void testCreateProject_SHouldSecceed_WhenDataIsValid(){
        when(repo.existsByName(requestDTO.name())).thenReturn(false);
        when(mapper.toDomain(requestDTO)).thenReturn(savedProject);
        when(repo.save(any(Project.class))).thenReturn(savedProject);
        when(mapper.toResponseDTO(savedProject)).thenReturn(expectedResponse);

        ProjectResponseDTO actual = useCase.execute(requestDTO);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expectedResponse.id(), actual.id());
        Assertions.assertEquals(expectedResponse.name(), actual.name());
    }

    @Test
    void testCreateProject_SHouldSecceed_WhenNameIsDuplicate(){
        when(repo.existsByName(requestDTO.name())).thenReturn(true);

        Exception exception = assertThrows(DuplicateResourceException.class,
                () -> useCase.execute(requestDTO));

        Assertions.assertEquals("Ya existe un proyecto con el mismo nombre", exception.getMessage());
    }

    @Test
    void testCreateProyect_ShouldThrowException_WhenDomainValidationFails() {
        when(repo.existsByName(requestDTO.name())).thenReturn(false);

        when(mapper.toDomain(requestDTO)).thenThrow(new BusinessRuleViolationsException("La fecha de fin es invalida"));

        Exception exception = assertThrows(BusinessRuleViolationsException.class, () -> {
            useCase.execute(requestDTO);
        });

        Assertions.assertEquals("La fecha de fin es invalida", exception.getMessage());
    }
}
