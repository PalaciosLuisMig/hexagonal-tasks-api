package com.acme.hexagonal.adapters.in.rest;

import com.acme.hexagonal.application.exception.TaskNotFoundException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps a {@link TaskNotFoundException} to {@code 404 not_found}.
 */
@Provider
public class TaskNotFoundExceptionMapper implements ExceptionMapper<TaskNotFoundException> {

    @Override
    public Response toResponse(TaskNotFoundException exception) {
        return ExceptionMapperSupport.error(Response.Status.NOT_FOUND, "not_found", exception.getMessage());
    }
}