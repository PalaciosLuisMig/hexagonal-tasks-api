package com.acme.hexagonal.application.port.in;

import com.acme.hexagonal.domain.model.Task;
import com.acme.hexagonal.domain.model.TaskId;

/**
 * Driving port (input side) to complete tasks.
 */
public interface CompleteTaskUseCase {

    /**
     * @param id identifier of the task to complete
     * @return the domain entity in its new {@code DONE} state
     * @throws com.acme.hexagonal.application.exception.TaskNotFoundException if the task does not exist
     * @throws com.acme.hexagonal.application.exception.TaskAlreadyCompletedException if already completed
     */
    Task complete(TaskId id);
}