package com.acme.hexagonal.adapters.in.rest;

import com.acme.hexagonal.application.exception.TaskAlreadyCompletedException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps a {@link TaskAlreadyCompletedException} to {@code 409 conflict}.
 */
@Provider
public class TaskAlreadyCompletedExceptionMapper implements ExceptionMapper<TaskAlreadyCompletedException> {

    @Override
    public Response toResponse(TaskAlreadyCompletedException exception) {
        return ExceptionMapperSupport.error(Response.Status.CONFLICT, "conflict", exception.getMessage());
    }
}