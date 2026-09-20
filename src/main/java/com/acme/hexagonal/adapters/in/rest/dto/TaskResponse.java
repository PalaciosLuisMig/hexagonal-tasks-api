package com.acme.hexagonal.adapters.in.rest.dto;

import com.acme.hexagonal.domain.model.Task;
import com.acme.hexagonal.domain.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * HTTP response payload built from a {@link Task}.
 */
public record TaskResponse(
        String id,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDate dueDate) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.id().value().toString(),
                task.title(),
                task.description(),
                task.status(),
                task.createdAt(),
                task.dueDate()
        );
    }
}