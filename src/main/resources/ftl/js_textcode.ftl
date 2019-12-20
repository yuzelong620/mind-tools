/**
*自动生成类
*/
bs.TextCode={
	<#assign ff=""/>
<#list fields as field>
	<#assign ff=ff+"\n\t"+"/**"+field.fieldDesc+"*/"+"\n\t"+field.fieldName+":"+field.fieldCode+","/>
</#list>
<#assign ffs=ff?substring(0,ff?length-1)/>
	${ffs}
}