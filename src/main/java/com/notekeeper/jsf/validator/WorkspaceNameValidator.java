package com.notekeeper.jsf.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("workspaceNameValidator")
public class WorkspaceNameValidator implements Validator<String> {

    @Override
    public void validate(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        String trimmed = value.trim();
        if (trimmed.equalsIgnoreCase("admin") || trimmed.equalsIgnoreCase("system")) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Workspace name cannot be a reserved name (admin or system).",
                    "Workspace name cannot be a reserved name (admin or system)."));
        }

        if (!trimmed.matches("^[A-Za-z][A-Za-z0-9\\s-]*$")) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Workspace name must start with a letter and may contain letters, numbers, spaces, and hyphens only.",
                    "Workspace name must start with a letter and may contain letters, numbers, spaces, and hyphens only."));
        }
    }
}
