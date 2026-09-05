package com.notekeeper.jsf.bean;

import com.notekeeper.jsf.dao.TagDAO;
import com.notekeeper.jsf.dao.WorkspaceDAO;
import com.notekeeper.jsf.model.Tag;
import com.notekeeper.jsf.model.Workspace;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

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
        tags = tagDAO.findAll();
        workspaces = workspaceDAO.findAll();
    }

    public int getTagCount() {
        return tags.size();
    }

    public int getWorkspaceCount() {
        return workspaces.size();
    }

    public List<Tag> getTags() {
        return tags.size() > 4 ? tags.subList(0, 4) : tags;
    }

    public List<Workspace> getWorkspaces() {
        return workspaces.size() > 4 ? workspaces.subList(0, 4) : workspaces;
    }
}
