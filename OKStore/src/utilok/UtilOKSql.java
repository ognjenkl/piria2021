package utilok;

import java.util.List;

public class UtilOKSql {

	public static String adjustSqlInOperatorValues(String sql, List<String> list, String placeholder) { 
		String retVal = null;
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			sb.append("?,");
		}
		sb.deleteCharAt(sb.length() - 1);
		retVal = sql.replace(placeholder, sb.toString());
		return retVal; 
	}
}
