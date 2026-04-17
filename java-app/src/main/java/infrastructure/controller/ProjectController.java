package infrastructure.controller;

import application.dto.request.ProjectRequestDTO;
import application.dto.request.TaskCommentRequestDTO;
import application.dto.request.TaskRequestDTO;
import application.dto.response.CommentResponseDTO;
import application.dto.response.ProjectResponseDTO;
import application.dto.response.TaskResponseDTO;
import application.usecase.CreateProjectUseCase;
import application.usecase.FindTaskUseCase;
import domain.model.TaskStatus;
//import domain.repository.ProjectRepository;
import application.dto.response.TaskWithCommentsResponseDTO;
import application.usecase.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
//import java.util.concurrent.ForkJoinPool;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectController {

    // Casos de uso que se van a utilizar para responder a los post y get
    private final CreateProjectUseCase createProjectUseCase;
    private final FindTaskUseCase findTaskUseCase;
    private final AddCommentToTaskUseCase addCommentToTaskUseCase;
    private final GetTaskByIdUseCase getTaskByIdUseCase;
    private final CreateTaskUseCase createTaskUseCase;

    public ProjectController(CreateProjectUseCase createProjectUseCase,
                             GetTaskByIdUseCase getTaskByIdUseCase,
                             AddCommentToTaskUseCase addCommentToTaskUseCase,
                             FindTaskUseCase findTaskUseCase,
                             CreateTaskUseCase createTaskUseCase) {
        this.createProjectUseCase = createProjectUseCase;
        this.addCommentToTaskUseCase = addCommentToTaskUseCase;
        this.findTaskUseCase = findTaskUseCase;
        this.getTaskByIdUseCase = getTaskByIdUseCase;
        this.createTaskUseCase = createTaskUseCase;
    }

    // --- Endpoints de Proyectos ---

    /**
     * Endpoint: POST /projects
     * Crea un nuevo proyecto.
     */
    @PostMapping("/projects")
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO request) {
        ProjectResponseDTO response = createProjectUseCase.execute(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    // -- Endpoints de Task (tareas del proyecto)

    /**
     *
     * POST /projects/{projectId}/tasks
     *Recibe el ID del proyecto en la URL y los datos de la tarea en el Body.
     *
     */

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponseDTO> createTask(
            @PathVariable Long projectId, //obtengo el id de project desde la url
            @Valid  @RequestBody TaskRequestDTO request
    ) {
        // Llamamos al caso de uso pasando el ID y el DTO
        TaskResponseDTO response = createTaskUseCase.execute(projectId, request);
        // Creamos la URI del recurso creado (ej: /projects/1/tasks/50)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{taskId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);

    }


    // --- Endpoints de Comentarios ---

    // projectId no es utilizado, pero es parte de la URL

    @PostMapping("/projects/{projectId}/tasks/{taskId}/comments")
    public ResponseEntity<CommentResponseDTO> addCommentToTask(
            @PathVariable Long projectId, @PathVariable Long taskId,@Valid @RequestBody TaskCommentRequestDTO request) {

        CommentResponseDTO response = addCommentToTaskUseCase.execute(request, taskId);

        return ResponseEntity.status(201).body(response);
    }

    /**
     * Endpoint para buscar las tareas que cumplan con un estado determinado
     */
    @GetMapping("/tasks") // responde a un get a /projects/tasks
    public ResponseEntity<List<TaskResponseDTO>> getTasksByStatus(@RequestParam("status") TaskStatus status) {
        List<TaskResponseDTO> responseDTOs = findTaskUseCase.execute(status);
        return ResponseEntity.ok(responseDTOs); //Retorna 200 Ok con la lista
    }

    @GetMapping("/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<TaskWithCommentsResponseDTO> getTaskById(@PathVariable Long projectId,@PathVariable Long taskId, @RequestParam(value = "comments", defaultValue = "false") boolean withComments) {

        TaskWithCommentsResponseDTO response = getTaskByIdUseCase.execute(taskId, withComments);
        return ResponseEntity.ok(response);
    }

}