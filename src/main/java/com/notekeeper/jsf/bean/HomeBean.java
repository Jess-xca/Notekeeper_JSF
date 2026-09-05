package com.notekeeper.jsf.bean;

import com.notekeeper.jsf.dao.TagDAO;
import com.notekeeper.jsf.dao.WorkspaceDAO;
import com.notekeeper.jsf.model.Tag;
import com.notekeeper.jsf.model.Workspace;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named("homeBean")
@ApplicationScoped
public class HomeBean {

    private final TagDAO tagDAO = new TagDAO();
    private final WorkspaceDAO workspaceDAO = new WorkspaceDAO();

    private List<Tag> tags = new ArrayList<>();
    private List<Workspace> workspaces = new ArrayList<>();

    @PostConstruct
    public void init() {
        refreshData();
    }
    
    private void refreshData() {
        tags = tagDAO.findAll();
        workspaces = workspaceDAO.findAll();
    }

    public int getTagCount() {
        refreshData(); // Always get fresh data
        return tags.size();
    }

    public int getWorkspaceCount() {
        refreshData(); // Always get fresh data
        return workspaces.size();
    }

    public List<Tag> getTags() {
        refreshData(); // Always get fresh data
        return tags.size() > 4 ? tags.subList(0, 4) : tags;
    }

    public List<Workspace> getWorkspaces() {
        refreshData(); // Always get fresh data
        return workspaces.size() > 4 ? workspaces.subList(0, 4) : workspaces;
    }
}
