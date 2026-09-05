package com.notekeeper.jsf.bean;

import com.notekeeper.jsf.dao.WorkspaceDAO;
import com.notekeeper.jsf.model.Workspace;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("workspaceBean")
@ViewScoped
public class WorkspaceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final WorkspaceDAO workspaceDAO = new WorkspaceDAO();
    private Workspace workspace = new Workspace();
    private List<Workspace> workspaces = new ArrayList<>();
    private boolean editing;

    @PostConstruct
    public void init() {
        load();
    }

    public void load() {
        workspaces = workspaceDAO.findAll();
    }

    public void save() {
        try {
            if (workspace.getIsDefault() == null) {
                workspace.setIsDefault(false);
            }
            
            // Business rule: Only one default workspace allowed
            if (workspace.getIsDefault()) {
                clearOtherDefaults();
            }
            
            if (workspace.getId() == null || workspace.getId().isBlank()) {
                workspaceDAO.save(workspace);
                addMessage(FacesMessage.SEVERITY_INFO, "Workspace created successfully.");
            } else {
                workspaceDAO.update(workspace);
                addMessage(FacesMessage.SEVERITY_INFO, "Workspace updated successfully.");
            }
            reset();
            load();
        } catch (Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Unable to save workspace: " + ex.getMessage());
        }
    }
    
    private void clearOtherDefaults() {
        try {
            List<Workspace> allWorkspaces = workspaceDAO.findAll();
            for (Workspace ws : allWorkspaces) {
                if (ws.getIsDefault() && !ws.getId().equals(workspace.getId())) {
                    ws.setIsDefault(false);
                    workspaceDAO.update(ws);
                }
            }
        } catch (Exception ex) {
            addMessage(FacesMessage.SEVERITY_WARN, "Warning: Could not clear other default workspaces.");
        }
    }

    public void edit(Workspace selected) {
        this.workspace = new Workspace();
        this.workspace.setId(selected.getId());
        this.workspace.setName(selected.getName());
        this.workspace.setDescription(selected.getDescription());
        this.workspace.setIcon(selected.getIcon());
        this.workspace.setOwnerName(selected.getOwnerName());
        this.workspace.setIsDefault(selected.getIsDefault());
        this.workspace.setCreatedAt(selected.getCreatedAt());
        this.editing = true;
    }

    public void delete(Workspace selected) {
        try {
            workspaceDAO.delete(selected.getId());
            addMessage(FacesMessage.SEVERITY_INFO, "Workspace deleted successfully.");
            if (workspace.getId() != null && workspace.getId().equals(selected.getId())) {
                reset();
            }
            load();
        } catch (Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Unable to delete workspace: " + ex.getMessage());
        }
    }

    public void reset() {
        workspace = new Workspace();
        editing = false;
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, text));
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public List<Workspace> getWorkspaces() {
        return workspaces;
    }

    public boolean isEditing() {
        return editing;
    }
}
