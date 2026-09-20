package com.acme.hexagonal.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object that identifies a {@link Task} in an unambiguous way.
 *
 * <p>The identifier is generated inside the domain (and not by the persistence layer) so that
 * {@code TaskId} does not depend on any infrastructure or database detail.
 *
 * @param value the underlying UUID value
 */
public record TaskId(UUID value) {

    public TaskId {
        Objects.requireNonNull(value, "TaskId must not be null");
    }

    public static TaskId generate() {
        return new TaskId(UUID.randomUUID());
    }

    public static TaskId of(String value) {
        return new TaskId(UUID.fromString(value));
    }
}