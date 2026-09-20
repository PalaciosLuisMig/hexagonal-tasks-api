package com.acme.hexagonal.adapters.in.rest.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Uniform error body returned by the API.
 *
 * <p>Annotated with {@link RegisterForReflection} because entities returned from an
 * {@link jakarta.ws.rs.ext.ExceptionMapper} are not auto-registered by Quarkus in native
 * mode (unlike the entity types of resource method signatures). Without it, Jackson cannot
 * serialize this record in a native executable and the request falls back to a 500.
 */
@RegisterForReflection
public record ErrorResponse(String code, String message) {
}