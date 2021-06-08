package validator;

import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("passwordLengthAndCharsValidator")
public class PasswordLengthAndCharsValidator implements Validator<String> {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, String arg2) throws ValidatorException {
		String errorMessage = (String) arg1.getAttributes().get("errorMessagePassRegister");
		
		final Pattern textPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");

		if (arg2.length() < 15 || !textPattern.matcher(arg2).matches())
			throw new ValidatorException(new FacesMessage(errorMessage));
	}
}
