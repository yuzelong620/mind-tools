package xgame.tools.message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.mind.core.util.StringUtils;
import com.mind.xgame.tools.config.ToolsConf;
import com.mind.xgame.tools.constants.ToolsConstants;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

import freemarker.template.Template;

/**消息生成类
 * @author jorsun
 *
 */
public class MessageUtil {
	private static final Logger logger = Logger.getLogger(MessageUtil.class);
	private static List<Map<String, Object>> classesMap=new ArrayList<Map<String,Object>>();
	
	private static Map<String, String> namePackage=new HashMap<String, String>();
	public static String[] getMessageNames(List<MessageModel> messageModels){
		String[] strings=new String[messageModels.size()];
		for(int i=0;i<messageModels.size();i++){
			strings[i]=(messageModels.get(i).getName()+"--"+messageModels.get(i).getDesc());
		}
		return strings;

	}
	public static MessageModel createNewMessageModel() {
		MessageModel model = new MessageModel();
		model.setName(ToolsConstants.NEW_MESSAGE_NAME);
		model.setDesc("创建新协议");
		return model;
	}
	public static void writeMessageFile(Vector<ModuleModel> moduleModels){
		writeCodeFile(moduleModels);
		writeProtocolFile(moduleModels);
		writeMessageCodeFile(moduleModels);
	}
	public static void writeMessageCodeFile(Vector<ModuleModel> moduleModels){
		Map<String, Object> map=new HashMap<String, Object>();
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		for(ModuleModel moduleModel:moduleModels){
			Map<String, Object> value=new HashMap<String, Object>();
			List<Map<String, String>> classList=new ArrayList<Map<String,String>>();
			for(MessageModel messageModel:moduleModel.getMessageModels()){
				Map<String, String> class1=new HashMap<String, String>();
				class1.put("className", messageModel.getName());
				class1.put("classDesc", messageModel.getDesc());
				class1.put("code", messageModel.getMessageType()+"");
				classList.add(class1);
			}
			value.put("classes", classList);
			value.put("id", moduleModel.getId());
			value.put("code", 100*moduleModel.getId()+"------"+100*(moduleModel.getId()+1));
			value.put("name", moduleModel.getModuleName());
			list.add(value);
		}
		map.put("modules", list);
		try {
			Template t = ToolsConf.getInstance().getCfg().getTemplate(ToolsConf.JAVA_FTL_MESSAGECODE);
			File file = new File(ToolsConf.JAVA_SERVER_MessageCode);
			if (file.exists()) {
				file.delete();
			}
			Writer out = new OutputStreamWriter(new FileOutputStream(file),
					"utf-8");
			StringWriter write = new StringWriter();
			write.flush();
			t.process(map, out);
			write.close();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			Template t = ToolsConf.getInstance().getCfg().getTemplate(ToolsConf.JAVA_FTL_MESSAGECODE);
			File file = new File(ToolsConf.JAVA_CLIENT_MessageCode);
			if (file.exists()) {
				file.delete();
			}
			Writer out = new OutputStreamWriter(new FileOutputStream(file),
					"utf-8");
			StringWriter write = new StringWriter();
			write.flush();
			t.process(map, out);
			write.close();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			Template t = ToolsConf.getInstance().getCfg().getTemplate("TestHandler.ftl");
			File file = new File(ToolsConf.JAVA_OUT_PATH_TESTHANDLER_CLIENT);
			if (file.exists()) {
				file.delete();
			}
			Writer out = new OutputStreamWriter(new FileOutputStream(file),
					"utf-8");
			StringWriter write = new StringWriter();
			write.flush();
			t.process(map, out);
			write.close();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			Template t = ToolsConf.getInstance().getCfg().getTemplate("jsMessageCode.ftl");
			File file = new File(ToolsConf.JS_MESSAGECODE);
			if (file.exists()) {
				file.delete();
			}
			Writer out = new OutputStreamWriter(new FileOutputStream(file),
					"utf-8");
			StringWriter write = new StringWriter();
			write.flush();
			t.process(map, out);
			write.close();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private static void writeProtocolFile(Vector<ModuleModel> moduleModels){
		File file = new File(ToolsConf.MESSAGE_FILE);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		XStream stream=new XStream();
		Annotations.configureAliases(stream,ModuleModel.class, MessageModel.class,FieldModel.class);
		try {
			FileUtils.writeStringToFile(file,stream.toXML(moduleModels));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void writeCodeFile(Vector<ModuleModel> moduleModels){
		for(ModuleModel moduleModel:moduleModels){
			for(MessageModel messageModel:moduleModel.getMessageModels()){
				namePackage.put(messageModel.getName(),ToolsConf.PACKAGE_NAME+"."+moduleModel.getModuleName());
			}
		}
		
		for(ModuleModel moduleModel:moduleModels){
			for(MessageModel messageModel:moduleModel.getMessageModels()){
				writeOneFile(moduleModel.getModuleName(),	messageModel);
			}
		}

	}
	private static void writeOneFile(String moduleName,MessageModel messageModel){
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("className",messageModel.getName());
		root.put("classDesc", messageModel.getDesc());
		root.put("package", namePackage.get(messageModel.getName()));
		root.put("messageType", messageModel.getMessageType());
		List<Object> list=new ArrayList<Object>();
		Set<String> importClassName=new HashSet<String>();
		for(FieldModel fieldModel:messageModel.getFields()){
			Map<String, Object> field=new HashMap<String, Object>();
			field.put("fieldDesc", fieldModel.getDesc());
			field.put("fieldType", fieldModel.getJavaType());
			field.put("fieldCppType", fieldModel.getCppType());
			field.put("fieldStyle",fieldModel.getBigType());
			field.put("fieldName", fieldModel.getName());
			list.add(field);
			if(fieldModel.getBigType()==2||fieldModel.getBigType()==3){
				importClassName.add(namePackage.get(fieldModel.getJavaType())+"."+fieldModel.getJavaType());
			}
		}
		List<Object> names=new ArrayList<Object>();
		for(String name:importClassName){
			Map<String, Object> field=new HashMap<String, Object>();
			field.put("name", name);
			names.add(field);
		}
		root.put("importClassName", importClassName);
		root.put("fields", list);
		classesMap.add(root);
		
		File modelDirection=new File(ToolsConf.JAVA_SERVER_MESSAGE+moduleName);
		if(modelDirection.exists()){
			modelDirection.delete();
		}
		modelDirection.mkdir();
		for(Map<String, Object> one:classesMap){
			File afile =null;
			try {
				Template t = ToolsConf.getInstance().getCfg().getTemplate(ToolsConf.JAVA_FTL_SERVER);
				afile = new File(ToolsConf.JAVA_SERVER_MESSAGE+moduleName+"/"
						+StringUtils.toUpperCaseFirstOne((String) one.get("className"))+".java");
				if (afile.exists()) {
					afile.delete();
				}
				afile.createNewFile();
				Writer out = new OutputStreamWriter(new FileOutputStream(afile),
						"utf-8");
				StringWriter write = new StringWriter();
				write.flush();
				t.process(one, out);
				write.close();
				out.close();
				
			}catch(Exception e){
				e.printStackTrace();
				logger.error("write file wrong!file="+afile.getAbsolutePath(), e);
			}
			try {
				Template t = ToolsConf.getInstance().getCfg().getTemplate(ToolsConf.JAVA_FTL_SERVER);
				afile = new File(ToolsConf.JAVA_CLIENT_MESSAGE+moduleName+"/"
						+StringUtils.toUpperCaseFirstOne((String) one.get("className"))+".java");
				if (afile.exists()) {
					afile.delete();
				}
				File b=new File(ToolsConf.JAVA_CLIENT_MESSAGE+moduleName);
				b.mkdirs();
				logger.info("java file path="+afile.getAbsolutePath());
				afile.createNewFile();
				Writer out = new OutputStreamWriter(new FileOutputStream(afile),
						"utf-8");
				StringWriter write = new StringWriter();
				write.flush();
				t.process(one, out);
				write.close();
				out.close();
				
			}catch(Exception e){
				e.printStackTrace();
				logger.error("write file wrong!file="+afile.getAbsolutePath(), e);
			}
		}
		File jsModelDirection=new File(ToolsConf.JS_MESSAGE+moduleName);
		if(jsModelDirection.exists()){
			jsModelDirection.delete();
		}
		jsModelDirection.mkdir();
		for(Map<String, Object> one:classesMap){
			File afile = new File(ToolsConf.JS_MESSAGE+moduleName+"/"
					+StringUtils.toUpperCaseFirstOne((String) one.get("className"))+".js");
			try {
				Template t = ToolsConf.getInstance().getCfg().getTemplate("js_client.ftl");
				if (afile.exists()) {
					afile.delete();
				}
				afile.createNewFile();
				Writer out = new OutputStreamWriter(new FileOutputStream(afile),
						"utf-8");
				StringWriter write = new StringWriter();
				write.flush();
				t.process(one, out);
				write.close();
				out.close();
				
			}catch(Exception e){
				logger.error("file="+afile.getAbsolutePath(), e);
			}
		}
		classesMap.clear();
	}
//	private static void initFileData(FieldModel model){
//		Map<String, Object> root=new HashMap<String, Object>();
//		root.put("className",ToolsConstants.SUB_CLASS+model.getName());
//		root.put("classDesc", model.getDesc());
//		root.put("package", model.getPackageName());
//		List<Object> list=new ArrayList<Object>();
//		for(FieldModel fieldModel:model.getFields()){
//			Map<String, Object> field=new HashMap<String, Object>();
//			field.put("fieldDesc", fieldModel.getDesc());
//			DataBaseType dataTypeEnum=DataBaseType.getDataTypeEnum(fieldModel.getType());
//			TypeEnum typeEnum=dataTypeEnum.getTypeEnum();
//			switch(typeEnum){
//			case list:
//				field.put("fieldType",ToolsConstants.SUB_CLASS+fieldModel.getName());
//				fieldModel.setPackageName(model.getPackageName()+"."+XgameStringUtil.toLowerCaseFirstOne(model.getName()));
//				initFileData(fieldModel);
//				field.put("fieldStyle", typeEnum.ordinal());
//				break;
//			case normal:
//				field.put("fieldType", fieldModel.getType());
//				field.put("fieldStyle", typeEnum.ordinal());
//				break;
//			case array:
//				field.put("fieldType", fieldModel.getType().substring(0, fieldModel.getType().length()-1));
//				field.put("fieldStyle",typeEnum.ordinal());
//				break;
//			}
//			field.put("fieldName", fieldModel.getName());
//			list.add(field);
//		}
//		root.put("fields", list);
//		classesMap.add(root);
//	}
	public static Vector<MessageModel> getAllMessages(Vector<ModuleModel>  moduleModels) {
		Vector<MessageModel> models=new Vector<MessageModel>();
		for(ModuleModel moduleModel:moduleModels){
			Vector<MessageModel> v = moduleModel.getMessageModels();
			for(MessageModel mm:v){
//				if(mm.getName().startsWith("SL_") || mm.getName().startsWith("LS_")||mm.getName().startsWith("CS_") || mm.getName().startsWith("SC_")||mm.getName().startsWith("LC_") || mm.getName().startsWith("CL_")){
//					continue;
//				}
				models.add(mm);
			}
		}
		return models;
	}
	public static short createMessageType(ModuleModel moduleModel){
		int baseNum=0;
		if(ToolsConf.getInstance().getMessageType()==1){
			baseNum=10000+100*moduleModel.getId();
		}else if(ToolsConf.getInstance().getMessageType()==2){
			baseNum=20000+100*moduleModel.getId();
		}else if(ToolsConf.getInstance().getMessageType()==3){
			baseNum=30000+100*moduleModel.getId();
		}else{
			 baseNum=100*moduleModel.getId();
		}
		for(MessageModel model:moduleModel.getMessageModels()){
			if(baseNum<model.getMessageType()){
				baseNum=model.getMessageType();
			}
			
		}
		return  (short) (baseNum+1);
	}
	public static short createModuleId(Vector<ModuleModel>  moduleModels){
		int id=1;
		for(ModuleModel model:moduleModels){
			if(id<model.getId()){
				id=model.getId();
			}
			
		}
		return (short) (id+1);
	}
	public static Vector<ModuleModel> loadMessages() {
		Vector<ModuleModel> models = null;
		File file = new File(ToolsConf.MESSAGE_FILE);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			String s=FileUtils.readFileToString(file);
			if(s!=null&&s.length()>0){
				XStream stream=new XStream();
				Annotations.configureAliases(stream, ModuleModel.class,MessageModel.class,FieldModel.class);
				models=(Vector<ModuleModel>) stream.fromXML(s);
			}else{
				models=new Vector<ModuleModel>();
			}
		} catch (IOException e) {
			logger.error("file="+file.getAbsolutePath(), e);
		}
		return models;
	}
	public static Vector<ModuleModel> map2vector(Map<Integer,ModuleModel> models){
		Vector<ModuleModel> result =new Vector<ModuleModel>() ;
		for(ModuleModel model:models.values()){
			result.add(model);
		}
		return result;
	}
	public static Map<Integer,ModuleModel> vector2map(Vector<ModuleModel> models){
		Map<Integer,ModuleModel> result =new HashMap<Integer, ModuleModel>() ;
		for(ModuleModel model:models){
			result.put(model.getId(), model);
		}
		return result;
	}

}
