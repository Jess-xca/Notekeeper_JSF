package com.notekeeper.jsf.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tags")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Tag name is required.")
    @Size(min = 2, max = 50, message = "Tag name must be between 2 and 50 characters.")
    @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9\\s-]*$", message = "Tag name may contain letters, numbers, spaces, and hyphens only.")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @NotBlank(message = "Tag color is required.")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Color must be a hex value such as #2563EB.")
    @Column(nullable = false, length = 7)
    private String color = "#2563EB";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Tag() {
    }

    public Tag(String name, String color) {
        this.name = name;
        this.color = color;
    }

    @PrePersist
    protected void onCreate() {
        if (id == null || id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
