package com.globalgame.auto.lmsg;
public interface SysMessageCode{
public static final short SYS_SESSION=2;
<#list modules as module>
/**********************************************************
 *协议id：${module.id}
 *协议号：${module.code}
 *协议名称：${module.name}
 **********************************************************/
<#list module.classes as class>
	/**${class.className?cap_first}---${class.code}---${class.classDesc}*/
	public static final short ${class.className?cap_first}_CODE=${class.code};
</#list>
</#list>
}