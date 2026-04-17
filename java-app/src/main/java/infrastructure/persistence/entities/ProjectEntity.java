package infrastructure.persistence.entities;

import domain.model.ProjectStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //autogenerado incremental
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING) //Gardar el enum como string
    @Column(nullable = false)
    private ProjectStatus status;

    private String description; //Cambio JPA no maneja bien el tema de Optional

    //Definios la relacion: un proyecto tiene muchas tareas
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskEntity> tasks = new ArrayList<>();

    public ProjectEntity() {}

    // Getters y Setters para todos los campos...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<TaskEntity> getTasks() { return tasks; }
    public void setTasks(List<TaskEntity> tasks) { this.tasks = tasks; }
}
