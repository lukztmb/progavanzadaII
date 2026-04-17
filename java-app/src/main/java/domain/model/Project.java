package domain.model;

import infrastructure.exception.BusinessRuleViolationsException;

import java.time.LocalDate;
import java.util.Optional;

public class Project {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private Optional<String> description;

    private Project(String name,
                    LocalDate startDate,
                    LocalDate endDate,
                    ProjectStatus status,
                    Optional<String> description) {
        this.id = null;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.description = description;

    }

    /**
     *  Utiliza Factory Method
     *  El id no los da la db, se setea despues
     * @param name
     * @param startDate
     * @param endDate
     * @param status
     * @param description
     * @return nuevo proyecto
     */
    public static Project create(String name,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 ProjectStatus status,
                                 Optional<String> description){
        if (name == null || name.isBlank()) {
            throw new BusinessRuleViolationsException("El nombre no puede ser nulo");
        }
        if (startDate == null || endDate == null) {
            throw new BusinessRuleViolationsException("La fecha de inicio o final final no pueden ser nulas");
        }
        if (endDate.isBefore(LocalDate.now())){
            throw new BusinessRuleViolationsException("La fecha de fin es invalida");
        }
        if (startDate.isAfter(endDate)){
            throw new BusinessRuleViolationsException("La fecha de inicio es invalida");
        }
        if (status == null) {
            throw new BusinessRuleViolationsException("El estatus del proyecto no puede ser nulo");
        }
        return new Project(name, startDate, endDate, status, description);
    }

    /**
     * Método de comportamiento del dominio.
     * Verifica la regla: "Cannot add a Task to a Project with status CLOSED"
     */
    public boolean canAddTask() {
        return this.status != ProjectStatus.CLOSED;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public Optional<String> getDescription() {
        return description;
    }
}
