package com.notekeeper.jsf.bean;

import com.notekeeper.jsf.dao.TagDAO;
import com.notekeeper.jsf.model.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named("tagBean")
@ApplicationScoped
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

    public String edit(Tag selected) {
        System.out.println("TagBean.edit() called for: " + selected.getName());
        
        // Reset and populate tag object
        this.tag = new Tag();
        this.tag.setId(selected.getId());
        this.tag.setName(selected.getName());
        this.tag.setColor(selected.getColor());
        this.tag.setCreatedAt(selected.getCreatedAt());
        
        this.editing = true;
        
        System.out.println("Tag object populated: " + this.tag.getName() + ", Color: " + this.tag.getColor());
        System.out.println("Editing mode: " + this.editing);
        
        addMessage(FacesMessage.SEVERITY_INFO, "Editing: " + selected.getName());
        
        // Return null to stay on same page
        return null;
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
        System.out.println("TagBean.reset() called");
        tag = new Tag();
        editing = false;
        System.out.println("Tag reset to new instance");
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, text));
    }

    public Tag getTag() {
        if (tag == null) {
            System.out.println("Tag was null, creating new one");
            tag = new Tag();
        }
        System.out.println("getTag() called - Name: " + tag.getName() + ", ID: " + tag.getId() + ", Color: " + tag.getColor());
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
