package com.globalgame.auto.json;
import java.util.List;
import com.mind.core.util.StringIntTuple;
import com.mind.core.util.IntDoubleTuple;
import com.mind.core.util.IntTuple;
import com.mind.core.util.ThreeTuple;
import com.mind.core.util.StringFloatTuple;

/**
*自动生成类
*/
public class ${className?cap_first}{
<#list fields as field>
	/** ${field.fieldDesc}*/
	private ${field.fieldType}	${field.fieldName};
</#list>

<#list fields as field>
	/** ${field.fieldDesc}*/
	<#if field.fieldIsText==1  >
	<#if field.fieldIsList==1  >
	public ${field.fieldType?cap_first} get${field.fieldName?cap_first}(int language){
		return WorldUtils.getNewListString(this.${field.fieldName},language);
	<#else>
	public ${field.fieldType?cap_first} get${field.fieldName?cap_first}(int language){
		return WorldUtils.getNewString(this.${field.fieldName},language);
	</#if>
	<#else>
	public ${field.fieldType?cap_first} get${field.fieldName?cap_first}(){
		return this.${field.fieldName};
	</#if>
	}
</#list>
<#list fields as field>
	/**${field.fieldDesc}*/
	public void set${field.fieldName?cap_first}(${field.fieldType} ${field.fieldName}){
		this.${field.fieldName} = ${field.fieldName};
	}
</#list>
}