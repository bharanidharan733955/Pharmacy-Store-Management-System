package com.pharmacy.core.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract BaseEntity superclass for all domain entities in the Pharmacy Store Management System.
 * Provides standard tracking properties (id, createdAt, updatedAt, active status)
 * and auditing lifecycle operations.
 */
public abstract class BaseEntity {

    private final String id;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    /**
     * Default Constructor: Auto-generates a unique UUID and sets timestamps.
     */
    public BaseEntity() {
        this(UUID.randomUUID().toString());
    }

    /**
     * Constructor with explicit ID.
     *
     * @param id Unique entity ID
     */
    public BaseEntity(String id) {
        this.id = (id != null && !id.trim().isEmpty()) ? id : UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }

    /**
     * Updates the last modification timestamp to current system time.
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Soft-deactivates this entity.
     */
    public void deactivate() {
        this.active = false;
        touch();
    }

    /**
     * Re-activates this entity.
     */
    public void activate() {
        this.active = true;
        touch();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        touch();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", active=" + active +
                '}';
    }
}
