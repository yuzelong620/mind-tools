package xgame.cocostudio.model;

import java.util.Date;

public class LabelTextModel {
	private String jsonName;
	private String className;
	private String fieldName;
	private String en;
	private String ch;
	private long lastModifyTimeValue;
	private String lastModifyTime;
	public LabelTextModel(String jsonName, String className, String fieldName,
			String en,long lastModifyTimeValue,String lastModifyTime) {
		super();
		this.jsonName = jsonName;
		this.className = className;
		this.fieldName = fieldName;
		this.en = en;
		this.lastModifyTimeValue=lastModifyTimeValue;
		this.lastModifyTime=lastModifyTime;
	}
	public String getJsonName() {
		return jsonName;
	}
	public void setJsonName(String jsonName) {
		this.jsonName = jsonName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getEn() {
		return en;
	}
	public void setEn(String en) {
		this.en = en;
	}
	public String getCh() {
		return ch;
	}
	public void setCh(String ch) {
		this.ch = ch;
	}
	public String getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(String lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	public long getLastModifyTimeValue() {
		return lastModifyTimeValue;
	}
	public void setLastModifyTimeValue(long lastModifyTimeValue) {
		this.lastModifyTimeValue = lastModifyTimeValue;
	}
	
}
