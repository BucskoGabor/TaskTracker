package hu.bucskogabor.tasktracker.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import hu.bucskogabor.tasktracker.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "subtasks")
@Data
public class SubTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String description;

    private LocalDateTime completedAt;

    private TaskStatus status = TaskStatus.TODO;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mainTask_id")
    @NotNull
    @JsonBackReference
    private Task mainTask;
}
