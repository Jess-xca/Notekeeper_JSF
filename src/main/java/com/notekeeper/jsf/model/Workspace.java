package com.notekeeper.jsf.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jsf_workspaces")
public class Workspace implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Workspace name is required.")
    @Size(min = 3, max = 100, message = "Workspace name must be between 3 and 100 characters.")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    @Column(length = 500)
    private String description;

    @Size(max = 10, message = "Icon cannot exceed 10 characters.")
    @Column(length = 10)
    private String icon;

    @NotBlank(message = "Owner name is required.")
    @Size(min = 2, max = 80, message = "Owner name must be between 2 and 80 characters.")
    @Column(nullable = false, length = 80)
    private String ownerName;

    @Column(nullable = false)
    private Boolean isDefault = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Workspace() {
    }

    public Workspace(String name, String description, String icon, String ownerName, Boolean isDefault) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.ownerName = ownerName;
        this.isDefault = isDefault;
    }

    @PrePersist
    protected void onCreate() {
        if (id == null || id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (isDefault == null) {
            this.isDefault = false;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
