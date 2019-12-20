package com.globalgame.common;
/**
*自动生成类
*/
public interface TextCodeConstants{
<#list fields as field>
	/**${field.fieldDesc}*/
    public final static short  ${field.fieldName}=${field.fieldCode};
</#list>
}