package com.acme.hexagonal.adapters.in.rest.dto;

import com.acme.hexagonal.application.port.in.CreateTaskCommand;

import java.time.LocalDate;

/**
 * HTTP request payload. Its shape is a concern of the adapter, NOT of the core.
 */
public record CreateTaskRequest(String title, String description, LocalDate dueDate) {

    public CreateTaskCommand toCommand() {
        return new CreateTaskCommand(title, description, dueDate);
    }
}