package validator;

import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

@FacesValidator("floatingPointOKValidator")
public class FloatingPointOKValidator extends OKValidator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String errorMessage = (String) component.getAttributes().get("errorMessage");
		try {
			String property = component.getId();
			BigDecimal input = new BigDecimal(value.toString());

			if (!isValidBigDecimal(property, input))
				throw new ValidatorException(new FacesMessage(errorMessage));
		} catch (Exception e) {
			throw new ValidatorException(new FacesMessage(errorMessage));

		}
	}

}
