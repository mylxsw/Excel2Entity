package cc.aicode.e2e.Excel2Entity;

import cc.aicode.e2e.extension.ExcelType;

public class MyDataType extends ExcelType<MyDataType> {
    private String value;

    @Override
    public MyDataType parseValue(String value) {
        this.value = "新-->" + value + "<--";
        return this;
    }

    @Override
    public String toString() {
        return "MyDataType [value=" + value + "]";
    }

}
