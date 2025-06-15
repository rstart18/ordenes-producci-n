package com.example.ordenes.domain.model;

import java.time.LocalDateTime;

/**
 * Entidad que representa una orden de producción.
 */
public class ProductionOrder {

    private Long id;
    private String description;
    private LocalDateTime createdAt;

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
