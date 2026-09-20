package com.acme.hexagonal.application.port.in;

import com.acme.hexagonal.domain.model.TaskId;

/**
 * Driving port (input side) to delete tasks.
 */
public interface DeleteTaskUseCase {

    /**
     * @param id identifier of the task to delete
     * @throws com.acme.hexagonal.application.exception.TaskNotFoundException if the task does not exist
     */
    void delete(TaskId id);
}