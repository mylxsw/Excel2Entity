package pw.agiledev.e2e.rule;

import pw.agiledev.e2e.extension.ExcelRule;
/**
 * 默认字段处理规则
 * 不对字段值进行任何处理
 * @author code.404
 * @2013年11月29日
 * Site : http://blog.agiledev.pw
 *
 */
public class NoneRule implements ExcelRule<Object> {

	public void check(Object value, String columnName, String fieldName) {
	}

	public Object filter(Object value, String columnName, String fieldName) {
		return value;
	}

}
