package com.acme.hexagonal.application.exception;

import com.acme.hexagonal.domain.model.TaskId;

/**
 * Application-level error: the requested {@link TaskId} does not exist in the system.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(TaskId id) {
        super("Task %s not found".formatted(id.value()));
    }
}