package com.pharmacy.web.dto;

import java.util.Set;

public class AuthResponse {

    private String token;
    private String username;
    private String role;
    private String fullName;
    private Set<String> permissions;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, String role, String fullName, Set<String> permissions) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
        this.permissions = permissions;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
