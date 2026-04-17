package domain.model;

import infrastructure.exception.BusinessRuleViolationsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProjectTest {
    @Test
    @DisplayName("Debe crear un Proyect exitosamente cuando los datos son validos")
    void testCreateProyect_ShouldSucceed_WhenDataIsValid() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(10);
        String name = "Proyecto Test";

        // 1. Act (Actuar)
        Project project = Project.create(name,
                startDate,
                endDate,
                ProjectStatus.PLANNED,
                Optional.of("Descripcion de prueba")
        );

        // 2. Assert (Verificar)
        assertNotNull(project);
        assertEquals(name, project.getName());
        assertEquals(startDate, project.getStartDate());
        assertEquals(endDate, project.getEndDate());
        assertEquals(ProjectStatus.PLANNED, project.getStatus());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si endDate es anterior a startDate")
    void testCreateProyect_ShouldThrowException_WhenEndDateIsBeforeStartDate() {
        String name = "Proyecto Test";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.minusDays(1); // Fecha invalida

        // Verificamos que se lanza la excepción correcta
        Exception exception = assertThrows(BusinessRuleViolationsException.class, () -> {
            Project.create(name,
                    startDate,
                    endDate,
                    ProjectStatus.PLANNED,
                    Optional.of("Descripcion de prueba")
            );
        });

        // Verificamos el mensaje (basado en la regla del README)
        assertEquals("La fecha de fin es invalida", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si endDate es anterior a hoy")
    void testCreateProyect_ShouldThrowException_WhenEndDateIsInThePast() {
        String name = "Proyecto Test";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(-10);//Fecha invalida

        Exception exception = assertThrows(BusinessRuleViolationsException.class, () -> {
            // Usamos 'startDate' Y endDate para que pase la primera validación
            // pero falle la segunda (endDate >= startDate)
            Project.create(name,
                    startDate,
                    endDate,
                    ProjectStatus.PLANNED,
                    Optional.of("Descripción de prueba")
            );
        });

        assertEquals("La fecha de fin es invalida", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si falta un campo requerido (ej: name es null)")
    void testCreateProyect_ShouldThrowException_WhenNameIsNull() {
        String name = null;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(10);

        Exception exception = assertThrows(BusinessRuleViolationsException.class, () -> {
            Project.create(name,
                    startDate,
                    endDate,
                    ProjectStatus.PLANNED,
                    Optional.of("Descripcion de prueba")
            );
        });

        assertEquals("El nombre no puede ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("canAddTask debe devolver true si el estado NO es CLOSED")
    void testCanAddTask_ShouldReturnTrue_WhenStatusIsActive() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(10);
        String name = "Proyecto Test";

        // Creamos un proyecto valido con estado ACTIVE
        Project activeProject = Project.create(name,
                startDate,
                endDate,
                ProjectStatus.ACTIVE,
                Optional.of("Descripcion de prueba")
        );

        assertTrue(activeProject.canAddTask());
    }

    @Test
    @DisplayName("canAddTask debe devolver false si el estado ES CLOSED")
    void testCanAddTask_ShouldReturnFalse_WhenStatusIsClosed() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(10);
        String name = "Proyecto Test";

        // Creamos un proyecto valido con estado CLOSED
        Project closedProject = Project.create(name,
                startDate,
                endDate,
                ProjectStatus.CLOSED,
                Optional.of("Descripcion de prueba")
        );

        assertFalse(closedProject.canAddTask());
    }
}
