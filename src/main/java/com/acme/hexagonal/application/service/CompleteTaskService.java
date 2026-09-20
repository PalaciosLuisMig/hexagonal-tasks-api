package com.acme.hexagonal.application.service;

import com.acme.hexagonal.application.exception.TaskAlreadyCompletedException;
import com.acme.hexagonal.application.exception.TaskNotFoundException;
import com.acme.hexagonal.application.port.in.CompleteTaskUseCase;
import com.acme.hexagonal.application.port.out.TaskRepository;
import com.acme.hexagonal.domain.model.Task;
import com.acme.hexagonal.domain.model.TaskId;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Application service (use case implementation) for the "complete a task" use case.
 *
 * <p>Notice the division of responsibilities:
 * <ul>
 *   <li>the <b>domain</b> decides the state machine ({@code Task#complete()} throws if the
 *       task is already done);</li>
 *   <li>the <b>use case</b> decides what that means for the application (translate the
 *       domain rule into an application-level error) and orchestrates persistence.</li>
 * </ul>
 */
@ApplicationScoped
public class CompleteTaskService implements CompleteTaskUseCase {

    private final TaskRepository taskRepository;

    public CompleteTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task complete(TaskId id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        try {
            task.complete();
        } catch (RuntimeException e) {
            // A domain {@link BusinessRuleException} becomes an application-level error
            throw new TaskAlreadyCompletedException(id);
        }
        return taskRepository.save(task);
    }
}