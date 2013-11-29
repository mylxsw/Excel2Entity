package pw.agiledev.e2e.rule;

import pw.agiledev.e2e.extension.ExcelRule;

public class NoneRule implements ExcelRule<Object> {

	public void check(Object value, String columnName, String fieldName) {
	}

	public Object filter(Object value, String columnName, String fieldName) {
		return value;
	}

}
