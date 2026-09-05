package com.notekeeper.jsf.bean;

import com.notekeeper.jsf.dao.WorkspaceDAO;
import com.notekeeper.jsf.model.Workspace;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named("workspaceBean")
@ApplicationScoped
public class WorkspaceBean {

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
            
            // Business rule: First workspace automatically becomes default
            List<Workspace> allWorkspaces = workspaceDAO.findAll();
            boolean isFirstWorkspace = (workspace.getId() == null || workspace.getId().isBlank()) 
                && allWorkspaces.isEmpty();
            
            if (isFirstWorkspace) {
                workspace.setIsDefault(true);
            }
            
            // Business rule: Only one default workspace allowed
            if (workspace.getIsDefault()) {
                clearOtherDefaults();
            }
            
            // Business rule: At least one default must exist
            if (!workspace.getIsDefault() && !hasOtherDefaults()) {
                workspace.setIsDefault(true);
                addMessage(FacesMessage.SEVERITY_INFO, "This workspace was set as default because at least one default workspace is required.");
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
    
    private boolean hasOtherDefaults() {
        try {
            List<Workspace> allWorkspaces = workspaceDAO.findAll();
            return allWorkspaces.stream()
                .anyMatch(ws -> ws.getIsDefault() && !ws.getId().equals(workspace.getId()));
        } catch (Exception ex) {
            return false;
        }
    }

    public void edit(Workspace selected) {
        System.out.println("WorkspaceBean.edit() called for: " + selected.getName());
        this.workspace = new Workspace();
        this.workspace.setId(selected.getId());
        this.workspace.setName(selected.getName());
        this.workspace.setDescription(selected.getDescription());
        this.workspace.setIcon(selected.getIcon());
        this.workspace.setOwnerName(selected.getOwnerName());
        this.workspace.setIsDefault(selected.getIsDefault());
        this.workspace.setCreatedAt(selected.getCreatedAt());
        this.editing = true;
        addMessage(FacesMessage.SEVERITY_INFO, "Editing workspace: " + selected.getName());
    }

    public void delete(Workspace selected) {
        System.out.println("WorkspaceBean.delete() called for: " + selected.getName());
        try {
            // Business rule: Must have at least one workspace
            if (workspaces.size() <= 1) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Cannot delete the last workspace. At least one workspace is required.");
                return;
            }
            
            // If deleting the default workspace, make another one default
            if (selected.getIsDefault()) {
                Workspace nextDefault = workspaces.stream()
                    .filter(ws -> !ws.getId().equals(selected.getId()))
                    .findFirst()
                    .orElse(null);
                
                if (nextDefault != null) {
                    nextDefault.setIsDefault(true);
                    workspaceDAO.update(nextDefault);
                    addMessage(FacesMessage.SEVERITY_INFO, "'" + nextDefault.getName() + "' is now the default workspace.");
                }
            }
            
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
