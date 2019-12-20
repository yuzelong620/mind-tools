package xgame.tools.message;

import java.util.Vector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@XStreamAlias("module")
public class ModuleModel {
	private int id; 
	private String moduleName;
	private Vector<MessageModel> messageModels=new Vector<MessageModel>();
	
	public ModuleModel() {
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public Vector<MessageModel> getMessageModels() {
		return messageModels;
	}
	public void setMessageModels(Vector<MessageModel> messageModels) {
		this.messageModels = messageModels;
	}
	@Override
	public String toString() {
		return  moduleName;
	}
	
}
