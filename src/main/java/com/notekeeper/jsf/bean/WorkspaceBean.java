package com.notekeeper.jsf.bean;

import com.notekeeper.jsf.dao.WorkspaceDAO;
import com.notekeeper.jsf.model.Workspace;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Named("workspaceBean")
@ApplicationScoped
public class WorkspaceBean {

    private static final String DEFAULT_WORKSPACE_NAME = "Personal";

    private final WorkspaceDAO workspaceDAO = new WorkspaceDAO();
    private Workspace workspace = new Workspace();
    private List<Workspace> workspaces = new ArrayList<>();
    private boolean editing;

    @PostConstruct
    public void init() {
        if (workspace == null) {
            workspace = new Workspace();
        }
        ensureSingleDefault();
        load();
    }

    public void load() {
        workspaces = workspaceDAO.findAll();
    }

    public void save() {
        try {
            boolean isNew = workspace.getId() == null || workspace.getId().trim().isEmpty();
            List<Workspace> existing = workspaceDAO.findAll();

            // Only "Personal" is the default workspace
            boolean shouldBeDefault = DEFAULT_WORKSPACE_NAME.equalsIgnoreCase(
                    workspace.getName() != null ? workspace.getName().trim() : "");
            workspace.setIsDefault(shouldBeDefault);

            // First workspace ever created becomes default if it is not named Personal yet
            if (isNew && existing.isEmpty()) {
                workspace.setIsDefault(true);
            }

            if (Boolean.TRUE.equals(workspace.getIsDefault())) {
                clearOtherDefaults(workspace.getId());
            }

            if (isNew) {
                workspaceDAO.save(workspace);
                addMessage(FacesMessage.SEVERITY_INFO, "Workspace created successfully.");
            } else {
                workspaceDAO.update(workspace);
                addMessage(FacesMessage.SEVERITY_INFO, "Workspace updated successfully.");
            }

            ensureSingleDefault();
            reset();
            load();
        } catch (Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Unable to save workspace: " + ex.getMessage());
        }
    }

    public String edit(Workspace selected) {
        Workspace editWorkspace = new Workspace();
        editWorkspace.setId(selected.getId());
        editWorkspace.setName(selected.getName());
        editWorkspace.setDescription(selected.getDescription());
        editWorkspace.setIcon(selected.getIcon());
        editWorkspace.setOwnerName(selected.getOwnerName());
        editWorkspace.setIsDefault(selected.getIsDefault());
        editWorkspace.setCreatedAt(selected.getCreatedAt());

        this.workspace = editWorkspace;
        this.editing = true;
        return "workspaces?faces-redirect=true";
    }

    public void delete(Workspace selected) {
        try {
            if (workspaces.size() <= 1) {
                addMessage(FacesMessage.SEVERITY_ERROR,
                        "Cannot delete the last workspace. At least one workspace is required.");
                return;
            }

            boolean wasDefault = Boolean.TRUE.equals(selected.getIsDefault());
            workspaceDAO.delete(selected.getId());

            if (wasDefault) {
                ensureSingleDefault();
            }

            if (workspace.getId() != null && workspace.getId().equals(selected.getId())) {
                reset();
            }
            load();
            addMessage(FacesMessage.SEVERITY_INFO, "Workspace deleted successfully.");
        } catch (Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Unable to delete workspace: " + ex.getMessage());
        }
    }

    public void reset() {
        workspace = new Workspace();
        editing = false;
    }

    /**
     * Exactly one default: prefer a workspace named "Personal", otherwise the first by name.
     */
    private void ensureSingleDefault() {
        List<Workspace> all = workspaceDAO.findAll();
        if (all.isEmpty()) {
            return;
        }

        Optional<Workspace> personal = all.stream()
                .filter(ws -> DEFAULT_WORKSPACE_NAME.equalsIgnoreCase(ws.getName()))
                .findFirst();

        Workspace chosen = personal.orElse(all.get(0));

        for (Workspace ws : all) {
            boolean shouldBeDefault = ws.getId().equals(chosen.getId());
            if (shouldBeDefault != Boolean.TRUE.equals(ws.getIsDefault())) {
                ws.setIsDefault(shouldBeDefault);
                workspaceDAO.update(ws);
            }
        }
    }

    private void clearOtherDefaults(String keepId) {
        for (Workspace ws : workspaceDAO.findAll()) {
            if (Boolean.TRUE.equals(ws.getIsDefault())
                    && (keepId == null || keepId.trim().isEmpty() || !ws.getId().equals(keepId))) {
                ws.setIsDefault(false);
                workspaceDAO.update(ws);
            }
        }
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, text));
    }

    public Workspace getWorkspace() {
        if (workspace == null) {
            workspace = new Workspace();
        }
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
