package pw.agiledev.e2e.extension;
/**
 * 功能说明： 扩展字段类型抽象父类
 * 参数说明：
 * 	泛型量必须与子类类名相同！！！！
 * @author 管宜尧
 * 2013-11-28 下午9:59:55
 */
public abstract class ExcelType<T> {
	public abstract T parseValue(String value);

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
