package infrastructure.persistence.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_comments")
public class TaskCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //autogenerado incremental
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;

    @Column(nullable = false, length = 1000)
    private String text;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public TaskCommentEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TaskEntity getTask() { return task; }
    public void setTask(TaskEntity task) { this.task = task; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
