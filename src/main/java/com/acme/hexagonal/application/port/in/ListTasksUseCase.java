package com.acme.hexagonal.application.port.in;

import com.acme.hexagonal.domain.model.Task;

import java.util.List;

/**
 * Driving port (input side) to read tasks.
 */
public interface ListTasksUseCase {

    List<Task> listAll();
}