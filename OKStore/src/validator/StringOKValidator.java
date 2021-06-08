package validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

@FacesValidator("stringOKValidator")
public class StringOKValidator extends OKValidator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String errorMessage = (String) component.getAttributes().get("errorMessage");
		String property = component.getId();
		String input = null;
		if (value instanceof String)
			input = (String) value;
		else
			throw new ValidatorException(new FacesMessage(errorMessage));
			
		if (!isValidStringInput(property, input))
			throw new ValidatorException(new FacesMessage(errorMessage));

	}
	
}
