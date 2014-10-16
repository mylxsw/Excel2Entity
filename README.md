Excel2Entity
============

<div class="content">
							<p>该类库（Excel2Entity, 简称e2e）对POI对xls文件的读取进行了封装，实现了批量导入Excel中的数据时自动将数据填充到实体列表的功能， 支持字段类型校验以及自定义校验规则，可以对Excel中的数据类型合法性进行校验，同时e2e提供了一定的扩展性，可以自定义校验规则以及自定义实体对象字段类型实现更加负责的校验以及字段类型填充。</p><ul class=" list-paddingleft-2" style="list-style-type: disc;"><li><p><span style="font-size: 20px;"><strong>Excel2Entity依赖于Apache POI类库。</strong></span></p></li><li><p><span style="font-size: 20px;"><strong>项目基于Maven。</strong></span></p></li><li><p><span style="font-size: 20px;"><strong>项目托管在Github。</strong></span><br/></p></li></ul><pre class="brush:html;toolbar:false">https://github.com/mylxsw/Excel2Entity.git</pre><p><br/></p><ul class=" list-paddingleft-2" style="list-style-type: disc;"><li><p><span style="font-size: 20px;"><strong>使用方法：</strong></span></p></li></ul><p><br/></p><p>&nbsp;&nbsp;&nbsp;&nbsp;<span style="font-size: 18px;">1. 普通实体创建</span><br/></p><p>&nbsp; &nbsp;&nbsp;</p><pre class="brush:java;toolbar:false">ExcelHelper&nbsp;eh&nbsp;=&nbsp;ExcelHelper.readExcel(&quot;111.xls&quot;);
List&lt;Demo&gt;&nbsp;entitys&nbsp;&nbsp;=&nbsp;null;
try&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;entitys&nbsp;=&nbsp;eh.toEntitys(Demo.class);
&nbsp;&nbsp;&nbsp;&nbsp;for&nbsp;(Demo&nbsp;d&nbsp;:&nbsp;entitys)&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(d.toString());
&nbsp;&nbsp;&nbsp;&nbsp;}
}&nbsp;catch&nbsp;(ExcelParseException&nbsp;e)&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(e.getMessage());
}&nbsp;catch&nbsp;(ExcelContentInvalidException&nbsp;e)&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(e.getMessage());
}&nbsp;catch&nbsp;(ExcelRegexpValidFailedException&nbsp;e)&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(e.getMessage());
}</pre><p>&nbsp;&nbsp;<span style="font-size: 18px;">&nbsp;&nbsp;2. 注册新的字段类型</span><br/></p><p>&nbsp;&nbsp;&nbsp;&nbsp;注册的新的字段类型类必须实现ExcelType抽象类。</p><pre class="brush:java;toolbar:false">ExcelHelper.registerNewType(MyDataType.class);</pre><p>&nbsp;&nbsp;&nbsp;<span style="font-size: 18px;">&nbsp;3. 实体对象</span><br/></p><p>&nbsp;&nbsp;&nbsp;&nbsp;实体类必须标注@ExcelEntity注解， 同时需要填充的字段标注@ExcelProperty注解<br/></p><p>&nbsp;&nbsp;&nbsp;&nbsp;</p><pre class="brush:java;toolbar:false">@ExcelEntity
public&nbsp;class&nbsp;Demo&nbsp;{
	@ExcelProperty(value=&quot;Name&quot;,&nbsp;rule=MyStringCheckRule.class)
	private&nbsp;String&nbsp;name;

	@ExcelProperty(&quot;Sex&quot;)
	private&nbsp;String&nbsp;sex;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//&nbsp;基于正则表达式的字段校验
	@ExcelProperty(value=&quot;Age&quot;,&nbsp;regexp=&quot;^[1-4]{1}[0-9]{1}$&quot;,&nbsp;regexpErrorMessage=&quot;年龄必须在10-49岁之间&quot;)
	private&nbsp;int&nbsp;age;

	@ExcelProperty(value=&quot;Tel&quot;)
	private&nbsp;Long&nbsp;tel;
	
	@ExcelProperty(&quot;创建时间&quot;)
	private&nbsp;Timestamp&nbsp;createDate;
	
	@ExcelProperty(value=&quot;Name&quot;,&nbsp;required=true)
	private&nbsp;MyDataType&nbsp;name2;
	

...&nbsp;[get/set方法]
}</pre><p><span style="font-size: 18px;">&nbsp;&nbsp;&nbsp;&nbsp;4. 自定义校验规则</span><br/></p><p>&nbsp;&nbsp;&nbsp;&nbsp;自定义校验规则必须实现ExcelRule接口</p><pre class="brush:java;toolbar:false">public&nbsp;class&nbsp;MyStringCheckRule&nbsp;implements&nbsp;ExcelRule&lt;String&gt;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//&nbsp;字段检查
	public&nbsp;void&nbsp;check(Object&nbsp;value,&nbsp;String&nbsp;columnName,&nbsp;String&nbsp;fieldName)&nbsp;throws&nbsp;ExcelContentInvalidException&nbsp;{
		String&nbsp;val&nbsp;=&nbsp;(String)&nbsp;value;
		System.out.println(&quot;--------&gt;&nbsp;&nbsp;&nbsp;检测的列名为&nbsp;&nbsp;&quot;&nbsp;+&nbsp;columnName&nbsp;+&nbsp;&quot;，&nbsp;填充的字段名为&nbsp;&quot;&nbsp;+&nbsp;fieldName&nbsp;);
		if(val.length()&nbsp;&gt;&nbsp;10){
			throw&nbsp;new&nbsp;ExcelContentInvalidException(&quot;内容超长!&quot;);
		}
	}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//&nbsp;结果修改
	public&nbsp;String&nbsp;filter(Object&nbsp;value,&nbsp;String&nbsp;columnName,&nbsp;String&nbsp;fieldName)&nbsp;{
		String&nbsp;val&nbsp;=&nbsp;(String)&nbsp;value;
		return&nbsp;&quot;[[[[&quot;&nbsp;+&nbsp;val&nbsp;+&nbsp;&quot;]]]&quot;;
	}

}</pre><p><br/></p>                        <p class="banquan"> 转载请注明来源:  <a href="http://aicode.cc/article/319.html">http://aicode.cc/article/319.html</a> </p>
		</div>
