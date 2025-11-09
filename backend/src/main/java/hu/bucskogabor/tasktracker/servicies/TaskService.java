package hu.bucskogabor.tasktracker.servicies;

import hu.bucskogabor.tasktracker.entities.Task;
import hu.bucskogabor.tasktracker.enums.TaskStatus;
import hu.bucskogabor.tasktracker.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task completeTask(Long id) {
        Task completedTask = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
        boolean allSubTasksCompleted = completedTask.getSubTasks().stream().allMatch(subTask -> subTask.getStatus() == TaskStatus.COMPLETED);
        if (completedTask.getSubTasks().isEmpty() || allSubTasksCompleted) {
            completedTask.setCompletedAt(LocalDateTime.now());
            completedTask.setStatus(TaskStatus.COMPLETED);
            return taskRepository.save(completedTask);
        } else {
            throw new IllegalStateException("Cannot complete task with id " + id + " because not all subtasks are completed.");
        }
    }

    public Task getById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public Task create(Task task) {
        return taskRepository.save(task);
    }

    public Task update(Long id, Task updateTaskBody) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
        task.setName(updateTaskBody.getName());
        task.setDescription(updateTaskBody.getDescription());
        task.setDeadline(updateTaskBody.getDeadline());
        task.setStatus(updateTaskBody.getStatus());
        if (!updateTaskBody.getStatus().equals(TaskStatus.COMPLETED)) {
            task.setCompletedAt(null);
        }
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
