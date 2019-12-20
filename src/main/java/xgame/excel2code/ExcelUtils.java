/**
 * 
 */
package xgame.excel2code;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mind.core.util.StringUtils;
import com.mind.xgame.tools.config.ToolsConf;
import com.mind.xgame.tools.util.ExcelColumn;
import com.mind.xgame.tools.util.JsonUtils;
import com.mind.xgame.tools.util.PoiUtil;

import freemarker.template.Template;

/**
 * excel工具类
 * @author ninglong
 *
 */
public class ExcelUtils {
	private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
	
	public final static String jsTemplate = "excel2js.ftl";
	public final static String javaTemplate = "excel2java.ftl";
	
	public static void main(String[] args) {
		String s="99901_player.xls";
		s=s.substring(s.lastIndexOf('_')+1,s.length() );
		System.out.println(s); 
	}
	
	
	/**
	 * 读取列
	 */
	public static List<ExcelColumn> readSchema(String tmpPath){
		File tmplFile = new File(tmpPath);
		String typeName = subTypeString(tmplFile.getName());
		String schemaPath = tmplFile.getParentFile().getAbsolutePath()+"/schema/schema_"+typeName;
		FileInputStream in = null;
		try {
			in = new FileInputStream(schemaPath);
			HSSFSheet sheet = new HSSFWorkbook(in).getSheetAt(0);
			List<ExcelColumn> columns = Lists.newArrayList();
			for(int rowNum = 1; true;rowNum++){
				HSSFRow row = sheet.getRow(rowNum);
				if(row==null)break;
				String str = PoiUtil.getStringValue(row.getCell((short)0, Row.RETURN_NULL_AND_BLANK));
				if(str.isEmpty())break;
				columns.add(new ExcelColumn(row));
			}
			return columns;
		} catch (Exception e) {
			logger.error("读schema文件出错"+e.getMessage(), e);
			throw new RuntimeException(e);
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}


	private static String subTypeString(String typeName) {		
		typeName=typeName.substring(typeName.lastIndexOf('_')+1,typeName.length() );
		return typeName;
	}
	
	/**
	 * 写json文件  xx.Json
	 */
	public static String writeJsonFile(File file,String jsonPath,List<ExcelColumn> fields){
		FileInputStream in = null; 
		try{
			List<String> stringList = new ArrayList<>();
			String key = file.getName().substring(0, file.getName().length()-4);
			if(Excel2code.translateTextMap.containsKey(key)){
				stringList = Excel2code.translateTextMap.get(key);
			}
			if(key.equals("Treats")){ 
				 
			}
			in = new FileInputStream(file);
			HSSFSheet sheet = new HSSFWorkbook(in).getSheetAt(0);
			JSONArray jsonData = new JSONArray();
			//前两行为说明信息，跳过
			for(int rowNum = 1; true; rowNum ++){
				HSSFRow row = sheet.getRow(rowNum);
				if(row==null)break;
				String str = PoiUtil.getStringValue(row.getCell((short)0, Row.RETURN_NULL_AND_BLANK));
				if(str.isEmpty())break;
				JSONObject jsonObj = new JSONObject();
				int index = 0;
				for(ExcelColumn column : fields){
					try{
						if(stringList.contains(column.getFieldName())){
							if(column.getColumnType().equals("string")){
								String testStr = PoiUtil.getStringValue(row.getCell(index, Row.RETURN_NULL_AND_BLANK));
								try {
									Integer.parseInt(testStr);
								} catch (Exception e) { 
									logger.error("",e);
									logger.info("文件名:"+file.getPath()+"行号:"+rowNum+"列名:"+column.getColumnName()+",value="+testStr);
								}
							}
						}
						column.set(jsonObj,row);//调用相应的setter方法
						index++;
					}catch(Exception e){
						logger.error("",e.getMessage());
						logger.error("文件名:"+file.getPath()+"行号:"+rowNum+"列名:"+column.getColumnName());
						throw new RuntimeException(e);
					}
				}
				jsonData.add(jsonObj);
			}
			String beautiferJson = JsonUtils.jsonBeautifier(jsonData);
			if(StringUtils.isNotEmpty(jsonPath)){
				File javaFile = new File(jsonPath);
				if(!javaFile.exists()){
					javaFile.mkdirs();
				}
//				String fileName = excelPath.substring(excelPath.lastIndexOf("/")+1,excelPath.length()-4);
				String fileName = file.getName().substring(0,file.getName().length()-4);
				File jsonFile = new File(jsonPath,fileName+".json");
				if (jsonFile.exists()) {
					jsonFile.delete();
				}
				jsonFile.createNewFile();
				FileUtils.writeStringToFile(jsonFile, beautiferJson,"utf8");
				logger.info("生成文件---------->"+jsonFile.getAbsolutePath() );
			}
			return beautiferJson;
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		} 
	}
	
	/**
	 * 写Code
	 */
	public static void writeCodeFile(String excelPath,String jsPath,String javaPath,List<ExcelColumn> fields){
		FileInputStream in = null;
		JSONArray jsonData = null;
		try{
			in = new FileInputStream(excelPath);
			HSSFSheet sheet = new HSSFWorkbook(in).getSheetAt(0);
			jsonData = new JSONArray();
			//前两行为说明信息，跳过
			for(int rowNum = 1; true; rowNum ++){
				HSSFRow row = sheet.getRow(rowNum);
				if(row==null)break;
				String str = PoiUtil.getStringValue(row.getCell((short)0, Row.RETURN_NULL_AND_BLANK));
				if(str.isEmpty())break;
				JSONObject jsonObj = new JSONObject();
				for(ExcelColumn column : fields){
					column.set(jsonObj,row);//调用相应的setter方法
				}
				jsonData.add(jsonObj);
			}
			in.close();
		}catch(Exception e){ 
			logger.error("",e);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < jsonData.size(); i++) {
			JSONObject jsonObject = jsonData.getJSONObject(i);
			Map<String, Object> value = new HashMap<String, Object>();
			value.put("fieldDesc", jsonObject.optString("desc"));
			value.put("fieldCode", jsonObject.optString("id"));
			value.put("fieldName", jsonObject.optString("fieldName"));
			list.add(value);
		}
		map.put("fields", list);
		File file =null;
		if(StringUtils.isNotEmpty(jsPath)){
			try {
				String tmp = "js_textcode.ftl";
				if(StringUtils.isEmpty(javaPath)){
					tmp = "js_musiccode.ftl";
				}
				Template t = ToolsConf.getInstance().getCfg().getTemplate(tmp);
				file = new File(jsPath);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				Writer out = new OutputStreamWriter(new FileOutputStream(file),"utf-8");
				StringWriter write = new StringWriter();
				write.flush();
				t.process(map, out);
				write.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(file.getAbsolutePath()	, e);
				return;
			}
		}
		if(StringUtils.isNotEmpty(javaPath)){
			try {
				Template t = ToolsConf.getInstance().getCfg().getTemplate("java_textcode.ftl");
				file = new File(javaPath);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				Writer out = new OutputStreamWriter(new FileOutputStream(file),"utf-8");
				StringWriter write = new StringWriter();
				write.flush();
				t.process(map, out);
				write.close();
				out.close();
			} catch (Exception e) { 
				logger.error(file.getAbsolutePath()	, e);
				throw new RuntimeException(e);
			}
		}
	}
	/**
	 * 写gameparams
	 */
	public static String writeGameParamas(File file,String javaJson,String jsPath,String javaPath ){
		return writeGameParamas(file, javaJson, jsPath, javaPath,null);
	}
	/**
	 * 写gameparams
	 */
	public static String writeGameParamas(File file,String javaJson,String jsPath,String javaPath,String clientJsonPath){
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			HSSFSheet sheet = new HSSFWorkbook(in).getSheetAt(0);
			List<ExcelColumn> columns = Lists.newArrayList();
			JSONArray jsonData = new JSONArray();
			JSONObject tmpl = new JSONObject();
			for(int rowNum = 1; true; rowNum ++){
				HSSFRow row = sheet.getRow(rowNum);
				if(row==null)break;
				try {
					if(row.getCell(0)==null || StringUtils.isEmpty(row.getCell(0).toString())){
						continue;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				String des = PoiUtil.getStringValue(row.getCell((short)0, Row.RETURN_NULL_AND_BLANK));
				String name = PoiUtil.getStringValue(row.getCell((short)1, Row.RETURN_NULL_AND_BLANK));
				String type = PoiUtil.getStringValue(row.getCell((short)2, Row.RETURN_NULL_AND_BLANK));
				String value = PoiUtil.getStringValue(row.getCell((short)3, Row.RETURN_NULL_AND_BLANK));
				
				ExcelColumn column = new ExcelColumn(des,name,type,value);
				columns.add(column);
				tmpl.put(name, column.convert(value));
			}
			jsonData.add(tmpl);
			String beautiferJson = JsonUtils.jsonBeautifier(jsonData);
			//js json
//			String fileName = excelPath.substring(excelPath.lastIndexOf("/")+1,excelPath.length()-4);
			String fileName = file.getName().substring(0,file.getName().length()-4);
			//java json
			if(StringUtils.isNotEmpty(javaJson)){
				File jsonFile = new File(javaJson,fileName+".json");
				if (jsonFile.exists()) {
					jsonFile.delete();
				}
				jsonFile.createNewFile();
				FileUtils.writeStringToFile(jsonFile, beautiferJson,"utf8");
			}
			if(clientJsonPath!=null){
				File clietnJsonFile = new File(clientJsonPath,fileName+".json");
				if (clietnJsonFile.exists()) {
					clietnJsonFile.delete();
				}
				clietnJsonFile.createNewFile();
				FileUtils.writeStringToFile(clietnJsonFile, beautiferJson,"utf8");
			}
			//js
//			if(StringUtils.isNotEmpty(jsPath)){
//				writeBeanFile(fileName,"js",columns,jsPath,jsTemplate);
//			}
			//java
			if(StringUtils.isNotEmpty(javaPath)){
			    writeBeanFile(fileName,"java",columns,javaPath,javaTemplate);
			}
			return beautiferJson;
		} catch (Exception e) {
			logger.error("",e);
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
		return null;
	}
	
	/**
	 * 写实体类开文件
	 */
	public static void writeBeanFile(String fileName,String fileFix ,List<ExcelColumn> columns,String path,String templateFile){
		if(fileFix.equals("js")){
			if(ToolsConf.file_write_switch_js==false){
				return;
			}
			File javaFile = new File(path);
			if(!javaFile.exists()){
				javaFile.mkdirs();
			}
		}
		if(fileFix.equals("java")){
			if(ToolsConf.file_write_switch_sever==false){
				return;
			}
			File javaFile = new File(path);
			if(!javaFile.exists()){
				javaFile.mkdirs();
			}
		}
		fileName=subTypeString(fileName);
		String excelName = fileName;
		fileName=fileName+"_Json";
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("className",fileName);
		List<Object> list=new ArrayList<Object>();
		for(ExcelColumn fieldModel:columns){
			
			Map<String, Object> field=new HashMap<String, Object>();
			String desc = "";
			if(StringUtils.isNotEmpty(fieldModel.getDesc()))desc = fieldModel.getDesc();
			field.put("fieldDesc", fieldModel.getColumnName()+"::"+desc);
			field.put("fieldType", fieldModel.getJavaType());
			field.put("fieldName", fieldModel.getFieldName());
			if(Excel2code.translateTextMap.containsKey(excelName)){
				List<String> stringList = Excel2code.translateTextMap.get(excelName);
				if(stringList.contains(fieldModel.getFieldName())){
					field.put("fieldIsText", 1);
					if(fieldModel.getJavaType().equals("List<String>")){
						field.put("fieldIsList", 1);
					}else{
						field.put("fieldIsList", 0);
					}
				}else{
					field.put("fieldIsText", 0);
					field.put("fieldIsList", 0);
				}
			}else{
				field.put("fieldIsText", 0);
				field.put("fieldIsList", 0);
			}
			list.add(field);
		}
		root.put("fields", list);
		File afile = null;
		try {
			Template t = ToolsConf.getInstance().getCfg().getTemplate(templateFile);
		    afile = new File(path+StringUtils.toUpperCaseFirstOne((String) fileName)+"."+fileFix);
			if (afile.exists()) {
				afile.delete();
			}
			afile.createNewFile();
			Writer out = new OutputStreamWriter(new FileOutputStream(afile),"utf-8");
			StringWriter write = new StringWriter();
			write.flush();
			t.process(root, out);
			write.close();
			logger.info("生成文件---------->"+afile.getAbsolutePath());
			out.close();
		}catch(Exception e){ 
			logger.error(fileName+fileFix, e);
		} 
	}
}