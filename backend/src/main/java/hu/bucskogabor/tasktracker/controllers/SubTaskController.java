package hu.bucskogabor.tasktracker.controllers;

import hu.bucskogabor.tasktracker.entities.SubTask;
import hu.bucskogabor.tasktracker.servicies.SubTaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasktracker/subtasks")
public class SubTaskController {
    private final SubTaskService subTaskService;

    public SubTaskController(SubTaskService subTaskService) {
        this.subTaskService = subTaskService;
    }

    @PostMapping("/completeSubTask/{id}")
    public ResponseEntity<SubTask> completeSubTask(@PathVariable Long id) {
        return new ResponseEntity<>(subTaskService.completeSubTask(id), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SubTask>> getAllSubTasks() {
        return new ResponseEntity<>(subTaskService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubTask> getSubTaskById(@PathVariable Long id) {
        return new ResponseEntity<>(subTaskService.getById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubTask> updateSubTask(@PathVariable Long id, @RequestBody SubTask subTask) {
        return new ResponseEntity<>(subTaskService.update(id, subTask), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SubTask> createSubTask(@RequestBody SubTask subTask) {
        return new ResponseEntity<>(subTaskService.create(subTask), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubTask(@PathVariable Long id) {
        subTaskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
