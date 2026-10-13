package com.pharmacy.core.exception;

/**
 * Base exception for domain and business-rule violations in the Pharmacy Store Management System.
 */
public class BusinessRuleException extends RuntimeException {

    private final String errorCode;
    private final int status;

    public BusinessRuleException(String message) {
        this("BUSINESS_RULE_VIOLATION", 409, message);
    }

    public BusinessRuleException(String errorCode, String message) {
        this(errorCode, 409, message);
    }

    public BusinessRuleException(String errorCode, int status, String message) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatus() {
        return status;
    }
}
