package com.notekeeper.jsf.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("hexColorValidator")
public class HexColorValidator implements Validator<String> {

    @Override
    public void validate(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return;
        }

        if (!value.matches("^#([A-Fa-f0-9]{6})$")) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Color must be a valid hex value such as #2563EB.",
                    "Color must be a valid hex value such as #2563EB."));
        }
    }
}
