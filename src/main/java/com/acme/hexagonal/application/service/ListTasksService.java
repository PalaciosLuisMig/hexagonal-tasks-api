package com.acme.hexagonal.application.service;

import com.acme.hexagonal.application.port.in.ListTasksUseCase;
import com.acme.hexagonal.application.port.out.TaskRepository;
import com.acme.hexagonal.domain.model.Task;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Application service (use case implementation) for reading tasks.
 */
@ApplicationScoped
public class ListTasksService implements ListTasksUseCase {

    private final TaskRepository taskRepository;

    public ListTasksService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> listAll() {
        return taskRepository.findAll();
    }
}