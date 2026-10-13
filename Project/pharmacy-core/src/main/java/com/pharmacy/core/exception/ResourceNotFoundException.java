package com.pharmacy.core.exception;

/**
 * Thrown when an expected entity (medicine, supplier, batch, or invoice) cannot be found.
 */
public class ResourceNotFoundException extends BusinessRuleException {

    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", 404, message);
    }

    public ResourceNotFoundException(String resourceName, String identifier) {
        super("RESOURCE_NOT_FOUND", 404, String.format("%s with identifier '%s' was not found.", resourceName, identifier));
    }
}
