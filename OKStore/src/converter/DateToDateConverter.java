package converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("dateToDateConverter")
public class DateToDateConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
		Date date = null;
		java.sql.Date dateConverted = null;
		try {
			date = sdf.parse(arg2);
			dateConverted = new java.sql.Date(date.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateConverted;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		return new SimpleDateFormat("dd.MM.yyyy.").format(arg2);
	}

}
