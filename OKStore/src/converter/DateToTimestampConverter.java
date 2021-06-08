package converter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import utilok.UtilOKJSF;

@FacesConverter("dateToTimestampConverter")
public class DateToTimestampConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
		Date date = null;
		Timestamp timestamp = null;
		try {
			date = sdf.parse(value);
			timestamp = new Timestamp(date.getTime());
		} catch (ParseException e) {
			System.out.println("DateToTimestamp.class#getAsObject unparseable date: " + value);
			UtilOKJSF.jsfMessage("formAddArticle:startSellingDate", "errorStartSellingDateConvertor");
			return value;
		}
		return timestamp;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (!(value instanceof Date)){
			System.out.println("DateToTimestamp.class#getAsString not a date object: " + value);
			UtilOKJSF.jsfMessage("formAddArticle:startSellingDate", "errorStartSellingDateConvertor");
		}
		return new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss").format(value);
	}

}
