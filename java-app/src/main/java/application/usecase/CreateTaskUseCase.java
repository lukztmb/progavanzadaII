package application.usecase;

//import application.dto.request.ProjectRequestDTO;
import org.springframework.stereotype.Service;

import application.dto.request.TaskRequestDTO;
import application.dto.response.TaskResponseDTO;
import application.mapper.TaskMapper;
import domain.model.Project;
import domain.model.ProjectStatus;
import domain.model.Task;
import domain.repository.ProjectRepository;
import domain.repository.TaskRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import infrastructure.exception.DuplicateResourceException;
import infrastructure.exception.ResourceNotFoundException;

@Service
public class CreateTaskUseCase {
    /**
     * El caso de uso hace:
     * 1. Busca y valida que el Proyecto exista y no esté CERRADO.
     * 2. Valida que el título no esté duplicado en ese proyecto.
     * 3. Mapea el DTO a dominio (el mapper se encarga de llamar
     * a Task.create() y setear las fechas).
     * 4. Guarda la nueva tarea en el repo.
     * 5. Mapea el dominio a DTO para respuesta.
     */


    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectRepository projectRepository;

    public CreateTaskUseCase(TaskRepository taskRepository, TaskMapper taskMapper, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectRepository = projectRepository;
    }

    // CAMBIO 1: Ahora recibimos el ID del proyecto (de la URL) y el DTO de la tarea
    public TaskResponseDTO execute(Long projectId, TaskRequestDTO taskDTO) {

        /* 1) Buscar el proyecto real en la BD por ID (ya no por nombre desde un DTO) */
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("El proyecto no fue encontrado con id: " + projectId));

        /* 2) Validar que el proyecto (entidad real) no esté cerrado */
        if (project.getStatus() == ProjectStatus.CLOSED) {
            throw new BusinessRuleViolationsException("No se puede agregar una tarea a un proyecto Cerrado (CLOSED).");
        }

        /* 3) Validar si existe la tarea en el proyecto
         * NOTA: Usamos 'project' (la entidad que recuperamos arriba)
         * y taskDTO.title() */
        if (taskRepository.existByTitleAndProject(taskDTO.title(), project)) {
            throw new DuplicateResourceException("Ya existe una tarea con el mismo titulo en este proyecto.");
        }

        /* 4) Mapear a Dominio pasando AMBOS objetos */
        Task newTask = taskMapper.toDomain(taskDTO, project);

        /* 5) Guardar */
        newTask = taskRepository.save(newTask);

        /* 6) Responder */
        return taskMapper.toResponseDTO(newTask);
    }
}



