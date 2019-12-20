<#assign ff=""/>
<#list modules as module>
<#list module.classes as class>
	<#if class.className.substring(0,3) =="SC_">
		<#assign ff=ff+"MessageCode."+class.className+"_CODE,"/>
	</#if>
</#list>
</#list>
<#assign ffs=ff?substring(0,ff?length-1)/>

package com.mind.test;
import com.mind.auto.msg.MessageCode;
import com.mind.core.config.CoreConfig;
import com.mind.core.handler.MessageHandler;
import com.mind.core.net.msg.Message;
import com.mind.common.model.Player;

public class TestHandler implements MessageHandler<Message>{

	@Override
	public void execute(Message message) {
		Player	player=(Player) message.sender();
		player.testManager.dealMessage(message);
	}
	@Override
	public short[] getTypes() {
		if(CoreConfig.getInstance().isDebug()){
			return new short[]{
					 ${ffs}
			};
		}else{
			return new short[]{};
		}
	}

}