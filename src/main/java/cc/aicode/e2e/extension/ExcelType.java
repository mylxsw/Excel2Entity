package cc.aicode.e2e.extension;

/**
 * 扩展字段类型抽象父类
 * 泛型量必须与子类类名相同！！！！
 *
 * @param <T>
 */
public abstract class ExcelType<T> {
    public abstract T parseValue(String value);

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
