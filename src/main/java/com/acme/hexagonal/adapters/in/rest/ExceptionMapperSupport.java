package com.acme.hexagonal.adapters.in.rest;

import com.acme.hexagonal.adapters.in.rest.dto.ErrorResponse;

import jakarta.ws.rs.core.Response;

/**
 * Shared helper used by the exception mappers to build the uniform error response.
 */
final class ExceptionMapperSupport {

    private ExceptionMapperSupport() {
    }

    static Response error(Response.Status status, String code, String message) {
        return Response.status(status).entity(new ErrorResponse(code, message)).build();
    }
}