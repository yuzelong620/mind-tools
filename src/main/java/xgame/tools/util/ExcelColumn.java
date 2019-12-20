package xgame.tools.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Row;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mind.core.util.IntDoubleTuple;
import com.mind.core.util.IntTuple;
import com.mind.core.util.StringFloatTuple;
import com.mind.core.util.StringIntTuple;
import com.mind.core.util.StringUtils;
import com.mind.core.util.ThreeTuple;

public  class ExcelColumn{
	private static final Logger logger = Logger.getLogger(ExcelColumn.class);
	
	/**
	 * 用来拆分具有<..><..><..>形式的字符串正则表达式
	 */
	public static final String GROUP_SPLITER = "(?<=>)(?=<)";
	
	/**
	 * 用来拆分具有v1|v2|v3形式的字符串表达式
	 */
	public static final String ELEMENT_SPLITER = ",";
	public static final char ELEMENT_SPLITER_CHAR  = ',';
	
	
	public static final String[] STR_SEARCH_LIST = { "\\n" };
	public static final String[] STR_REPLACE_LIST = { "\n" };
	short columnIndex;//列索引，定义为short类型,因为HSSFRow#getCell方法需要传入short类型的参数
	String columnName;//列明
	String fieldName;//与该列对应的模板类型字段
	String columnType;//类型
	boolean isNumber;//是否数字类型（整形或者浮点型）
	boolean isInteger;//是否是整数类型
	boolean isFloat; //是否是浮点类型
	boolean nullable; //是否可以为空
	String defaultValue; //默认值
	boolean hashMax;   //是否有最大限制
	String maxStr;     //最大字符串
	double fMax;       //浮点数最大值
	long iMax;         //整形最大值
	boolean hashMin;    //是否有最小值
	String minStr;      //最小值字符串
	double fMin;		//浮点数最小值
	long iMin;			//整行最小值
	String valids;      //有效值列表，CSV
	String pattern;     //该列必须匹配的正则表达式
	String desc;     
	Class<?> setterParamType; //setter的参数类型
	Class<?> setterElementType; //当setter的参数类型有泛型时，如List<T>,setterElementName指出泛型参数的类型
	String javaType;
	
	private ExcelColumn(){}
	//从HSSFRow构造ExcelColumn
	public ExcelColumn(HSSFRow row) {
		this.columnIndex = (short)(row.getRowNum() - 1);
		short cellIndex = 0;
		try{
			this.columnName = getCellString(row, cellIndex++);
			this.fieldName = getCellString(row, cellIndex++);
			this.setType(getCellString(row, cellIndex++));
			this.nullable = "yes".equals(getCellString(row, cellIndex++));
			this.defaultValue = getCellString(row, cellIndex++);
			this.setMin(getCellString(row, cellIndex++));
			this.setMax(getCellString(row, cellIndex++));
			this.setValids(getCellString(row, cellIndex++));
			this.pattern = getCellString(row, cellIndex++);
			this.desc = getCellString(row, cellIndex++);
		}catch (NumberFormatException e){
			logger.error("", e);
			throw new RuntimeException("最大值或最小值无效：" + getCellString(row, cellIndex),e);
		}
	}
	
	public ExcelColumn(String columnName,String fieldName,String type,String value){
		this.columnName = columnName;
		this.fieldName = fieldName;
		this.setType(type); 
		this.defaultValue = value;
		this.nullable = false;
	}
	
	void setType(String cellStr){
		this.columnType = cellStr.toLowerCase();
		this.isInteger =this.columnType.contains("integer")
				||this.columnType.contains("long")
				||this.columnType.contains("int")
				||this.columnType.contains("short");
		this.isFloat = this.columnType.contains("float")
				||this.columnType.contains("double");
		this.isNumber = this.isInteger || this.isFloat;
		boolean isStringIntTuple=this.columnType.contains("stringint");
		boolean isListList=this.columnType.contains("list<list>");
		boolean isIntTuple=this.columnType.contains("intint");
		boolean isIntDoubleTuple=this.columnType.contains("intdouble")||this.columnType.contains("intfloat");
		boolean isList=this.columnType.contains("list");
		boolean isThreeTuple = this.columnType.contains("three");
		boolean isStringFloat = this.columnType.contains("stringfloat");
		if(isList){
			if(isListList){
				setterElementType=List.class;
				this.javaType="List<List>";
			}else if(isStringFloat){
				setterElementType = StringFloatTuple.class;
				this.javaType="List<StringFloatTuple>";
			}else if(isThreeTuple){
				setterElementType = ThreeTuple.class;
				this.javaType="List<ThreeTuple>";
			}else if(isStringIntTuple){
				setterElementType=StringIntTuple.class;
				this.javaType="List<StringIntTuple>";
			}else if(isIntDoubleTuple){
				setterElementType=IntDoubleTuple.class;
				this.javaType="List<IntDoubleTuple>";
			}else if(isIntTuple){
				setterElementType=IntTuple.class;
				this.javaType="List<IntTuple>";
			}else if(isInteger){
				setterElementType = Integer.class;
				this.javaType="List<Integer>";
			}else if(isFloat){
				setterElementType = Double.class;
				this.javaType="List<Double>";
			}else{
				setterElementType = String.class;
				this.javaType="List<String>";
			}
			setterParamType=List.class;
		}else if(isStringFloat){
			setterParamType = StringFloatTuple.class;
			this.javaType="StringFloatTuple";
		}else if(isThreeTuple){
			setterParamType = ThreeTuple.class;
			this.javaType="ThreeTuple";
		} else if(isStringIntTuple){
			setterParamType=StringIntTuple.class;
			this.javaType="StringIntTuple";
		}else if(isInteger){
			setterParamType = Integer.class;
			this.javaType="Integer";
		}else if(isFloat){
			setterParamType = Double.class;
			this.javaType="Double";
		}else{
			setterParamType = String.class;
			this.javaType="String";
		}
	}
	
	void setMax(String cellStr){
		this.maxStr = cellStr;
		if(!this.maxStr.isEmpty()){
			this.hashMax = true;
			if(this.isInteger){
				this.iMax = Long.parseLong(this.maxStr);
			}
			if(this.isFloat){
				this.fMax = Double.parseDouble(this.maxStr);
			}
		}
	}
	
	void setMin(String cellStr){
		this.minStr = cellStr;
		if(!this.minStr.isEmpty()){
			this.hashMin = true;
			if(this.isInteger){
				this.iMin = Long.parseLong(this.minStr);
			}
			if(this.isFloat){
				this.fMin = Double.parseDouble(this.minStr);
			}
		}
	}
	
	void setValids(String cellStr){
		this.valids = cellStr;
		if(!valids.isEmpty()){
			valids = ','+valids+',';
		}
	}
	
	public void set(JSONObject tmpl, HSSFRow row)throws Exception{
		
		String cellStr = getCellString(row,this.columnIndex);
		cellStr = validate(cellStr, row.getRowNum() + 1);
		
//		if(cellStr.isEmpty() && this.isNumber){
//			return;
//		}
		if(cellStr.isEmpty()){
			return;
		}
		try {
		Object val = convert(cellStr);tmpl.put(this.fieldName, val);
		}
//		short columnIndex;//列索引，定义为short类型,因为HSSFRow#getCell方法需要传入short类型的参数
//		String columnName;//列明
//		String fieldName;//与该列对应的模板类型字段
//		String columnType;//类型
		catch(RuntimeException e) {
			System.out.println("列值："+cellStr+" ,columnIndex:"+columnIndex+",columnName:"+columnName+",fieldName:"+fieldName+",columnType:"+columnType);
			throw  e;
		}
		
	}
	
	/**
	 * 将字符串转换为相应的数据类型
	 * @param cellStr
	 * @return
	 */
	public Object convert(String cellStr) throws Exception{
		if(this.setterParamType == String.class){
			String str = org.apache.commons.lang.StringUtils.replaceEachRepeatedly(cellStr, STR_SEARCH_LIST, STR_REPLACE_LIST);
			return str;
		}
		if(this.setterParamType == Integer.TYPE || this.setterParamType == Integer.class){
			return (int)Math.round(Double.parseDouble(cellStr));
		}
		if(this.setterParamType == Short.TYPE || this.setterParamType == Short.class){
			return Short.parseShort(cellStr);
		}
		if(this.setterParamType == Byte.TYPE || this.setterParamType == Byte.class){
			return Byte.parseByte(cellStr);
		}
		if(this.setterParamType == Long.TYPE || this.setterParamType == Long.class){
			return Long.parseLong(cellStr);
		}
		if(this.setterParamType == Float.TYPE || this.setterParamType == Float.class){
			return Float.parseFloat(cellStr);
		}
		if(this.setterParamType == Double.TYPE || this.setterParamType == Double.class){
			return Double.parseDouble(cellStr);
		}		
		if(this.setterParamType == Boolean.TYPE || this.setterParamType == Boolean.class){
			return "1".equals(cellStr);
		}
		if(this.setterParamType==StringIntTuple.class){
			return toStringIntTuple(cellStr);
		}
		if(this.setterParamType==ThreeTuple.class){
			return toThreeTuple(cellStr);
		}
		if(this.setterParamType==StringFloatTuple.class){
			return toStringFloatTuple(cellStr);
		}
		if(this.setterParamType == List.class){
			if(this.setterElementType==List.class){
				return toListList(cellStr);
			}
			if(this.setterElementType==StringIntTuple.class){
				return toStringIntTupleList(cellStr);
			}else if(this.setterElementType == IntDoubleTuple.class){
				return toIntDoubleTupleList(cellStr);
			}else if(this.setterElementType == IntTuple.class){
				return toIntTupleList(cellStr);
			}else if(this.setterElementType == Integer.class){
				return toIntList(cellStr);
			}else if(this.setterElementType == Double.class){
				return toDoubelList(cellStr);
			}else if(this.setterElementType == ThreeTuple.class){
				return toThreeTupleList(cellStr);
			}else if(this.setterElementType == StringFloatTuple.class){
				return toStringFloatTupleList(cellStr);
			}else{
				return toStringList(cellStr);
			}
		}
		if(this.setterParamType == Set.class){
			if(this.setterElementType.equals("Integer")){
				return toIntSet(cellStr);
			}
		}
		if(this.setterParamType.isArray()){
			if(this.setterParamType.getComponentType() == int.class){
				return toIntArray(cellStr);
			}
		}
		if(this.setterParamType == Map.class){
			if(this.setterElementType.equals("Int|Int")){
				return toIntIntMap(cellStr);
			}
		}
		return null;
	}

	/**
	 * @param cellStr
	 * @return
	 */
	private Map toIntIntMap(String str) {
		Map<Integer,Integer> map = Maps.newHashMap();
		if(!str.isEmpty()){
			for(String pair : str.split(GROUP_SPLITER)){
				int key = Integer.parseInt(pair.substring(1,pair.indexOf(ELEMENT_SPLITER_CHAR)));
				int value = Integer.parseInt(pair.substring(pair.indexOf(ELEMENT_SPLITER_CHAR)+1, pair.length() -1));
				map.put(key, value);
			}
		}
		return map;
	}

	/**
	 * 将123|456|789形式的字符串转换为int[]
	 * @param cellStr
	 * @return
	 */
	private int[] toIntArray(String str) {
		return StringHelper.getIntList(str.split(ELEMENT_SPLITER));
	}

	/**
	 * 将123|456|789形式的字符串转换为Set<Integer>
	 * @param cellStr
	 * @return
	 */
	private Set<Integer> toIntSet(String str) {
		Set<Integer> ints = Sets.newHashSet();
		if(!str.isEmpty()){
			for(String intStr : str.split(ELEMENT_SPLITER)){
				ints.add(Integer.parseInt(intStr));
			}
		}
		return ints;
	}
	private static String getCellString(HSSFRow row, short cellIdx){
		return PoiUtil.getStringValue(row.getCell(cellIdx, Row.RETURN_NULL_AND_BLANK));
	}
	/**
	 * 将<key,value><key,value>形式的字符串转为List<IntTuple>
	 * @param cellStr
	 * @return
	 */
	private List<IntTuple> toIntTupleList(String str) {
		List<IntTuple> tuples = Lists.newArrayList();
		if(!str.isEmpty()){
			for(String pair : str.split(GROUP_SPLITER)){
				tuples.add(toIntTuple(pair));
			}
		}
		return tuples;
	}
	/**
	 * 将<key,value><key,value>形式的字符串转为List<IntTuple>
	 * @param cellStr
	 * @return
	 */
	private List<IntDoubleTuple> toIntDoubleTupleList(String str) {
		List<IntDoubleTuple> tuples = Lists.newArrayList();
		if(!str.isEmpty()){
			for(String pair : str.split(GROUP_SPLITER)){
				tuples.add(toIntDoubleTuple(pair));
			}
		}
		return tuples;
	}
	/**
	 * 将<key,value><key,value>形式的字符串转为List<IntTuple>
	 * @param cellStr
	 * @return
	 */
	private List<StringIntTuple> toStringIntTupleList(String str) throws Exception{
		List<StringIntTuple> tuples = Lists.newArrayList();
		if(!str.isEmpty()){
			for(String pair : str.split(GROUP_SPLITER)){
				tuples.add(toStringIntTuple(pair));
			}
		}
		return tuples;
	}
	/**
	 * 将<key|value>形式的字符串转为IntTuple
	 * @param pair
	 * @return
	 */
	private IntTuple toIntTuple(String str) {
		if(str.isEmpty()){
			return null;
		}
		int key = Integer.parseInt(str.substring(1,str.indexOf(ELEMENT_SPLITER_CHAR)));
		int value = Integer.parseInt(str.substring(str.indexOf(ELEMENT_SPLITER_CHAR)+1, str.length() -1));
		return new IntTuple(key, value);
		
	}
	
	public static void main(String[] args) {
		String str="<12,23>";
		IntTuple  obj=new  ExcelColumn().toIntTuple(str);
		System.out.println(obj.toString());
		
		String sql="<123,1><123213,23>";
		for(String s:sql.split(GROUP_SPLITER)) {
			 System.out.println(s);
		}
	}
	/**
	 * 将<key,value>形式的字符串转为IntTuple
	 * @param pair
	 * @return
	 */
	private IntDoubleTuple toIntDoubleTuple(String str) {
		if(str.isEmpty()){
			return null;
		}
		int key = Integer.parseInt(str.substring(1,str.indexOf(ELEMENT_SPLITER_CHAR)));
		double value =Double.parseDouble(str.substring(str.indexOf(ELEMENT_SPLITER_CHAR)+1, str.length() -1));
		return new IntDoubleTuple(key, value);
		
	}
	/**
	 * 将<key,value>形式的字符串转为IntTuple
	 * @param pair
	 * @return
	 */
	private StringIntTuple toStringIntTuple(String str) throws Exception{
		if(str.isEmpty()){
			return null;
		}
		String key = str.substring(1,str.indexOf(ELEMENT_SPLITER_CHAR));
		int value =Integer.parseInt(str.substring(str.indexOf(ELEMENT_SPLITER_CHAR)+1, str.length() -1));
		return new StringIntTuple(key, value);
		
	}
	/**
	 * 将<key,float>形式转成List<StringFloatTuple>
	 */
	private List<StringFloatTuple> toStringFloatTupleList(String str){
		if(str.isEmpty()){
			return null;
		}
		List<StringFloatTuple> list = new ArrayList<StringFloatTuple>();
		for(String pair : str.split(GROUP_SPLITER)){
			list.add(toStringFloatTuple(pair));
		}
		return list;
	}
	/**
	 * 将<key,value,probability>形式转成List<IntThreeTuple>
	 */
	private List<ThreeTuple> toThreeTupleList(String str){
		if(str.isEmpty()){
			return null;
		}
		List<ThreeTuple> list = new ArrayList<ThreeTuple>();
		for(String pair : str.split(GROUP_SPLITER)){
			list.add(toThreeTuple(pair));
		}
		return list;
	}
	
	/**
	 * 将<key,float>形式转成StringFloatTuple
	 */
	private StringFloatTuple toStringFloatTuple(String str){
		if(str.isEmpty()){
			return null;
		}
		String pair=str.substring(1, str.length()-1);
		
		String [] tuples = pair.split(ELEMENT_SPLITER);
		return new StringFloatTuple(tuples[0],Float.valueOf(tuples[1]));
	}
	/**
	 * 将<key,value,probability>形式转成IntThreeTuple
	 */
	private ThreeTuple toThreeTuple(String str){
		if(str.isEmpty()){
			return null;
		}
		String pair=str.substring(1, str.length()-1);
		
		String [] tuples = pair.split(ELEMENT_SPLITER);
		return new ThreeTuple(tuples[0],Integer.valueOf(tuples[1]),Integer.valueOf(tuples[2]));
	}
	
	/**
	 * 将<x,y,z><a,b,c>形式的字符串转为List<String>
	 * @param cellStr
	 * @return
	 */
	private List<List> toListList(String cellStr) {
		List<List> tuples = Lists.newArrayList();
		if(!cellStr.isEmpty()){
			for(String pair : cellStr.split(GROUP_SPLITER)){
				pair=pair.substring(1, pair.length()-1);
				List list= Arrays.asList(pair.split(ELEMENT_SPLITER));
				tuples.add(list);
			}
		}
		return tuples;
	}
	/**
	 * 将<x,y,z>形式的字符串转为List<String>
	 * @param cellStr
	 * @return
	 */
	private List<String> toStringList(String cellStr) {
		cellStr=cellStr.substring(1, cellStr.length()-1);
		return Arrays.asList(cellStr.split(ELEMENT_SPLITER));
	}

	/**
	 * 将<123,456,789>形式的字符串转换为List<Integer>
	 * @param cellStr
	 * @return
	 */
	private List<Integer> toIntList(String str) {
		str=str.substring(1, str.length()-1);
		List<Integer> ints = Lists.newArrayList();
		if(!str.isEmpty()){
			for(String intStr : str.split(ELEMENT_SPLITER)){
				ints.add(Integer.parseInt(intStr));
			}
		}
		return ints;
	}
	/**
	 * 将<123,456,789>形式的字符串转换为List<Integer>
	 * @param cellStr
	 * @return
	 */
	private List<Double> toDoubelList(String str) {
		str=str.substring(1, str.length()-1);
		List<Double> doubles = Lists.newArrayList();
		if(!str.isEmpty()){
			for(String intStr : str.split(ELEMENT_SPLITER)){
				doubles.add(Double.parseDouble(intStr));
			}
		}
		return doubles;
	}
	/**
	 * 检验数据
	 * @param val
	 * @param rowIdx
	 * @return
	 */
	private String validate(String val, int rowIdx) {
		//空检查
		if(!this.nullable && val.isEmpty() && StringUtils.isEmpty(this.defaultValue)){
			String msg = String.format("[行:%d, 列:%s(%s)] >> 不能为空", rowIdx, numToAlpha(this.columnIndex),this.columnName);
			throw new RuntimeException(msg);
		}
		
		//设置默认值
		if(!this.nullable && val.isEmpty()){
			val = this.defaultValue;
		}
		
		//匹配正则表达式
		if(!this.pattern.isEmpty() && !val.isEmpty() && !val.matches(this.pattern)){
			String msg = String .format("[行:%d, 列:%s(%s)] >> [%s]无法匹配[%s]",  rowIdx, numToAlpha(this.columnIndex),this.columnName, val, this.pattern);
			throw new RuntimeException(msg);
		}
		
		//有效值检查
		if(!this.valids.isEmpty() && !val.isEmpty() && !this.valids.contains(','+val+',')){
			String msg = String .format("[行:%d, 列:%s(%s)] >> [%s]无效枚举值，有效值为:[%s]",  rowIdx, numToAlpha(this.columnIndex),this.columnName, val, this.valids.substring(1,this.valids.length()-1));
			throw new RuntimeException(msg);
		}
		//数字用公式算的检查
		if(this.columnType.equalsIgnoreCase("integer")||
				this.columnType.equalsIgnoreCase("long")||
				this.columnType.equalsIgnoreCase("int")||
				this.columnType.equalsIgnoreCase("short")||
				this.columnType.equalsIgnoreCase("float")||
				this.columnType.equalsIgnoreCase("double")){
			if(!isNumeric(val)){
				String msg = String .format("[行:%d, 列:%s(%s)] >> [%s]无效值，此处应该是数字",  rowIdx, numToAlpha(this.columnIndex),this.columnName, val);
				throw new RuntimeException(msg);
			}
		}
		//最大值最小值检查
		try{
			if(this.hashMax && ((this.isInteger && Long.parseLong(val) > this.iMax) || 
					(this.isFloat && Double.parseDouble(val) > this.fMax))){
				String msg = String .format("[行:%d, 列:%s(%s)] >> [%s]超出了最大值[%s]",  rowIdx, numToAlpha(this.columnIndex),this.columnName, val, this.maxStr);
				throw new RuntimeException(msg);
			}
			if(this.hashMin && ((this.isInteger && Long.parseLong(val) < this.iMin) || 
					(this.isFloat && Double.parseDouble(val) < this.fMin))){
				String msg = String .format("[行:%d, 列:%s(%s)] >> [%s]超出了最小值[%s]",  rowIdx, numToAlpha(this.columnIndex),this.columnName, val, this.maxStr);
				throw new RuntimeException(msg);
			}
		}catch(NumberFormatException e){
			logger.error("", e);
			String msg = String.format("[行:%d, 列:%s(%s)] >> [%s]无法转换为[%s]",  rowIdx, numToAlpha(this.columnIndex),this.columnName, val, this.isInteger ? "整数" : "浮点数");
			throw new RuntimeException(msg);
		}
		
		return val;
	}
	private boolean isNumeric(String str){
		if(StringUtils.isEmpty(str)){
			return true;
		}
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	} 

	/**
	 * 将大于0小于26*27的数字转换为字母
	 * 0=>A,...,25>Z，26=>AZ,...,20*27-1=>ZZ
	 * @param num
	 * @return
	 */
	private String numToAlpha(int num) {
		if(num < 0 || num >= 26*27){
			return "" + num;
		}
		if(num >= 26){
			return "" + ((char)(num/26 -1 + 65)) + ((char)(num % 26 + 65));
		}
		return "" + ((char)(num + 65));
	}


	public short getColumnIndex() {
		return columnIndex;
	}


	public String getColumnName() {
		return columnName;
	}


	public String getFieldName() {
		return fieldName;
	}


	public String getColumnType() {
		return columnType;
	}


	public boolean isNumber() {
		return isNumber;
	}


	public boolean isInteger() {
		return isInteger;
	}


	public boolean isFloat() {
		return isFloat;
	}


	public boolean isNullable() {
		return nullable;
	}


	public String getDefaultValue() {
		return defaultValue;
	}


	public boolean isHashMax() {
		return hashMax;
	}


	public String getMaxStr() {
		return maxStr;
	}


	public double getfMax() {
		return fMax;
	}


	public long getiMax() {
		return iMax;
	}


	public boolean isHashMin() {
		return hashMin;
	}


	public String getMinStr() {
		return minStr;
	}


	public double getfMin() {
		return fMin;
	}


	public long getiMin() {
		return iMin;
	}


	public String getValids() {
		return valids;
	}


	public String getPattern() {
		return pattern;
	}


	public Class<?> getSetterParamType() {
		return setterParamType;
	}


	public Class<?> getSetterElementType() {
		return setterElementType;
	}


	public String getDesc() {
		return desc;
	}


	public String getJavaType() {
		return javaType;
	}


	

	
}