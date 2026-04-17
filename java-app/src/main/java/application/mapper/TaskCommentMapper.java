package application.mapper;

//import application.dto.request.TaskCommentRequestDTO;
import application.dto.response.CommentResponseDTO;
import domain.model.TaskComment;
import org.springframework.stereotype.Component;

@Component
public class TaskCommentMapper {

    public CommentResponseDTO toResponseDTO(TaskComment taskComment){
        if (taskComment == null) {
            return null;
        }
        return new CommentResponseDTO(taskComment.getId(),
                taskComment.getTask(),
                taskComment.getText(),
                taskComment.getAuthor(),
                taskComment.getCreatedAt());
    }
}
