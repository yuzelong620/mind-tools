#ifndef __${className?upper_case}_H__
#define __${className?upper_case}_H__
#include "BaseMessage.h"
#include "MessageCode.h"
USING_NS_CC;
using namespace std;
/**${classDesc}
*自动生成类
*/
class ${className?cap_first}:public BaseMessage{
public:
	${className?cap_first}(){}
	<#assign ff=""/>
	<#list fields as field>
	<#if field.fieldStyle == 0>
	<#assign ff=ff+field.fieldCppType+" "+field.fieldName+","/>
	<#elseif field.fieldStyle == 1>
	<#assign ff=ff+field.fieldCppType+"* "+field.fieldName+","/>
	<#elseif field.fieldStyle == 2>
	<#assign ff=ff+field.fieldCppType+"* "+field.fieldName+","/>
	<#elseif field.fieldStyle == 3>
	<#assign ff=ff+""+CCArray+"* "+field.fieldName+","/>
	</#if>
	</#list>
	<#assign ffs=ff?substring(0,ff?length-1)/>
	${className?cap_first}(${ffs}){
	  <#list fields as field>
	  this->${field.fieldName}=${field.fieldName};
	  </#list>
	}
	<#list fields as field>
	/**获取${field.fieldCppType}  ${field.fieldDesc}*/
	<#if field.fieldStyle == 0>
	${field.fieldCppType} get${field.fieldName?cap_first}(){
	<#elseif field.fieldStyle == 1>
	${field.fieldCppType}* get${field.fieldName?cap_first}(){
	<#elseif field.fieldStyle == 2>
	${field.fieldCppType}* get${field.fieldName?cap_first}(){
	<#elseif field.fieldStyle == 3>
	CCArray* get${field.fieldName?cap_first}(){
	</#if>
		return this->${field.fieldName};
	}
	</#list>
	bool readImpl() {
		<#list fields as field>
		<#if field.fieldStyle == 0>
		this->${field.fieldName}=read${field.fieldCppType?cap_first}();
		<#elseif field.fieldStyle == 1>
		this->${field.fieldName}=read${field.fieldCppType?cap_first}s();
		<#elseif field.fieldStyle == 2>
		${field.fieldCppType}* param${field_index+1}=new ${field.fieldCppType}();
		readMessage(param${field_index+1});
		this->${field.fieldName}=param${field_index+1};
		<#elseif field.fieldStyle == 3>
		short size=readShort();
		this->${field.fieldName}=new CCArray[size];
		for(int i=0;i<size;i++){
			${field.fieldCppType}* param${field_index+1}=new ${field.fieldCppType}();
			readMessage(param${field_index+1});
			this->${field.fieldName}->addObject(param${field_index+1});
		}
		</#if>
		</#list>
		return true;
	}
	bool writeImpl() {
		<#list fields as field>
		<#if field.fieldStyle == 0>
		write${field.fieldCppType?cap_first}(this->${field.fieldName});
		<#elseif field.fieldStyle == 1>
		write${field.fieldCppType?cap_first}s(this->${field.fieldName});
		<#elseif field.fieldStyle == 2>
		writeMessage(this->${field.fieldName});
		<#elseif field.fieldStyle == 3>
		short size=this->${field.fieldName}->count();
		writeShort(size);
		for(int i=0;i<size;i++){
			${field.fieldCppType}* param${field_index+1}=this->${field.fieldName}->objectAtIndex(i);
			writeMessage(param${field_index+1});
		}
		</#if>
		</#list>
		return true;
	}
	short getType() {
		return CODE::${className}_CODE;
	}
private:
<#list fields as field>
	/**${field.fieldDesc}*/
	<#if field.fieldStyle == 0>
	${field.fieldCppType} ${field.fieldName};
	<#elseif field.fieldStyle == 1>
	${field.fieldCppType}* ${field.fieldName};
	<#elseif field.fieldStyle == 2>
	${field.fieldCppType}* ${field.fieldName};
	<#elseif field.fieldStyle == 3>
	CCArray* ${field.fieldName};
	</#if>
</#list>
};
#endif
