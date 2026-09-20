package com.acme.hexagonal.adapters.in.rest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Fallback mapper: any exception not matched by a more specific mapper becomes a
 * {@code 500 internal_error}. RESTEasy picks the closest {@link ExceptionMapper} by
 * exception type, so this one is only consulted as a last resort.
 */
@Provider
public class UnhandledExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        return ExceptionMapperSupport.error(Response.Status.INTERNAL_SERVER_ERROR, "internal_error", exception.getMessage());
    }
}