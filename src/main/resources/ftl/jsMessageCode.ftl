var MessageCode=MessageCode||{};
<#list modules as module>
/**********************************************************
 *协议id：${module.id}
 *协议号：${module.code}
 *协议名称：${module.name}
 **********************************************************/
<#list module.classes as class>
	/**${class.className?cap_first}---${class.code}---${class.classDesc}*/
MessageCode.${class.className?cap_first}_CODE=${class.code};
</#list>
</#list>
MessageCode.createMessageByBytes=function(type,bytes){
	var msg;
	switch(type){
	<#list modules as module>
	/**********************************************************
	 *协议id：${module.id}
	 *协议号：${module.code}
	 *协议名称：${module.name}
	 **********************************************************/
	<#list module.classes as class>
		case MessageCode.${class.className?cap_first}_CODE://${class.className?cap_first}---${class.code}---${class.classDesc}
			msg=${class.className?cap_first}.createByBytes(bytes); 
		break;
	</#list>
	</#list>
		default:
			cc.log("the type is not regist,type=["+type+"]");
		break;
	}
	return msg;
}
