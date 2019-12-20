/**${classDesc}
*自动生成类
*/
var ${className?cap_first}=BaseMessage.extend({
<#if (fields?size > 0)>
<#list fields as field>
	/**${field.fieldDesc}*/
	<#if field.fieldStyle == 0>
	_${field.fieldName}:null,
	<#elseif field.fieldStyle == 1>
	_${field.fieldName}:null,
	<#elseif field.fieldStyle == 2>
	_${field.fieldName}:null,
	<#elseif field.fieldStyle == 3>
	_${field.fieldName}:null,
	</#if>
</#list>
	<#list fields as field>
	/**获取${field.fieldCppType}  ${field.fieldDesc}*/
	get${field.fieldName?cap_first}:function(){
		return this._${field.fieldName};
	},
	</#list>
	_read:function() {
		<#list fields as field>
		<#if field.fieldStyle == 0>
		this._${field.fieldName}=this._read${field.fieldType?cap_first}();
		<#elseif field.fieldStyle == 1>
		this._${field.fieldName}=this._read${field.fieldType?cap_first}s();
		<#elseif field.fieldStyle == 2>
		this._${field.fieldName}=this._readMessage();
		<#elseif field.fieldStyle == 3>
		this._${field.fieldName}=this._readMessages();
		</#if>
		</#list>
	},
	_write:function() {
		<#list fields as field>
		<#if field.fieldStyle == 0>
		this._write${field.fieldCppType?cap_first}(this._${field.fieldName});
		<#elseif field.fieldStyle == 1>
		this._write${field.fieldCppType?cap_first}s(this._${field.fieldName});
		<#elseif field.fieldStyle == 2>
		this._writeMessage(this._${field.fieldName});
		<#elseif field.fieldStyle == 3>
		this._writeMessages(this._${field.fieldName});
		</#if>
		</#list>
	},
	getType:function() {
		return MessageCode.${className}_CODE;
	}
});
<#assign ff=""/>
	<#list fields as field>
	<#assign ff=ff+"\n\t"+field.fieldName+","/>
	</#list>
	<#assign ffs=ff?substring(0,ff?length-1)/>
${className?cap_first}.create=function(${ffs}){
	var msg=new ${className?cap_first}();
	<#list fields as field>
	msg._${field.fieldName}=${field.fieldName};
	</#list>
	return msg;
}
<#elseif fields?size==0>
	_read:function() {
	},
	_write:function() {
	},
	getType:function() {
		return MessageCode.${className}_CODE;
	}
});
${className?cap_first}.create=function(){
	var msg=new ${className?cap_first}();
	return msg;
}
</#if>
/**通过字节来生成消息---读消息*/
${className?cap_first}.createByBytes=function(bytes){
	var msg=new ${className?cap_first}();
	msg._buf=bytes;
	msg.read();
	return msg;
}