package com.acme.hexagonal.application.exception;

import com.acme.hexagonal.domain.model.TaskId;

/**
 * Application-level error raised when the state of a task makes an operation invalid.
 */
public class TaskAlreadyCompletedException extends RuntimeException {

    public TaskAlreadyCompletedException(TaskId id) {
        super("Task %s is already completed".formatted(id.value()));
    }
}