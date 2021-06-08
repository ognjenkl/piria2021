package validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import utilok.UtilOKJSF;

/**
 * 
 * JSF 2.2 password confirmation validator.
 * 
 * @author ognjen
 *
 */
@FacesValidator("passwordConfirmValidator")
public class PasswordConfirmValidator implements Validator {

	public PasswordConfirmValidator() {

	}

	/* (non-Javadoc)
	 * @see javax.faces.validator.Validator#validate(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
		String password = (String) value;
		String passwordConfirm = (String) component.getAttributes().get("confirmPasswordAttribute");

		if(password == null || password.isEmpty() || passwordConfirm == null || passwordConfirm.isEmpty())
			return;
		
		if(!password.equals(passwordConfirm)){
			throw new ValidatorException(new FacesMessage(UtilOKJSF.getLangMessage("passwordsConfirmValidatorErrorMessage")));
		}
	}

}
