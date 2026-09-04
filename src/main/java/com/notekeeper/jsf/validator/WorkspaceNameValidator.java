package com.notekeeper.jsf.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("workspaceNameValidator")
public class WorkspaceNameValidator implements Validator<String> {

    @Override
    public void validate(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
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
