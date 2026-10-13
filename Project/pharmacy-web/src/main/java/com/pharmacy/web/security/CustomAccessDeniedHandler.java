package com.pharmacy.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.web.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Custom AccessDeniedHandler ensuring 403 Forbidden responses match the unified ErrorResponse format.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        String corrId = (String) request.getAttribute("X-Correlation-Id");
        if (corrId == null || corrId.isBlank()) {
            corrId = request.getHeader("X-Correlation-Id");
        }
        if (corrId == null || corrId.isBlank()) {
            corrId = UUID.randomUUID().toString();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "FORBIDDEN",
                "You do not possess the required role permissions for this operation.",
                request.getRequestURI(),
                corrId
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("X-Correlation-Id", corrId);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
