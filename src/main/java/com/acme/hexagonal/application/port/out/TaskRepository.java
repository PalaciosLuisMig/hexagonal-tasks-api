package com.acme.hexagonal.application.port.out;

import com.acme.hexagonal.domain.model.Task;
import com.acme.hexagonal.domain.model.TaskId;

import java.util.List;
import java.util.Optional;

/**
 * Driven port (output / "right" side of the hexagon).
 *
 * <p>Defines how the core persists and loads {@link Task} aggregates. Nobody in the core
 * knows about JPA, H2, or any concrete storage: only this interface. That is what makes
 * the persistence an interchangeable side-adapter.
 */
public interface TaskRepository {

    Optional<Task> findById(TaskId id);

    List<Task> findAll();

    /**
     * Saves (insert or update) a task and returns the persisted state.
     */
    Task save(Task task);

    void deleteById(TaskId id);
}