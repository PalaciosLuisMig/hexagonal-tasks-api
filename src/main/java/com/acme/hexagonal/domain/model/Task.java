package com.acme.hexagonal.domain.model;

import com.acme.hexagonal.domain.exception.BusinessRuleException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Core domain entity.
 *
 * <p>{@code Task} is a plain Java class with no reference to Quarkus, JPA or any other
 * framework. All the business rules that the rest of the system must respect are
 * encapsulated here (state machine, invariants, defensive copies).
 */
public class Task {

    private final TaskId id;
    private final String title;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDate dueDate;
    private TaskStatus status;

    private Task(TaskId id, String title, String description, LocalDate dueDate, TaskStatus status,
                 LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * Factory method. A task always starts OPEN with a newly generated {@link TaskId}.
     * Business invariants (a non-blank title) are verified here, at construction time.
     */
    public static Task create(String title, String description, LocalDate dueDate) {
        if (title == null || title.isBlank()) {
            throw new BusinessRuleException("title must not be null or blank");
        }
        TaskId id = TaskId.generate();
        return new Task(id, title.trim(), description, dueDate, TaskStatus.OPEN, LocalDateTime.now());
    }

    /**
     * Restores an already persisted task (used by the persistence adapters).
     */
    public static Task restore(TaskId id, String title, String description, LocalDate dueDate,
                               TaskStatus status, LocalDateTime createdAt) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(status, "status must not be null");
        return new Task(id, title, description, dueDate, status, createdAt);
    }

    public void start() {
        if (status == TaskStatus.DONE) {
            throw new BusinessRuleException("a completed task cannot be started again");
        }
        this.status = TaskStatus.IN_PROGRESS;
    }

    /**
     * Business rule #1: an already completed task cannot be completed again.
     */
    public void complete() {
        if (status == TaskStatus.DONE) {
            throw new BusinessRuleException("a completed task cannot be completed again");
        }
        this.status = TaskStatus.DONE;
    }

    public TaskId id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public TaskStatus status() {
        return status;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDate dueDate() {
        return dueDate;
    }
}