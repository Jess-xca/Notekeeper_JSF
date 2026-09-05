package com.notekeeper.jsf.bean;

import com.notekeeper.jsf.dao.TagDAO;
import com.notekeeper.jsf.model.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named("tagBean")
@RequestScoped
public class TagBean {

    private final TagDAO tagDAO = new TagDAO();
    private Tag tag = new Tag();
    private List<Tag> tags = new ArrayList<>();
    private boolean editing;

    @PostConstruct
    public void init() {
        load();
    }

    public void load() {
        tags = tagDAO.findAll();
    }

    public void save() {
        try {
            if (tag.getId() == null || tag.getId().isBlank()) {
                tagDAO.save(tag);
                addMessage(FacesMessage.SEVERITY_INFO, "Tag created successfully.");
            } else {
                tagDAO.update(tag);
                addMessage(FacesMessage.SEVERITY_INFO, "Tag updated successfully.");
            }
            reset();
            load();
        } catch (Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Unable to save tag: " + ex.getMessage());
        }
    }

    public void edit(Tag selected) {
        this.tag = new Tag();
        this.tag.setId(selected.getId());
        this.tag.setName(selected.getName());
        this.tag.setColor(selected.getColor());
        this.tag.setCreatedAt(selected.getCreatedAt());
        this.editing = true;
    }

    public void delete(Tag selected) {
        try {
            tagDAO.delete(selected.getId());
            addMessage(FacesMessage.SEVERITY_INFO, "Tag deleted successfully.");
            if (tag.getId() != null && tag.getId().equals(selected.getId())) {
                reset();
            }
            load();
        } catch (Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Unable to delete tag: " + ex.getMessage());
        }
    }

    public void reset() {
        tag = new Tag();
        editing = false;
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, text));
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public boolean isEditing() {
        return editing;
    }
}
