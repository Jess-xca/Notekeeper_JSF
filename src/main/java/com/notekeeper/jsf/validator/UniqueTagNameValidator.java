package com.notekeeper.jsf.validator;

import com.notekeeper.jsf.dao.TagDAO;
import com.notekeeper.jsf.model.Tag;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("uniqueTagNameValidator")
public class UniqueTagNameValidator implements Validator<String> {

    private final TagDAO tagDAO = new TagDAO();

    @Override
    public void validate(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return;
        }

        Tag existing = tagDAO.findByName(value.trim());
        if (existing == null) {
            return;
        }

        Object currentId = component.getAttributes().get("currentId");
        if (currentId != null && existing.getId().equals(currentId.toString())) {
            return;
        }

        throw new ValidatorException(new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "A tag with this name already exists.",
                "A tag with this name already exists."));
    }
}
