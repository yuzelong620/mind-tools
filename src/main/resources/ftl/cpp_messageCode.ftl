#include "MessageCode.h"
<#list modules as module>
	<#list module.classes as class>
#include "${class.className?cap_first}.h"
	</#list>
</#list>
BaseMessage* MessageCode::createMessage(short type){
	BaseMessage* msg;
	switch(type){
	<#list modules as module>
		<#list module.classes as class>
		case ${class.className}_CODE:	/**${class.className?cap_first}---${class.code}---${class.classDesc}*/
		msg=new ${class.className?cap_first}();
		break;
		</#list>
	</#list>
		default:
			CCLOG("this type is not have message;[code=%d]",type);
		break;
		}
	return msg;
}
