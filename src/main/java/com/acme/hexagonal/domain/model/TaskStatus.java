package com.acme.hexagonal.domain.model;

/**
 * Status of a {@link Task}.
 *
 * <p>The possible states and the allowed {@code TaskStatus} transitions are part of the
 * business rules of the domain and therefore live in the domain model.
 */
public enum TaskStatus {
    OPEN,
    IN_PROGRESS,
    DONE
}