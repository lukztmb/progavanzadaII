package domain.model;

import infrastructure.exception.BusinessRuleViolationsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
//import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    @Order(1)
    @DisplayName("Camino 1: Objeto Task Completo")

    public void OBjetoTaskCompleto() {
        //Creacion de un proyecto para la tarea
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(10);
        String name = "Proyecto Test";

        // 1. Act (Actuar)
        Project project = Project.create(name,
                startDate,
                endDate,
                ProjectStatus.PLANNED,
                Optional.of("Descripción de prueba")
        );

        //Fechas de inicio y fin para task

        LocalDateTime startDateTask = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDateTask = startDateTask.plusDays(10);

        //Crear Objeto Tarea
        Task task = Task.create(null,
                "Prog II: implement",
                project,
                15,
                "Joaquin del Canto",
                TaskStatus.TODO,
                startDateTask,
                endDateTask);

        assertNotNull(task, "La tarea no deberia ser nula");

        assertNull(task.getId(), "El id debe ser nulo antes de guardarse");

        assertEquals("Prog II: implement", task.getTitle());
        assertEquals(project, task.getProyect(), "El proyecto asignado no es el correcto");
        assertEquals(15, task.getEstimatedHours());
        assertEquals(15, task.getEstimatedHours());
        assertEquals("Joaquin del Canto", task.getAssignee());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertEquals(startDateTask, task.getCreatedAt());
        assertEquals(endDateTask, task.getFinishedAt());

    }


    @Test
    @Order(2)
    @DisplayName("Nombre null, lazar una exception")

    void nombreNullLazarUnaException() {

        //Creacion de un proyecto para la tarea
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(10);
        String name = "Proyecto Test";

        // 1. Act (Actuar)
        Project project = Project.create(name,
                startDate,
                endDate,
                ProjectStatus.PLANNED,
                Optional.of("Descripción de prueba")
        );

        //Fechas de inicio y fin para task
        LocalDateTime startDateTask = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDateTask = LocalDateTime.now().plusDays(10);

        //Crear Objeto Tarea con expcetion

        Exception exception = assertThrows(BusinessRuleViolationsException.class, () -> {
            Task.create(null,
                    "",
                    project,
                    15,
                    "Joaquin del Canto",
                    TaskStatus.TODO,
                    startDateTask,
                    endDateTask);

        });
        System.out.print(exception.getMessage());
        assertEquals("El titulo de la tarea no puede estar vacio", exception.getMessage());

    }

    @Test
    @Order(3)
    @DisplayName("La fecha de creacion no puede ser posterior a la finalizacion")

    void fechaInicioNoPuedeSerPosteriorAFinalizacion() {

        //Creacion de un proyecto para la tarea
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(10);
        String name = "Proyecto Test";

        // 1. Act (Actuar)
        Project project = Project.create(name,
                startDate,
                endDate,
                ProjectStatus.PLANNED,
                Optional.of("Descripción de prueba")
        );

        //Fechas de inicio y fin para task
        LocalDateTime startDateTask = LocalDateTime.now().plusDays(12);
        LocalDateTime endDateTask = LocalDateTime.now().plusDays(10);

        //Crear Objeto Tarea con expcetion

        Exception exception = assertThrows(BusinessRuleViolationsException.class, () -> {
            Task.create(null,
                    "Proyecto Pepecito",
                    project,
                    15,
                    "Joaquin del Canto",
                    TaskStatus.TODO,
                    startDateTask,
                    endDateTask);

        });
        System.out.print(exception.getMessage());
        assertEquals("La fecha de inicio no puede ser posterio a fecha de fin", exception.getMessage());

    }


    @Test
    @Order(4)
    @DisplayName("Si el estado es DONE, se asigna fecha actual")

    void estadoDoneSeAsignaFechaActual() {
        //Creacion de un proyecto para la tarea
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(10);
        String name = "Proyecto Test";

        // 1. Act (Actuar)
        Project project = Project.create(name,
                startDate,
                endDate,
                ProjectStatus.PLANNED,
                Optional.of("Descripción de prueba")
        );

        //Fechas de inicio y fin para task
        LocalDateTime startDateTask = LocalDateTime.now().plusDays(1);
        LocalDateTime endDateTask = LocalDateTime.now().plusDays(10);

        //Crear Objeto Tarea con expcetion
        Task task = Task.create(null,
                "Proyecto Pepecito",
                project,
                15,
                "Joaquin del Canto",
                TaskStatus.DONE,
                startDateTask,
                endDateTask);

        System.out.println(task.getStatus());
        System.out.println(task.getCreatedAt());
        assertEquals(TaskStatus.DONE, task.getStatus());


    }


    @Test
    @Order(5)
    @DisplayName("Los estados de Task solo son: IN_PROGRESS, DONE, TODO")

    void estadosTaskSoloSonTres(){
        //Creacion de un proyecto para la tarea
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(10);
        String name = "Proyecto Test";

        // 1. Act (Actuar)
        Project project = Project.create(name,
                startDate,
                endDate,
                ProjectStatus.PLANNED,
                Optional.of("Descripción de prueba")
        );

        //Fechas de inicio y fin para task
        LocalDateTime startDateTask = LocalDateTime.now().plusDays(1);
        LocalDateTime endDateTask = LocalDateTime.now().plusDays(10);

        //Crear Objeto Tarea con expcetion
        Exception exception =  assertThrows(BusinessRuleViolationsException.class, () -> {
            Task task = Task.create(null,
                    "Proyecto Pepecito",
                    project,
                    15,
                    "Joaquin del Canto",
                    null,
                    startDateTask,
                    endDateTask);
        });

        assertEquals("EL estado de la tarea no puede ser null", exception.getMessage());

    }

}
