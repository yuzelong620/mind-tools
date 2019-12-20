package ${package};
import java.util.List;
import java.util.ArrayList;
import com.mind.auto.msg.MessageCode;
import com.mind.core.net.msg.BaseMessage;
import com.mind.core.net.msg.IMessage;
import java.util.Date;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
<#if (importClassName?size > 0)>
	<#list importClassName as tmpName>
import ${tmpName};	
	</#list>
</#if>


/**${classDesc}
*自动生成类
*/
public class ${className?cap_first} extends BaseMessage{
<#if (fields?size > 0)>
<#list fields as field>
	/**${field.fieldDesc}*/
	<#if field.fieldStyle == 0>
	private ${field.fieldType} ${field.fieldName};
	<#elseif field.fieldStyle == 1>
	private ${field.fieldType}[] ${field.fieldName};
	<#elseif field.fieldStyle == 2>
	private ${field.fieldType} ${field.fieldName};
	<#elseif field.fieldStyle == 3>
	private ${field.fieldType}[] ${field.fieldName};
	</#if>
</#list>
	public ${className?cap_first}(){}
	<#assign ff=""/>
	<#list fields as field>
	<#if field.fieldStyle == 0>
	<#assign ff=ff+field.fieldType+" "+field.fieldName+","/>
	<#elseif field.fieldStyle == 1>
	<#assign ff=ff+field.fieldType+"[] "+field.fieldName+","/>
	<#elseif field.fieldStyle == 2>
	<#assign ff=ff+field.fieldType+" "+field.fieldName+","/>
	<#elseif field.fieldStyle == 3>
	<#assign ff=ff+field.fieldType+"[] "+field.fieldName+","/>
	
	</#if>
	</#list>
	<#assign ffs=ff?substring(0,ff?length-1)/>
	public ${className?cap_first}(${ffs}){
	  <#list fields as field>
	  this.${field.fieldName}=${field.fieldName};
	  </#list>
	}
	<#list fields as field>
	/**获取${field.fieldDesc}*/
	<#if field.fieldStyle == 0>
	public ${field.fieldType} get${field.fieldName?cap_first}(){
	<#elseif field.fieldStyle == 1>
	public ${field.fieldType}[] get${field.fieldName?cap_first}(){
	<#elseif field.fieldStyle == 2>
	public ${field.fieldType} get${field.fieldName?cap_first}(){
	<#elseif field.fieldStyle == 3>
	public ${field.fieldType}[] get${field.fieldName?cap_first}(){
	</#if>
		return this.${field.fieldName};
	}
	/**获取${field.fieldDesc}*/
	<#if field.fieldStyle == 0>
	public void set${field.fieldName?cap_first}(${field.fieldType} ${field.fieldName}){
	<#elseif field.fieldStyle == 1>
	public void set${field.fieldName?cap_first}(${field.fieldType}[] ${field.fieldName}){
	<#elseif field.fieldStyle == 2>
	public void set${field.fieldName?cap_first}(${field.fieldType} ${field.fieldName}){
	<#elseif field.fieldStyle == 3>
	public void set${field.fieldName?cap_first}(${field.fieldType}[] ${field.fieldName}){
	</#if>
		this.${field.fieldName}=${field.fieldName};
	}
	</#list>

	public boolean readImpl() {
		<#list fields as field>
		<#if field.fieldStyle == 0>
		this.${field.fieldName}=read${field.fieldType?cap_first}();
		<#elseif field.fieldStyle == 1>
		this.${field.fieldName}=read${field.fieldType?cap_first}s();
		<#elseif field.fieldStyle == 2>
		this.${field.fieldName}=(${field.fieldType?cap_first})readMessage();
		<#elseif field.fieldStyle == 3>
		BaseMessage[] ${field.fieldName}Tmp=readMessages();
		this.${field.fieldName}=new ${field.fieldType}[${field.fieldName}Tmp.length];
		if(${field.fieldName}Tmp.length>0){
			for(int i=0;i<${field.fieldName}Tmp.length;i++){
				this.${field.fieldName}[i]=(${field.fieldType}) ${field.fieldName}Tmp[i];
			}
		}
		</#if>
		</#list>
		return true;
	}
	public boolean writeImpl() {
		<#list fields as field>
		<#if field.fieldStyle == 0>
		write${field.fieldType?cap_first}(this.${field.fieldName});
		<#elseif field.fieldStyle == 1>
		write${field.fieldType?cap_first}s(this.${field.fieldName});
		<#elseif field.fieldStyle == 2>
		writeMessage(this.${field.fieldName});
		<#elseif field.fieldStyle == 3>
		writeMessages(this.${field.fieldName});
		</#if>
		</#list>
		return true;
	}
<#elseif fields?size==0>
	public ${className?cap_first}(){}
	public boolean readImpl() {
		return true;
	}
	public boolean writeImpl() {
		return true;
	}
</#if>
	public short getMessageType() {
		return MessageCode.${className?cap_first}_CODE;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}
}
<#--
<#if (subClasses?size>0)>
<#list subClasses as class>
/**${class.classDesc}
*自动生成类
*/
class ${class.className?cap_first}{
	<#list class.fields as field>
		/**${field.fieldDesc}*/
		private ${field.fieldType?cap_first} ${field.fieldName};
	</#list>
	<#list class.fields as field>
		/**获取${field.fieldDesc}*/
		public ${field.fieldType?cap_first} get${field.fieldName?cap_first}(){
			return this.${field.fieldName};
		}
	</#list>
	<#assign ff=""/>
	<#list fields as field>
	<#assign ff=ff+field.fieldType?cap_first+" "+field.fieldName+","/>
	</#list>
	<#assign ffs=ff?substring(0,ff?length-1)/>
	
	public ${className?cap_first}(${ffs}){
	  <#list fields as field>
	  this.${field.fieldName}=${field.fieldName};
	  </#list>
	}
}
</#list>
</#if>
-->