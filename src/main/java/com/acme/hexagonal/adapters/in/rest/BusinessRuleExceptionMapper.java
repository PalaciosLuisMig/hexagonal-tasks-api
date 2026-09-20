package com.acme.hexagonal.adapters.in.rest;

import com.acme.hexagonal.domain.exception.BusinessRuleException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps a {@link BusinessRuleException} to {@code 400 bad_request}.
 */
@Provider
public class BusinessRuleExceptionMapper implements ExceptionMapper<BusinessRuleException> {

    @Override
    public Response toResponse(BusinessRuleException exception) {
        return ExceptionMapperSupport.error(Response.Status.BAD_REQUEST, "bad_request", exception.getMessage());
    }
}