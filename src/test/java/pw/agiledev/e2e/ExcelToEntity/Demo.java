package pw.agiledev.e2e.ExcelToEntity;

import java.sql.Timestamp;

import pw.agiledev.e2e.annotation.ExcelEntity;
import pw.agiledev.e2e.annotation.ExcelProperty;
/**
 * 功能说明： 测试例子
 * 参数说明：
 * @author 管宜尧
 * 2013-11-28 下午8:05:04
 */
@ExcelEntity
public class Demo {
	@ExcelProperty("Name")
	private String name;

	@ExcelProperty("Sex")
	private String sex;

	@ExcelProperty("Age")
	private int age;

	@ExcelProperty("Tel")
	private Long tel;
	
	@ExcelProperty("创建时间")
	private Timestamp createDate;
	
	@ExcelProperty(value="Name", required=true)
	private MyDataType name2;
	

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Long getTel() {
		return tel;
	}

	public void setTel(Long tel) {
		this.tel = tel;
	}

	public MyDataType getName2() {
		return name2;
	}

	public void setName2(MyDataType name2) {
		this.name2 = name2;
	}

	@Override
	public String toString() {
		return "Demo [name=" + name + ", sex=" + sex + ", age=" + age
				+ ", tel=" + tel + ", createDate=" + createDate + ", name2="
				+ name2 + "]";
	}
}
