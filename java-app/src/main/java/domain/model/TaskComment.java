package domain.model;

import jakarta.validation.ValidationException;

import java.time.LocalDateTime;

public class TaskComment {
    private Long id;
    private Task task;
    private String text;
    private String author;
    private LocalDateTime createdAt;

    private TaskComment(Task task, String text, String author, LocalDateTime createdAt) {
        this.id = null;
        this.task = task;
        this.text = text;
        this.author = author;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Factory Method para crear un nuevo Comentario.
     *
     * @param task La tarea a la que pertenece el comentario (obligatoria)
     * @param text El contenido del comentario (obligatorio)
     * @param author El autor del comentario (obligatorio)
     * @param createdAt Un Clock para manejar el tiempo (para 'createdAt')
     * @return una instancia de TaskComment válida.
     * @throws ValidationException si fallan las validaciones de campos.
     */
    public static TaskComment create(Task task, String text, String author, LocalDateTime createdAt) {
        if (task == null) {
            throw new ValidationException("Comment should be associated to a Task.");
        }
        if (text == null || text.isBlank()) {
            throw new ValidationException("Comment should have text.");
        }
        if (author == null || author.isBlank()) {
            throw new ValidationException("Comment should have an author.");
        }

        TaskComment comment = new TaskComment(task, text, author, createdAt);

        return comment;
    }



}
