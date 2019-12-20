#ifndef __MESSAGECODE_H__
#define __MESSAGECODE_H__
#include "BaseMessage.h"
<#assign ff=""/>
<#list modules as module>
/**********************************************************
 *协议id：${module.id}
 *协议号：${module.code}
 *协议名称：${module.name}
 **********************************************************/
<#list module.classes as class>
	/**${class.className?cap_first}---${class.code}---${class.classDesc}*/
	<#assign ff=ff+class.className+"_CODE="+class.code+",\n"/>
</#list>
</#list>	
<#assign ffs=ff?substring(0,ff?length-3)/>	
enum CODE{
	${ffs}
};
class MessageCode{
public:
	static BaseMessage* createMessage(short type);
};
#endif