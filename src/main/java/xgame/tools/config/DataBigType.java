package xgame.tools.config;

public class DataBigType {
	private String name;
	private int value;
	public DataBigType(){}
	public DataBigType(String name, int value) {
		super();
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "[" + name +  "]";
	}
	
}
