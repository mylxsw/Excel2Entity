package cc.aicode.e2e;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import cc.aicode.e2e.annotation.ExcelEntity;
import cc.aicode.e2e.annotation.ExcelProperty;
import cc.aicode.e2e.exception.ExcelContentInvalidException;
import cc.aicode.e2e.exception.ExcelParseException;
import cc.aicode.e2e.exception.ExcelRegexpValidFailedException;
import cc.aicode.e2e.extension.ExcelRule;
import cc.aicode.e2e.extension.ExcelType;

/**
 * EXCEL操作助手函数
 *
 * 使用方法很简单，只需要使用静态方法readExcel即可。
 *      ExcelHelper eh = ExcelHelper.readExcel("excel文件名");
 * 如果要读取Excel中的标题栏有哪些 `eh.getHeaders()`
 * 如果要读取Excel中的数区域 `eh.getDatas()`
 * 读取到的数据按照Excel中存放的行列形式存放在二维数组中
 * 如果需要转换为实体列表的话 `eh.toEntitys(实体.class)`
 * 注意的是，实体类必须含有@ExcelEntity注解，同时需要用到的属性字段上需要
 * 用@ExcelProperty标注。
 *
 */
public class ExcelHelper {
    /**
     * 最小列数目
     */
    final public static int MIN_ROW_COLUMN_COUNT = 1;
    /**
     * 列索引
     */
    private int lastColumnIndex;
    /**
     * 从Excel中读取的标题栏
     */
    private String[] headers;
    /**
     * 从Excel中读取的数据
     */
    private String[][] datas;
    /**
     * 规则对象缓存
     */
    @SuppressWarnings("rawtypes")
    private static Map<String, ExcelRule> rulesCache = new HashMap<String, ExcelRule>();

    private ExcelHelper() {
    }

    @SuppressWarnings("rawtypes")
    private static List<Class<? extends ExcelType>>
            userDefinedType = new ArrayList<Class<? extends ExcelType>>();

    /**
     * 注册新字段类型
     * 扩展字段类型必须ExcelType抽象数据类型
     *
     * @param type
     * @throws ExcelParseException
     */
    public static void registerNewType(@SuppressWarnings("rawtypes") Class<? extends ExcelType> type)
            throws ExcelParseException {
        if (!userDefinedType.contains(type)) {
            userDefinedType.add(type);
        }
    }

    /**
     * 读取Excel内容
     *
     * @param excelFilename
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static ExcelHelper readExcel(String excelFilename) throws InvalidFormatException, IOException {
        return readExcel(excelFilename, 0);
    }

    /**
     * 读取Excel内容
     *
     * @param excelFilename
     * @param sheetIndex
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static ExcelHelper readExcel(String excelFilename, int sheetIndex) throws InvalidFormatException, IOException {
        return readExcel(new File(excelFilename), sheetIndex);
    }

    /**
     * 读取Excel内容
     *
     * @param file
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static ExcelHelper readExcel(File file) throws InvalidFormatException, IOException {
        return readExcel(file, 0);
    }

    /**
     * 读取Excel内容
     *
     * @param file
     * @param sheetIndex
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static ExcelHelper readExcel(File file, int sheetIndex) throws InvalidFormatException, IOException {
        // 读取Excel工作薄
        Workbook wb = WorkbookFactory.create(file);
        return _readExcel(wb, sheetIndex);
    }

    /**
     * 从文件流读取Excel
     *
     * @param ins
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static ExcelHelper readExcel(InputStream ins) throws InvalidFormatException, IOException {
        return readExcel(ins, 0);
    }

    /**
     * 读取Excel内容（从文件流）
     *
     * @param ins
     * @param sheetIndex
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static ExcelHelper readExcel(InputStream ins, int sheetIndex) throws InvalidFormatException, IOException {
        return _readExcel(WorkbookFactory.create(ins), sheetIndex);
    }

    /**
     * 获取表格数据二维表
     *
     * @return
     */
    public String[][] getDatas() {
        return datas;
    }

    /**
     * 获取表格标题
     *
     * @return
     */
    public String[] getHeaders() {
        return headers;
    }

    /**
     * 转换为实体
     *
     * @param classType
     * @param <T>
     * @return
     * @throws ExcelParseException
     * @throws ExcelContentInvalidException
     * @throws ExcelRegexpValidFailedException
     */
    public <T> List<T> toEntitys(Class<T> classType) throws ExcelParseException, ExcelContentInvalidException, ExcelRegexpValidFailedException {
        // 如果实体没有@ExcelEntity，则不允许继续操作
        ExcelEntity excelEntity = classType.getAnnotation(ExcelEntity.class);
        if (excelEntity == null) {
            throw new ExcelParseException("转换的实体必须存在@ExcelEntity!");
        }
        // 创建Excel实体字段信息
        List<ExcelEntityField> eefs = _getEntityFields(classType);

        // 创建实体对象集
        List<T> entitys = new ArrayList<T>();
        try {
            // 遍历提交的数据行，依次填充到创建的实体对象中
            for (String[] data : datas) {
                T obj = classType.newInstance();
                // 遍历实体对象的实体字段，通过反射为实体字段赋值
                for (ExcelEntityField eef : eefs) {

                    Method method = obj.getClass().getDeclaredMethod("set" +
                                    _toCapitalizeCamelCase(eef.getField().getName()),
                            eef.getField().getType());
                    try {
                        method.invoke(obj, _getFieldValue(data[eef.getIndex()], eef));
                    } catch (ExcelParseException e) {
                        if (eef.isRequired()) {
                            throw new ExcelParseException("字段" + eef.getColumnName() + "出错!", e);
                        }
                        continue;
                    } catch (ExcelContentInvalidException e) {
                        if (eef.isRequired()) {
                            throw e;
                        }
                        continue;
                    } catch (NullPointerException e) {
                        if (eef.isRequired()) {
                            throw new ExcelParseException("字段" + eef.getColumnName() + "出错!", e);
                        }
                        continue;
                    }
                }
                entitys.add(obj);
            }
        } catch (InstantiationException e1) {
            throw new ExcelParseException(e1);
        } catch (IllegalAccessException e1) {
            throw new ExcelParseException(e1);
        } catch (NoSuchMethodException e1) {
            throw new ExcelParseException(e1);
        } catch (SecurityException e1) {
            throw new ExcelParseException(e1);
        } catch (IllegalArgumentException e) {
            throw new ExcelParseException(e);
        } catch (InvocationTargetException e) {
            throw new ExcelParseException(e);
        } catch (Exception e) {
            throw new ExcelParseException(e);
        }

        return entitys;
    }

    /**
     * 转换驼峰命名方式
     *
     * @param name
     * @return
     */
    private static String _toCapitalizeCamelCase(String name) {
        if (name == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(name.length());
        boolean upperCase = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (c == '_') {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        name = sb.toString();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * 获取Excel实体类中的填充字段
     *
     * @param classType
     * @param <T>
     * @return
     * @throws ExcelParseException
     */
    private <T> List<ExcelEntityField> _getEntityFields(Class<T> classType) throws ExcelParseException {
        List<ExcelEntityField> eefs = new ArrayList<ExcelHelper.ExcelEntityField>();
        // 遍历所有字段
        Field[] allFields = classType.getDeclaredFields();
        for (Field field : allFields) {
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            // 只对含有@ExcelProperty注解的字段进行赋值
            if (excelProperty == null) {
                continue;
            }

            String key = excelProperty.value().trim();// Excel Header名
            boolean required = excelProperty.required(); // 该列是否为必须列

            int index = _indexOfHeader(key);
            // 如果字段必须，而索引为-1 ，说明没有这一列，抛错
            if (required && index == -1) {
                throw new ExcelParseException("字段" + key + "必须!");
            }

            ExcelEntityField eef = new ExcelEntityField();
            eef.setField(field);
            eef.setColumnName(key);
            eef.setRequired(required);
            eef.setIndex(_indexOfHeader(key));
            eef.setAnnotation(excelProperty);

            eefs.add(eef);
        }
        return eefs;
    }

    /**
     * 获取字段的值，路由不同的字段类型
     *
     * @param value
     * @param eef
     * @return
     * @throws ExcelParseException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ExcelContentInvalidException
     * @throws ExcelRegexpValidFailedException
     */
    @SuppressWarnings("rawtypes")
    private Object _getFieldValue(String value, ExcelEntityField eef) throws ExcelParseException, InstantiationException, IllegalAccessException, ExcelContentInvalidException, ExcelRegexpValidFailedException {
        // 进行规则校验
        ExcelProperty annotation = eef.getAnnotation();

        // 获取解析后的字段结果
        Object result = null;
        try {
            // 是否提交过来的是空值
            // 如果提交值是空值而且含有默认值的话
            // 则让提交过来的空值为默认值
            if (("".equals(value) || (value == null)) && annotation.hasDefaultValue()) {
                value = annotation.defaultValue();
            }
            result = _getFieldValue(value, eef.getField(), annotation.regexp());
        } catch (ExcelRegexpValidFailedException e) {
            // 捕获正则验证失败异常
            String errMsg = annotation.regexpErrorMessage();
            if ("".equals(errMsg)) {
                errMsg = "列 " + eef.getColumnName() + " 没有通过规则验证!";
            }
            throw new ExcelContentInvalidException(errMsg, e);
        } catch (NumberFormatException e) {
            throw new ExcelContentInvalidException("列 " + eef.getColumnName() + " 数据类型错误!");
        } catch (NullPointerException e) {
            throw new ExcelContentInvalidException("列 " + eef.getColumnName() + " 不能为空!");
        }
        /**
         * 缓存已经实例化过的规则对象，避免每次都重新
         * 创建新的对象的额外消耗
         */
        ExcelRule ruleObj;
        Class<? extends ExcelRule> rule = annotation.rule();
        if (rulesCache.containsKey(rule.getName())) {
            ruleObj = rulesCache.get(rule.getName());
        } else {
            ruleObj = rule.newInstance();
            rulesCache.put(rule.getName(), ruleObj);
        }

        // 进行校验
        ruleObj.check(result, eef.getColumnName(), eef.getField().getName());
        result = ruleObj.filter(result, eef.getColumnName(), eef.getField().getName());

        return result;
    }

    /**
     * 解析字段类型
     *
     * @param value
     * @param field
     * @return
     * @throws ExcelParseException
     * @throws ExcelContentInvalidException
     * @throws ExcelRegexpValidFailedException
     */
    @SuppressWarnings("rawtypes")
    private static Object _getFieldValue(String value, Field field, String regexp) throws ExcelParseException, ExcelContentInvalidException, ExcelRegexpValidFailedException {
        Class<?> type = field.getType();
        String typeName = type.getName();
        // 字符串
        if ("java.lang.String".equals(typeName)) {
            if (!"".equals(regexp) && !value.matches(regexp)) {
                throw new ExcelRegexpValidFailedException();
            }
            return value;
        }
        // 长整形
        if ("java.lang.Long".equals(typeName) || "long".equals(typeName)) {
            if (!"".equals(regexp) && !value.matches(regexp)) {
                throw new ExcelRegexpValidFailedException();
            }
            return Long.parseLong(value);
        }
        // 整形
        if ("java.lang.Integer".equals(typeName) || "int".equals(typeName)) {
            if (!"".equals(regexp) && !value.matches(regexp)) {
                throw new ExcelRegexpValidFailedException();
            }
            return Integer.parseInt(value);
        }
        // 短整型
        if ("java.lang.Short".equals(typeName) || "short".equals(typeName)) {
            if (!"".equals(regexp) && !value.matches(regexp)) {
                throw new ExcelRegexpValidFailedException();
            }
            return Short.parseShort(value);
        }
        // Date型
        if ("java.util.Date".equals(typeName)) {
            try {
                return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(value);
            } catch (ParseException e) {
                throw new ExcelParseException("日期类型格式有误!");
            }
        }
        // Timestamp
        if ("java.sql.Timestamp".equals(typeName)) {
            try {
                return new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(value).getTime());
            } catch (ParseException e) {
                throw new ExcelParseException("时间戳类型格式有误!");
            }
        }
        // Char型
        if ("java.lang.Character".equals(typeName) || "char".equals(typeName)) {
            if (value.length() == 1) {
                return value.charAt(0);
            }
        }
        // 用户注册的自定义类型
        for (Class<? extends ExcelType> et : userDefinedType) {
            if (et.getName().equals(typeName)) {
                try {
                    ExcelType newInstance = et.newInstance();
                    return newInstance.parseValue(value);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        throw new ExcelParseException("不支持的字段类型 " + typeName + " !");
    }

    /**
     * 列名在列标题中的索引
     *
     * @param columnName
     * @return
     */
    private int _indexOfHeader(String columnName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 解析EXCEL标题栏
     *
     * @param row
     */
    private void _parseExcelHeader(Row row) {
        lastColumnIndex = Math.max(row.getLastCellNum(), MIN_ROW_COLUMN_COUNT);
        headers = new String[lastColumnIndex];
        // 初始化headers，每一列的标题
        for (int columnIndex = 0; columnIndex < lastColumnIndex; columnIndex++) {
            Cell cell = row.getCell(columnIndex, Row.RETURN_BLANK_AS_NULL);
            headers[columnIndex] = _getCellValue(cell).trim();
        }
    }

    /**
     * 解析EXCEL数据区域内容
     *
     * @param sheet
     * @param rowStart
     * @param rowEnd
     */
    private void _parseExcelData(Sheet sheet, int rowStart, int rowEnd) {
        datas = new String[rowEnd][lastColumnIndex];
        for (int rowIndex = rowStart; rowIndex <= rowEnd; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            int rowNumber = rowIndex - rowStart;
            // 读取遍历每一行中的每一列
            for (int columnIndex = 0; columnIndex < lastColumnIndex; columnIndex++) {
                Cell cell = row.getCell(columnIndex, Row.RETURN_BLANK_AS_NULL);
                String value = _getCellValue(cell).trim();
                datas[rowNumber][columnIndex] = value;
            }
        }
    }

    /**
     * 读取每个单元格中的内容
     *
     * @param cell
     * @return
     */
    private String _getCellValue(Cell cell) {
        // 如果单元格为空的，则返回空字符串
        if (cell == null) {
            return "";
        }

        // 根据单元格类型，以不同的方式读取单元格的值
        String value = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cell.getDateCellValue());
                } else {
                    value = (long) cell.getNumericCellValue() + "";
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue() ? "TRUE" : "FALSE";
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = cell.getCellFormula();
                break;
            default:
        }
        return value;
    }

    /**
     * 读取Excel内容
     *
     * @param wb
     * @param sheetIndex
     * @return
     */
    private static ExcelHelper _readExcel(Workbook wb, int sheetIndex) {
        // 遍历Excel Sheet， 依次读取里面的内容
        if (sheetIndex > wb.getNumberOfSheets()) {
            return null;
        }
        Sheet sheet = wb.getSheetAt(sheetIndex);
        // 遍历表格的每一行
        int rowStart = sheet.getFirstRowNum();
        // 最小行数为1行
        int rowEnd = sheet.getLastRowNum();
        // 读取EXCEL标题栏
        ExcelHelper eh = new ExcelHelper();
        eh._parseExcelHeader(sheet.getRow(0));
        // 读取EXCEL数据区域内容
        eh._parseExcelData(sheet, rowStart + 1, rowEnd);
        return eh;
    }

    /**
     * Excel实体字段类（内部类）
     *
     */
    private class ExcelEntityField {
        private String columnName;
        private boolean required;
        private Field field;
        private int index;
        private ExcelProperty annotation;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public ExcelProperty getAnnotation() {
            return annotation;
        }

        public void setAnnotation(ExcelProperty annotation) {
            this.annotation = annotation;
        }
    }

}
