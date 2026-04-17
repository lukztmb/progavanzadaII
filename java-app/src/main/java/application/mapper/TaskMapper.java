package application.mapper;

import application.dto.request.TaskRequestDTO;
import application.dto.response.TaskResponseDTO;
import domain.model.Project;
import domain.model.Task;
//import domain.repository.TaskRepository;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toDomain(TaskRequestDTO taskDto, Project project) {
        if(taskDto == null){
            return null;
        }
        return Task.create(taskDto.id(),
                taskDto.title(),
                project,
                taskDto.estimatedHours(),
                taskDto.assignee(),
                taskDto.status(),
                taskDto.createdAt(),
                taskDto.finishedAt());
    }


    public TaskResponseDTO toResponseDTO(Task task) {
        // Tu validación es correcta
        if (task == null) {
            return null;
        }

        // Llenamos el DTO con los datos del objeto de dominio
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getProyect(),
                task.getEstimatedHours(),
                task.getAssignee(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getFinishedAt()
        );



    }


}
