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
            System.out.println("Starting PERSONAL-ONLY fix...");
            
            // Set ALL workspaces to non-default first
            workspaceDAO.executeDirectSQL("UPDATE jsf_workspaces SET isDefault = false");
            System.out.println("PERSONAL-ONLY: Set ALL workspaces to non-default");
            
            // Set only "Personal" workspace as default
            workspaceDAO.executeDirectSQL("UPDATE jsf_workspaces SET isDefault = true WHERE LOWER(name) = 'personal'");
            System.out.println("PERSONAL-ONLY: Set 'Personal' workspace as THE ONLY default");
            
            System.out.println("PERSONAL-ONLY fix completed!");
        } catch (Exception ex) {
            System.err.println("Error in Personal-only fix: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void load() {
        System.out.println("=== LOADING WORKSPACES ===");
        workspaces = workspaceDAO.findAll();
        
        System.out.println("Loaded " + workspaces.size() + " workspaces:");
        for (Workspace ws : workspaces) {
            System.out.println("- Name: '" + ws.getName() + "' | isDefault: " + ws.getIsDefault() + " | Class: " + (ws.getIsDefault() != null ? ws.getIsDefault().getClass().getSimpleName() : "null"));
        }
        System.out.println("=========================");
    }

    public void save() {
        try {
            System.out.println("Save called - IsDefault checkbox value: " + workspace.getIsDefault());
            
            if (workspace.getIsDefault() == null) {
                workspace.setIsDefault(false);
            }
            
            // Business rule: First workspace automatically becomes default
            List<Workspace> allWorkspaces = workspaceDAO.findAll();
            boolean isFirstWorkspace = (workspace.getId() == null || workspace.getId().isBlank()) 
                && allWorkspaces.isEmpty();
            
            if (isFirstWorkspace) {
                workspace.setIsDefault(true);
                System.out.println("First workspace - setting as default");
            }
            
            // No default logic needed - handled in display
            
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
        
        // Create a completely new workspace object to avoid reference issues
        Workspace editWorkspace = new Workspace();
        editWorkspace.setId(selected.getId());
        editWorkspace.setName(selected.getName());
        editWorkspace.setDescription(selected.getDescription());
        editWorkspace.setIcon(selected.getIcon());
        editWorkspace.setOwnerName(selected.getOwnerName());
        editWorkspace.setIsDefault(selected.getIsDefault());
        editWorkspace.setCreatedAt(selected.getCreatedAt());
        
        // Set the workspace reference
        this.workspace = editWorkspace;
        this.editing = true;
        
        System.out.println("Workspace object populated: " + this.workspace.getName());
        System.out.println("Editing mode: " + this.editing);
        
        addMessage(FacesMessage.SEVERITY_INFO, "Editing: " + selected.getName());
        
        // Force a page redirect to refresh the view state completely
        return "workspaces?faces-redirect=true";
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
        System.out.println("RESET() called! Current editing mode: " + editing);
        workspace = new Workspace();
        editing = false;
        System.out.println("Reset completed - workspace is now empty");
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, text));
    }

    public Workspace getWorkspace() {
        if (workspace == null) {
            System.out.println("Workspace was null, creating new one");
            workspace = new Workspace();
        }
        System.out.println("getWorkspace() called - Name: " + workspace.getName() + ", ID: " + workspace.getId() + ", Owner: " + workspace.getOwnerName() + ", Editing: " + editing);
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
