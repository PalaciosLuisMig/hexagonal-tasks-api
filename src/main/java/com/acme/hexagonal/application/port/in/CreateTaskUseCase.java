package com.acme.hexagonal.application.port.in;

import com.acme.hexagonal.domain.model.Task;

/**
 * Driving port (input / "left" side of the hexagon).
 *
 * <p>Declares what the rest of the world can DO with the application. Adapters (e.g. REST)
 * depend on this interface, never on a concrete service.
 */
public interface CreateTaskUseCase {

    /**
     * @param command validated input coming from the adapter
     * @return the created domain entity
     */
    Task create(CreateTaskCommand command);
}