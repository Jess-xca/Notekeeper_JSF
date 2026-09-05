package com.notekeeper.jsf.bean;

import com.notekeeper.jsf.dao.TagDAO;
import com.notekeeper.jsf.model.Tag;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

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
            if (tag.getId() == null || tag.getId().trim().isEmpty()) {
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
        Tag editTag = new Tag();
        editTag.setId(selected.getId());
        editTag.setName(selected.getName());
        editTag.setColor(selected.getColor());
        editTag.setCreatedAt(selected.getCreatedAt());

        this.tag = editTag;
        this.editing = true;
        return "tags?faces-redirect=true";
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
        if (tag == null) {
            tag = new Tag();
        }
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
