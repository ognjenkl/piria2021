package validator;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public abstract class OKValidator implements Validator {

	@Override
	public abstract void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException;

	/**
	 * Checks if input string has no excessive length.
	 * 
	 * @param property
	 *            name that is validated
	 * @param input
	 *            came for property
	 * @param maxLength
	 *            validate property input for this value
	 * @return true if valid input
	 */
	public boolean isValidExcessiveCharsLength(String property, String input, Integer maxLength) {
		boolean retVal = input.length() < maxLength;
		if (!retVal)
			System.out.format("Validation failed for %s in isValidExcessiveCharsLength [%d] for: %s...\n", property,
					maxLength, input.substring(0, maxLength));
		return retVal;
	}

	/**
	 * XSS filter basic check.
	 * 
	 * @param input
	 *            string to check if is valid.
	 * @return true if input is with allowed chars.
	 */
	public boolean isValidBasic(String property, String input) {
		Pattern forbidenCharsPattern = Pattern.compile("^[^<>]+$");
		Matcher matcher = forbidenCharsPattern.matcher(input);
		boolean retVal = matcher.matches();
		if (!retVal)
			System.out.format("Validation failed for %s in isValidBasic for: %s\n", property, input);
		return retVal;
	}

	/**
	 * Check if input string has the allowed characters
	 * @param property name
	 * @param input to be checked
	 * @return true if <code>input</code> consists of allowed characters.
	 */
	public static boolean isValidAllowedChars(String property, String input) {
		StringBuilder allowedCharsPattern = new StringBuilder();
		allowedCharsPattern.append("[");
		
		allowedCharsPattern.append("a-z");
		allowedCharsPattern.append("A-Z");
		allowedCharsPattern.append("0-9");
		allowedCharsPattern.append("@");
		allowedCharsPattern.append("\\.");
		allowedCharsPattern.append("'");
		allowedCharsPattern.append(" ");
		allowedCharsPattern.append("!");
		allowedCharsPattern.append("#");
		allowedCharsPattern.append("/");

		allowedCharsPattern.append("]");

		
		boolean retVal = input.matches("^" + allowedCharsPattern.toString() + "{" + "1" + "," + input.length() + "}"+ "$");
		if (!retVal)
			System.out.format("Validation failed for %s in isValidAllowedChars for input: %s\n",
					property, input);
		return retVal;
	}
	
	/**
	 * Check if input string has the allowed characters
	 * 
	 * @param input
	 * @param minLentgh
	 *            positive integer
	 * @param maxLength
	 * @return
	 */
	public boolean isValidAllowedCharsAndLength(String property, String input, Integer minLength, Integer maxLength) {
		StringBuilder allowedCharsPattern = new StringBuilder();
		allowedCharsPattern.append("[");
		
		allowedCharsPattern.append("a-z");
		allowedCharsPattern.append("A-Z");
		allowedCharsPattern.append("0-9");
		allowedCharsPattern.append("@");
		allowedCharsPattern.append("\\.");
		allowedCharsPattern.append("'");
		allowedCharsPattern.append("!");
		allowedCharsPattern.append("#");
		allowedCharsPattern.append("/");
		
		allowedCharsPattern.append("]");

		
		boolean retVal = input.matches("^" + allowedCharsPattern.toString() + "{" + minLength + "," + maxLength + "}$");
		if (!retVal)
			System.out.format("Validation failed for %s in isValidAllowedCharsAndLength [%d, %d] for: %s...\n",
					property, minLength, maxLength, input.substring(minLength, maxLength));
		return retVal;
	}

	public boolean isValidListLength(List<?> list, Integer maxSize) {
		if (list != null && list.size() > maxSize) {
			System.out.format("Validation failed for list size greater than maxSize [%d] in isValidListLength \n",
					maxSize);
			return false;
		} else
			return true;
	}

	public boolean isValidDate(Object dateObject) {
		if (dateObject != null && !(dateObject instanceof Date)) {
			System.out.format("Validation failed for date in isValidDate\n");
			return false;
		}
		return true;
	}

	public boolean isValidStringInput(String property, String input, Integer minLength, Integer maxLength) {
		if (isValidExcessiveCharsLength(property, input, maxLength) && isValidBasic(property, input)
				&& isValidAllowedCharsAndLength(property, input, minLength, maxLength))
			return true;
		return false;

	}

	/**
	 * Validates if String input does not have excessive length, is not XSS
	 * (basic) and has allowed length.
	 * 
	 * @param property
	 *            String that describes validated field in notification message
	 * @param input
	 *            value to be validated
	 * @param minLength
	 *            of input
	 * @param maxLengthExcessive
	 *            not allowed excessive length of input
	 * @param maxLengthAllowed
	 *            max allowed length of input
	 * @return true if valid input, false if validation fail
	 */
	public boolean isValidStringInput(String property, String input, Integer minLength, Integer maxLengthAllowed,
			Integer maxLengthExcessive) {
		if (input != null && property != null && minLength != null && maxLengthExcessive != null
				&& maxLengthAllowed != null && isValidExcessiveCharsLength(property, input, maxLengthExcessive)
				&& isValidBasic(property, input)
				&& isValidAllowedCharsAndLength(property, input, minLength, maxLengthAllowed))
			return true;
		return false;

	}

	/**
	 * Validates if String input does not have excessive length, is not XSS
	 * (basic) and has allowed length.
	 * 
	 * @param property
	 *            String that describes validated field in notification message
	 * @param input
	 *            value to be validated
	 * @param minLength
	 *            of input
	 * @param maxLengthExcessive
	 *            not allowed excessive length of input
	 * @param maxLengthAllowed
	 *            max allowed length of input
	 * @return true if valid input, false if validation fail
	 */
	public boolean isValidStringInput(String property, String input) {
		if (input != null && property != null
				&& isValidBasic(property, input)
				&& isValidAllowedChars(property, input))
			return true;
		return false;

	}
	
	public boolean isValidBigDecimal(String property, BigDecimal input) {
		Boolean retVal = input != null && input.compareTo(new BigDecimal(0)) >= 0
				&& input.compareTo(new BigDecimal(1000000)) < 0;
		if (!retVal)
			System.out.format("Validation failed for %s in isValidBigDecimal for: %.2f...\n", property, input);
		return retVal;
	}

}
