package com.notekeeper.jsf.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("hexColorValidator")
public class HexColorValidator implements Validator<String> {

    @Override
    public void validate(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
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
