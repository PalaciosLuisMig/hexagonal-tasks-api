package com.acme.hexagonal.application.service;

import com.acme.hexagonal.application.port.in.CreateTaskCommand;
import com.acme.hexagonal.application.port.in.CreateTaskUseCase;
import com.acme.hexagonal.application.port.out.TaskRepository;
import com.acme.hexagonal.domain.model.Task;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Application service (use case implementation).
 *
 * <p>It only depends on the {@link TaskRepository} port and on the domain. It knows
 * nothing about HTTP, JSON or JPA. Business orchestration happens here:
 * validate-feasible -> delegate to domain -> persist result.
 */
@ApplicationScoped
public class CreateTaskService implements CreateTaskUseCase {

    private final TaskRepository taskRepository;

    public CreateTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task create(CreateTaskCommand command) {
        // The invariant checks (non-blank title, allowed state machine) live in the domain
        // factory method with the least risk of being bypassed.
        Task task = Task.create(command.title(), command.description(), command.dueDate());
        return taskRepository.save(task);
    }
}