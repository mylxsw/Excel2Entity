Excel2Entity
====

Excel2Entity对`Java POI`对`xls`文件的读取进行了封装，实现了批量导入Excel中的数据时自动根据Excel中的数据行创建对应的`Java POJO`实体对象。

该类库也实现了在创建实体对象时对字段类型进行校验，可以对Excel中的数据类型合法性进行校验，通过实现扩展接口，可以实现自定义校验规则以及自定义实体对象字段类型等更加复杂的校验规则和字段类型转换。

###依赖

Excel2Entity依赖于`Apache POI`类库。

###使用说明

####普通实体创建

        ExcelHelper eh = ExcelHelper.readExcel("111.xls");
        List<Demo> entitys  = null;
        try {
            entitys = eh.toEntitys(Demo.class);
            for (Demo d : entitys) {
                System.out.println(d.toString());
            }
        } catch (ExcelParseException e) {
            System.out.println(e.getMessage());
        } catch (ExcelContentInvalidException e) {
            System.out.println(e.getMessage());
        } catch (ExcelRegexpValidFailedException e) {
            System.out.println(e.getMessage());
        }


####注册新的字段类型

注册的新的字段类型类必须实现ExcelType抽象类。

	ExcelHelper.registerNewType(MyDataType.class);


####实体对象

实体类必须标注@ExcelEntity注解， 同时需要填充的字段标注@ExcelProperty注解

    @ExcelEntity
    public class Demo {
        @ExcelProperty(value="Name", rule=MyStringCheckRule.class)
        private String name;

        @ExcelProperty("Sex")
        private String sex;
            // 基于正则表达式的字段校验
        @ExcelProperty(value="Age", regexp="^[1-4]{1}[0-9]{1}$", regexpErrorMessage="年龄必须在10-49岁之间")
        private int age;

        @ExcelProperty(value="Tel")
        private Long tel;

        @ExcelProperty("创建时间")
        private Timestamp createDate;

        @ExcelProperty(value="Name", required=true)
        private MyDataType name2;


    ... [get/set方法]
    }

####自定义校验规则

自定义校验规则必须实现ExcelRule接口

    public class MyStringCheckRule implements ExcelRule<String> {
            // 字段检查
        public void check(Object value, String columnName, String fieldName) throws ExcelContentInvalidException {
            String val = (String) value;
            System.out.println("-------->   检测的列名为  " + columnName + "， 填充的字段名为 " + fieldName );
            if(val.length() > 10){
                throw new ExcelContentInvalidException("内容超长!");
            }
        }
            // 结果修改
        public String filter(Object value, String columnName, String fieldName) {
            String val = (String) value;
            return "[[[[" + val + "]]]";
        }

    }
