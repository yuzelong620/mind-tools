package xgame.tools.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@XStreamAlias("field")
public class FieldModel {
	private String desc;
	private String name;
	private int bigType;
	private String javaType;
	private String cppType;
	public FieldModel() {
		super();
	}
	public FieldModel(int bigType, String javaType,String cppType,String name,  String desc) {
		super();
		this.desc = desc;
		this.name = name;
		this.javaType = javaType;
		this.bigType=bigType;
		this.cppType=cppType;
	}
	public String getDesc() {
		return desc;
	}
	public String getName() {
		return name;
	}
	
	public int getBigType() {
		return bigType;
	}
	public String getJavaType() {
		return javaType;
	}
	public String getCppType() {
		return cppType;
	}
	
}
