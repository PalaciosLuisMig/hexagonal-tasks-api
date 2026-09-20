package com.acme.hexagonal.adapters.in.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps an {@link IllegalArgumentException} to {@code 400 bad_request}.
 */
@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return ExceptionMapperSupport.error(Response.Status.BAD_REQUEST, "bad_request", exception.getMessage());
    }
}