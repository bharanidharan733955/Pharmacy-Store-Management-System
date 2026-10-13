package com.pharmacy.web.controller;

import com.pharmacy.web.dto.AuthRequest;
import com.pharmacy.web.dto.AuthResponse;
import com.pharmacy.web.security.JwtTokenProvider;
import com.pharmacy.web.service.PharmacyWebService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PharmacyWebService pharmacyWebService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(PharmacyWebService pharmacyWebService, JwtTokenProvider jwtTokenProvider) {
        this.pharmacyWebService = pharmacyWebService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        PharmacyWebService.UserAccount account = pharmacyWebService.authenticate(request.getUsername(), request.getPassword());
        if (account == null) {
            throw new BadCredentialsException("Invalid username or password.");
        }

        String token = jwtTokenProvider.generateToken(account.getUsername(), account.getRole(), account.getFullName());
        AuthResponse response = new AuthResponse(
                token,
                account.getUsername(),
                account.getRole(),
                account.getFullName(),
                account.getPermissions()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        PharmacyWebService.UserAccount account = pharmacyWebService.getUserByUsername(username);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        AuthResponse response = new AuthResponse(
                null,
                account.getUsername(),
                account.getRole(),
                account.getFullName(),
                account.getPermissions()
        );
        return ResponseEntity.ok(response);
    }
}
