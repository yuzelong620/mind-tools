/**
*自动生成类
*/
var ${className?cap_first}=BaseJson.extend({
<#list fields as field>
	/** ${field.fieldDesc}*/
	${field.fieldName}:null,
</#list>
	getClassName:function() {
		return "${className?cap_first}";
	}
});

