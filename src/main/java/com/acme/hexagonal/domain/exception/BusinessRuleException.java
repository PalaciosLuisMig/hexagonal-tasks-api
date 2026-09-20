package com.acme.hexagonal.domain.exception;

/**
 * Represents a violation of a business rule of the domain model.
 *
 * <p>This exception is thrown from inside the domain and the allowed transitions are
 * applied by the application layer (the use cases) which decides what to do about it.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}