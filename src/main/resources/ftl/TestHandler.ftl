<#assign ff="MessageCode.SYS_SESSION,"/>
<#list modules as module>
<#list module.classes as class>
		<#assign ff=ff+"MessageCode."+class.className+"_CODE,"/>
</#list>
</#list>
<#assign ffs=ff?substring(0,ff?length-1)/>
package com.mind.test;
import com.mind.auto.msg.MessageCode;
import com.mind.core.handler.MessageHandler;
import com.mind.core.net.msg.Message;
import com.mind.common.ClientData;
import com.mind.common.model.Player;

public class TestHandler implements MessageHandler<Message> {
	public TestHandler() {
	}
	public void execute(Message message) {
		Player player = (Player) message.sender();
		if(ClientData.testType == 0){//压力测试
			player.bacthTestManager.dealMessage(message);
		}else if(ClientData.testType == 1){//gm
			player.gmTestManager.dealMessage(message);
		}else if(ClientData.testType == 2){//普通测试
			player.testManager.dealMessage(message);
		}
	}
	@Override
	public short[] getTypes() {
		return new short[] {
		  ${ffs}
		};
	}

}