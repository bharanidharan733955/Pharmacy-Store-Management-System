package com.pharmacy.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.web.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Custom AuthenticationEntryPoint ensuring 401 Unauthorized responses match the unified ErrorResponse format.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        String corrId = (String) request.getAttribute("X-Correlation-Id");
        if (corrId == null || corrId.isBlank()) {
            corrId = request.getHeader("X-Correlation-Id");
        }
        if (corrId == null || corrId.isBlank()) {
            corrId = UUID.randomUUID().toString();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "UNAUTHORIZED",
                "Authentication credentials are missing, invalid, or expired.",
                request.getRequestURI(),
                corrId
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("X-Correlation-Id", corrId);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
