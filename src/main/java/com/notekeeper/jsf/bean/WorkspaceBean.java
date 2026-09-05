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
        if (workspace == null) {
            workspace = new Workspace();
        }
        fixMultipleDefaults(); // Fix corrupted data
        load();
    }
    
    private void fixMultipleDefaults() {
        try {
            List<Workspace> allWorkspaces = workspaceDAO.findAll();
            List<Workspace> defaultWorkspaces = allWorkspaces.stream()
                .filter(ws -> ws.getIsDefault())
                .toList();
            
            if (defaultWorkspaces.size() > 1) {
                System.out.println("Found " + defaultWorkspaces.size() + " default workspaces. Fixing...");
                // Keep first one as default, remove default from others
                for (int i = 1; i < defaultWorkspaces.size(); i++) {
                    Workspace ws = defaultWorkspaces.get(i);
                    ws.setIsDefault(false);
                    workspaceDAO.update(ws);
                    System.out.println("Removed default from: " + ws.getName());
                }
            }
        } catch (Exception ex) {
            System.err.println("Error fixing defaults: " + ex.getMessage());
        }
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
            
            // Business rule: At least one default must exist (only when creating new workspace)
            boolean isNewWorkspace = (workspace.getId() == null || workspace.getId().isBlank());
            if (isNewWorkspace && !workspace.getIsDefault() && !hasOtherDefaults()) {
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

    public String edit(Workspace selected) {
        System.out.println("WorkspaceBean.edit() called for: " + selected.getName());
        
        // Reset and populate workspace object
        this.workspace = new Workspace();
        this.workspace.setId(selected.getId());
        this.workspace.setName(selected.getName());
        this.workspace.setDescription(selected.getDescription());
        this.workspace.setIcon(selected.getIcon());
        this.workspace.setOwnerName(selected.getOwnerName());
        this.workspace.setIsDefault(selected.getIsDefault());
        this.workspace.setCreatedAt(selected.getCreatedAt());
        
        this.editing = true;
        
        System.out.println("Workspace object populated: " + this.workspace.getName());
        System.out.println("Editing mode: " + this.editing);
        
        addMessage(FacesMessage.SEVERITY_INFO, "Editing: " + selected.getName());
        
        // Return null to stay on same page
        return null;
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
        if (workspace == null) {
            workspace = new Workspace();
        }
        System.out.println("getWorkspace() called - Name: " + workspace.getName() + ", ID: " + workspace.getId());
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
