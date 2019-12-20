package xgame.tools.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
@XStreamAlias("message")
public class MessageModel {
	private String name;
	private String desc;
	private List<FieldModel> fields=new ArrayList<FieldModel>();
	private short messageType;
	private int moduleId;
	@XStreamOmitField
	private String moduleName;
	@XStreamOmitField
	private int deep;
	public int getDeep() {
		return deep;
	}

	public void setDeep(int deep) {
		this.deep = deep;
	}

	public MessageModel() {
		super();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setFields(List<FieldModel> fields) {
		this.fields = fields;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public List<FieldModel> getFields() {
		return fields;
	}
	
	public short getMessageType() {
		return messageType;
	}

	public void setMessageType(short messageType) {
		this.messageType = messageType;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}


	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public static Vector<Vector<String>> getColumnsData(MessageModel messageModel) {
		Vector<Vector<String>> columnsData = new Vector<Vector<String>>();
		for(FieldModel model:messageModel.getFields()){
			getRow(model, columnsData);
		}
		return columnsData;
	}
	private static Vector<Vector<String>> getRow(FieldModel fieldModel,Vector<Vector<String>> columnsData){
		Vector<String> strings=new Vector<String>();
		strings.add(fieldModel.getJavaType().toString());
		strings.add(fieldModel.getName());
		strings.add(fieldModel.getDesc());
		columnsData.add(strings);
		return columnsData;
	}

	@Override
	public String toString() {
		return "["+name + "---" + desc + "]";
	}
	
}
