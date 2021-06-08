package validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

@FacesValidator("dateOKValidator")
public class DateOKValidator extends OKValidator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String errorMessage = (String) component.getAttributes().get("errorMessage");
		if (!(value instanceof Date))
			throw new ValidatorException(new FacesMessage(errorMessage));

		Date input = (Date) value;
		if (!isValidDate(input))
			throw new ValidatorException(new FacesMessage(errorMessage));
	}

}
