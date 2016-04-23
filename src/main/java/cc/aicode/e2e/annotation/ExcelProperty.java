package cc.aicode.e2e.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cc.aicode.e2e.extension.ExcelRule;
import cc.aicode.e2e.rule.NoneRule;

/**
 * 标记字段为Excel填充字段
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelProperty {
    /**
     * 采用的列名， 该列名对应Excel中第一列的内容
     *
     * @return
     */
    String value() default "";

    /**
     * 该列是否必须
     *
     * @return
     */
    boolean required() default false;

    /**
     * 校验规则类
     *
     * @return
     */
    @SuppressWarnings("rawtypes") Class<? extends ExcelRule> rule() default NoneRule.class;

    /**
     * 正则表达式校验规则
     * 该项仅对java.lang.String, java.lang.Long, java.lang.Short,
     * java.lang.Integer类型有效
     *
     * @return
     */
    String regexp() default "";

    /**
     * 正则规则校验失败错误提示信息
     *
     * @return
     */
    String regexpErrorMessage() default "";

    /**
     * 默认值
     * 默认值均采用String类型，系统将会自动进行类型转换，不支持对象类型!
     *
     * @return
     */
    String defaultValue() default "";

    /**
     * 是否采用默认值
     * 如果为true，默认值为空的时候会使用空字符串，否则使用null
     *
     * @return
     */
    boolean hasDefaultValue() default false;
}
