package hu.bucskogabor.tasktracker.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import hu.bucskogabor.tasktracker.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String description;

    private LocalDateTime startDate = LocalDateTime.now();

    private LocalDateTime deadline;

    private LocalDateTime completedAt;

    private TaskStatus status = TaskStatus.TODO;

    @OneToMany(mappedBy = "mainTask", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference
    private List<SubTask> subTasks;
}
