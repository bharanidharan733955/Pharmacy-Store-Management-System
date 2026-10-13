package com.pharmacy.web.exception;

import com.pharmacy.core.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Enterprise Global Exception Handler implementing centralized REST error handling.
 * Ensures consistent JSON response structure and protects against information leakage.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private String getCorrelationId(HttpServletRequest request) {
        String corrId = (String) request.getAttribute("X-Correlation-Id");
        if (corrId == null || corrId.isBlank()) {
            corrId = request.getHeader("X-Correlation-Id");
        }
        if (corrId == null || corrId.isBlank()) {
            corrId = UUID.randomUUID().toString();
        }
        return corrId;
    }

    // 1. Validation Failures (HTTP 400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String corrId = getCorrelationId(request);
        log.warn("[{}] Validation error on path {}: {}", corrId, request.getRequestURI(), ex.getMessage());

        BindingResult result = ex.getBindingResult();
        List<FieldErrorDetail> fieldErrors = new ArrayList<>();
        result.getFieldErrors().forEach(fe -> {
            fieldErrors.add(new FieldErrorDetail(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()));
        });

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "VALIDATION_FAILED",
                "Validation failed for one or more request fields.",
                request.getRequestURI(),
                corrId
        );
        response.setFieldErrors(fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String corrId = getCorrelationId(request);
        log.warn("[{}] Constraint violation on path {}: {}", corrId, request.getRequestURI(), ex.getMessage());

        List<FieldErrorDetail> fieldErrors = new ArrayList<>();
        ex.getConstraintViolations().forEach(cv -> {
            fieldErrors.add(new FieldErrorDetail(cv.getPropertyPath().toString(), cv.getMessage(), cv.getInvalidValue()));
        });

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "VALIDATION_FAILED",
                "Validation constraints were violated.",
                request.getRequestURI(),
                corrId
        );
        response.setFieldErrors(fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 2. Malformed Request Bodies or Invalid Parameters (HTTP 400)
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
        String corrId = getCorrelationId(request);
        log.warn("[{}] Bad request on path {}: {}", corrId, request.getRequestURI(), ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "BAD_REQUEST",
                "Malformed request body, missing parameter, or invalid argument format.",
                request.getRequestURI(),
                corrId
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 3. Unauthenticated Requests (HTTP 401)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        String corrId = getCorrelationId(request);
        log.warn("[{}] Authentication failure on path {}: {}", corrId, request.getRequestURI(), ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "UNAUTHORIZED",
                "Full authentication is required to access this resource.",
                request.getRequestURI(),
                corrId
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 4. Forbidden Role Access (HTTP 403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        String corrId = getCorrelationId(request);
        log.warn("[{}] Access denied on path {}: {}", corrId, request.getRequestURI(), ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "FORBIDDEN",
                "You do not possess the required role permissions for this operation.",
                request.getRequestURI(),
                corrId
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 5. Missing Entities (HTTP 404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        String corrId = getCorrelationId(request);
        log.warn("[{}] Resource not found on path {}: {}", corrId, request.getRequestURI(), ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getErrorCode() != null ? ex.getErrorCode() : "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI(),
                corrId
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 6. Business-Rule 1: Expired Medicine Sale (HTTP 409)
    @ExceptionHandler(ExpiredBatchException.class)
    public ResponseEntity<ErrorResponse> handleExpiredBatch(ExpiredBatchException ex, HttpServletRequest request) {
        String corrId = getCorrelationId(request);
        log.warn("[{}] Expired batch sale attempt rejected on path {}: {}", corrId, request.getRequestURI(), ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "EXPIRED_BATCH",
                ex.getMessage(),
                request.getRequestURI(),
                corrId
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 7. Business-Rule 2: Scheduled Medicine Prescription Reference Required (HTTP 409)
    @ExceptionHandler(InvalidPrescriptionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPrescription(InvalidPrescriptionException ex, HttpServletRequest request) {
        String corrId = getCorrelationId(request);
        log.warn("[{}] Prescription validation failed on path {}: {}", corrId, request.getRequestURI(), ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "PRESCRIPTION_REFERENCE_REQUIRED",
                ex.getMessage(),
                request.getRequestURI(),
                corrId
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 8. Insufficient Stock (HTTP 409)
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        String corrId = getCorrelationId(request);
        log.warn("[{}] Insufficient stock error on path {}: {}", corrId, request.getRequestURI(), ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "INSUFFICIENT_STOCK",
                ex.getMessage(),
                request.getRequestURI(),
                corrId
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 9. Other Business Rule Violations (HTTP 409)
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleGenericBusinessRule(BusinessRuleException ex, HttpServletRequest request) {
        String corrId = getCorrelationId(request);
        log.warn("[{}] Business rule violation on path {}: {}", corrId, request.getRequestURI(), ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                ex.getStatus() > 0 ? ex.getStatus() : HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getErrorCode() != null ? ex.getErrorCode() : "BUSINESS_RULE_VIOLATION",
                ex.getMessage(),
                request.getRequestURI(),
                corrId
        );
        return ResponseEntity.status(ex.getStatus() > 0 ? ex.getStatus() : HttpStatus.CONFLICT.value()).body(response);
    }

    // 10. Unexpected Server Errors (HTTP 500) - Never expose stack traces or DB details
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        String corrId = getCorrelationId(request);
        log.error("[{}] Internal server error on path {}:", corrId, request.getRequestURI(), ex);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected internal error occurred. Please contact the pharmacy system administrator with correlation ID: " + corrId,
                request.getRequestURI(),
                corrId
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
