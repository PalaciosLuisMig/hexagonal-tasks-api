package com.acme.hexagonal.application.service;

import com.acme.hexagonal.application.exception.TaskNotFoundException;
import com.acme.hexagonal.application.port.in.DeleteTaskUseCase;
import com.acme.hexagonal.application.port.out.TaskRepository;
import com.acme.hexagonal.domain.model.Task;
import com.acme.hexagonal.domain.model.TaskId;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Application service (use case implementation) for the "delete a task" use case.
 */
@ApplicationScoped
public class DeleteTaskService implements DeleteTaskUseCase {

    private final TaskRepository taskRepository;

    public DeleteTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void delete(TaskId id) {
        if (taskRepository.findById(id).isEmpty()) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }
}