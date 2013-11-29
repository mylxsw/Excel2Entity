package pw.agiledev.e2e.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pw.agiledev.e2e.extension.ExcelRule;
import pw.agiledev.e2e.rule.NoneRule;
/**
 * 功能说明： 标记字段为Excel填充字段
 * 参数说明：
 * @author 管宜尧
 * 2013-11-28 下午4:40:26
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelProperty {
	/**
	 * 功能说明： 采用的列名， 该列名对应Excel中第一列的内容
	 * 参数说明：
	 * @author 管宜尧
	 * 2013-11-28 下午4:40:47
	 */
	String value() default "";
	/**
	 * 功能说明： 该列是否必须
	 * 参数说明：
	 * @author 管宜尧
	 * 2013-11-28 下午4:41:05
	 */
	boolean required() default false;
	/**
	 * 校验规则类
	 * @return
	 */
	Class<? extends ExcelRule> rule() default NoneRule.class;
}
