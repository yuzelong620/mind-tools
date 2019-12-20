package xgame.tools.config;

public class DataBaseType {
	private String name;
	private String javaType;
	private String cppType;
	private DataBigType bigType;
	public DataBaseType(){}
	public DataBaseType(String name, String javaType, String cppType,
			DataBigType bigType) {
		super();
		this.name = name;
		this.javaType = javaType;
		this.cppType = cppType;
		this.bigType = bigType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJavaType() {
		return javaType;
	}
	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}
	public String getCppType() {
		return cppType;
	}
	public void setCppType(String cppType) {
		this.cppType = cppType;
	}
	public DataBigType getBigType() {
		return bigType;
	}
	public void setBigType(DataBigType bigType) {
		this.bigType = bigType;
	}
	@Override
	public String toString() {
		return "[" + name + "]";
	}
	
}
