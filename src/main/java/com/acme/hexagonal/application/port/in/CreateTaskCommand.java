package com.acme.hexagonal.application.port.in;

import com.acme.hexagonal.domain.model.Task;

import java.time.LocalDate;

/**
 * Input data for {@link CreateTaskUseCase}.
 *
 * <p>An immutable transport object that travels from the adapter into the core. It never
 * leaks domain entities outwards, nor infrastructure types inwards.
 *
 * @param title       task title (mandatory, cannot be blank)
 * @param description optional long description
 * @param dueDate     optional due date
 */
public record CreateTaskCommand(String title, String description, LocalDate dueDate) {
}