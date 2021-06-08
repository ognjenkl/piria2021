package validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("usernameLengthAndForbidenCharsValidator")
public class UsernameLengthAndForbidenCharsValidator implements Validator<String> {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, String arg2) throws ValidatorException {
		String errorMessage = (String) arg1.getAttributes().get("errorMessageUsernameRegister");
		
		if (arg2.length() < 12 || arg2.contains("@") || arg2.contains("#") || arg2.contains("/"))
			throw new ValidatorException(new FacesMessage(errorMessage));
	}
}
