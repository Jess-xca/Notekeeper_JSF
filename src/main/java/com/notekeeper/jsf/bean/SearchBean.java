package com.notekeeper.jsf.bean;

import com.notekeeper.jsf.dao.TagDAO;
import com.notekeeper.jsf.dao.WorkspaceDAO;
import com.notekeeper.jsf.model.Tag;
import com.notekeeper.jsf.model.Workspace;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named("searchBean")
@RequestScoped
public class SearchBean {

    private final TagDAO tagDAO = new TagDAO();
    private final WorkspaceDAO workspaceDAO = new WorkspaceDAO();
    private String searchQuery = "";
    private List<Object> searchResults = new ArrayList<>();

    public void search() {
        searchResults.clear();
        
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return;
        }
        
        String query = searchQuery.toLowerCase().trim();
        
        try {
            // Search in workspaces
            List<Workspace> workspaces = workspaceDAO.findAll();
            for (Workspace workspace : workspaces) {
                if (workspace.getName().toLowerCase().contains(query) ||
                    (workspace.getDescription() != null && workspace.getDescription().toLowerCase().contains(query)) ||
                    workspace.getOwnerName().toLowerCase().contains(query)) {
                    searchResults.add(workspace);
                }
            }
            
            // Search in tags
            List<Tag> tags = tagDAO.findAll();
            for (Tag tag : tags) {
                if (tag.getName().toLowerCase().contains(query)) {
                    searchResults.add(tag);
                }
            }
            
        } catch (Exception ex) {
            // keep search resilient; empty results on failure
            searchResults.clear();
        }
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public List<Object> getSearchResults() {
        return searchResults;
    }

    public boolean hasResults() {
        return !searchResults.isEmpty();
    }

    public String getResultType(Object item) {
        if (item instanceof Workspace) {
            return "Workspace";
        } else if (item instanceof Tag) {
            return "Tag";
        }
        return "Unknown";
    }

    public String getResultName(Object item) {
        if (item instanceof Workspace) {
            return ((Workspace) item).getName();
        } else if (item instanceof Tag) {
            return ((Tag) item).getName();
        }
        return "";
    }

    public String getResultDescription(Object item) {
        if (item instanceof Workspace) {
            Workspace ws = (Workspace) item;
            return ws.getDescription() != null ? ws.getDescription() : "No description";
        } else if (item instanceof Tag) {
            return "Tag for organizing notes";
        }
        return "";
    }

    public String getResultLink(Object item) {
        if (item instanceof Workspace) {
            return "workspaces.xhtml";
        } else if (item instanceof Tag) {
            return "tags.xhtml";
        }
        return "#";
    }
}