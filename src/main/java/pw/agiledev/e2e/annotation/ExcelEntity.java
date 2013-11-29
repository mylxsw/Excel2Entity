package pw.agiledev.e2e.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 功能说明： 标记实体为Excel实体
 * 参数说明：
 * @author 管宜尧
 * 2013-11-28 下午4:40:00
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelEntity {
}
