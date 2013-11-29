package pw.agiledev.e2e.ExcelToEntity;

import pw.agiledev.e2e.exception.ExcelContentInvalidException;
import pw.agiledev.e2e.extension.ExcelRule;

public class MyStringCheckRule implements ExcelRule<String> {

	public void check(Object value, String columnName, String fieldName) throws ExcelContentInvalidException {
		String val = (String) value;
		System.out.println("-------->   检测的列名为  " + columnName + "， 填充的字段名为 " + fieldName );
		if(val.length() > 10){
			throw new ExcelContentInvalidException("内容超长!");
		}
	}

	public String filter(Object value, String columnName, String fieldName) {
		String val = (String) value;
		return "[[[[" + val + "]]]";
	}

}
