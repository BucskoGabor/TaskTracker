package hu.bucskogabor.tasktracker.servicies;

import hu.bucskogabor.tasktracker.entities.SubTask;
import hu.bucskogabor.tasktracker.entities.Task;
import hu.bucskogabor.tasktracker.enums.TaskStatus;
import hu.bucskogabor.tasktracker.repositories.SubTaskRepository;
import hu.bucskogabor.tasktracker.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubTaskService {
    private final SubTaskRepository subTaskRepository;
    private final TaskRepository taskRepository;

    public SubTaskService(SubTaskRepository subTaskRepository, TaskRepository taskRepository) {
        this.subTaskRepository = subTaskRepository;
        this.taskRepository = taskRepository;
    }

    public SubTask completeSubTask(Long id) {
        SubTask completedSubTask = subTaskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Sub task with id " + id + " not found"));
        completedSubTask.setCompletedAt(LocalDateTime.now());
        completedSubTask.setStatus(TaskStatus.COMPLETED);
        return subTaskRepository.save(completedSubTask);
    }

    public SubTask getById(Long id) {
        return subTaskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("SubTask with id " + id + " not found"));
    }

    public List<SubTask> getAll() {
        return subTaskRepository.findAll();
    }

    public SubTask create(SubTask subTask) {
        return subTaskRepository.save(subTask);
    }

    public SubTask update(Long id, SubTask updateSubTaskBody) {
        SubTask subTask = subTaskRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("SubTask with id " + id + " not found"));
        subTask.setName(updateSubTaskBody.getName());
        subTask.setDescription(updateSubTaskBody.getDescription());
        subTask.setStatus(updateSubTaskBody.getStatus());
        subTask.setMainTask(updateSubTaskBody.getMainTask());
        if (!updateSubTaskBody.getStatus().equals(TaskStatus.COMPLETED)) {
            subTask.setCompletedAt(null);
        }
        return subTaskRepository.save(subTask);
    }

    public void delete(Long id) {
        SubTask subTask = subTaskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SubTask with id " + id + " not found"));
        
        Task mainTask = subTask.getMainTask();
        if (mainTask != null && mainTask.getSubTasks() != null) {
            mainTask.getSubTasks().remove(subTask);
            taskRepository.save(mainTask);
        }
        
        subTaskRepository.delete(subTask);
    }
}
