package com.pharmacy.core.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract User class representing the root of the Role Hierarchy in the Pharmacy system.
 * Extends BaseEntity to inherit auditing (id, createdAt, active status).
 */
public abstract class User extends BaseEntity {

    private String username;
    private String email;
    private String fullName;
    private UserRole role;
    private Set<String> permissions;

    public User(String id, String username, String email, String fullName, UserRole role) {
        super(id);
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (role == null) {
            throw new IllegalArgumentException("UserRole cannot be null.");
        }
        this.username = username;
        this.email = email != null ? email : "N/A";
        this.fullName = fullName != null ? fullName : username;
        this.role = role;
        this.permissions = new HashSet<>();
    }

    /**
     * Abstract contract requiring subclasses to define role-specific descriptions.
     */
    public abstract String getRoleDescription();

    /**
     * Abstract contract requiring subclasses to enforce role action boundaries.
     *
     * @param actionCode Permission or action identifier string
     * @return true if action is allowed for this role, false otherwise
     */
    public abstract boolean canPerformAction(String actionCode);

    // Permission Management
    public void grantPermission(String permission) {
        if (permission != null && !permission.trim().isEmpty()) {
            this.permissions.add(permission.toUpperCase());
            touch();
        }
    }

    public boolean hasPermission(String permission) {
        if (permission == null) return false;
        return permissions.contains(permission.toUpperCase());
    }

    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        touch();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        touch();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        touch();
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
        touch();
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + getId() + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", active=" + isActive() +
                '}';
    }
}
