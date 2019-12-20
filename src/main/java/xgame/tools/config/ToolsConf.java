package xgame.tools.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateExceptionHandler;

public class ToolsConf {
	
	/**js文件写入开关*/
	 public static boolean file_write_switch_js=true;
	 /**消息写入开关*/
	 public static boolean file_write_switch_message=false;
	 /**java文件写入开关*/
	 public static boolean file_write_switch_sever=false;
	 
	public static String path_excel =    "../../exls";
	public static String path_json_client =    "../../client/client_src/jsonFiles/"; 
	public static String path_java_server =    "../../server/mind-suport/src/main/java/com/globalgame/auto/json/";
	public static String path_json_server =    "../../server/mind-suport/src/main/json/";	
	
	public static String JAVA_SERVER_MESSAGE =     "../../server/mind-server/src/main/java/com/mind/auto/msg/";
	public static String JAVA_SERVER_MessageCode = "../../server/mind-server/src/main/java/com/mind/auto/msg/MessageCode.java";
	
	// 0:普通 1：login服务器消息
	public static int type = 0;

//	public final static String CONFIG_FILE = "/tools.conf";
	public static String MESSAGE_FILE =            "../../message/message.xml";
	public static String JS_MESSAGECODE =          "../../client/client_src/message/MessageCode.js";
	public static String JS_MESSAGE =              "../../client/client_src/message/";
	public final static String JAVA_OUT_PATH_TESTHANDLER_CLIENT  = "../../server/mind-client/src/main/java/com/mind/test/TestHandler.java";
	public static String JAVA_CLIENT_MessageCode                 = "../../server/mind-client/src/main/java/com/mind/auto/msg/MessageCode.java";
	public static String JAVA_CLIENT_MESSAGE 				     = "../../server/mind-client/src/main/java/com/mind/auto/msg/";
					//xxx_json.java 文件
					 
	// 生成json文件所在目录 
	public static String js_jsonPath = "./client/client_src/json";
	/**
	 * 客户端全局的json 仓库文件
	 */
	public static String js_data_file_name="ConfigDataStorage.js";
	//生成js文件所在目录 
	public static String jsPath = "../../client/client_src/auto/";	

	
	private final static String ftl_dir = "/ftl";

//	public static String JAVA_EXCEL2JSON_DATA_LOGIN = "../../../server/mind-suport/src/main/json/";
//	public static String JAVA_EXCEL2JSON_SCHEMAL_LOGIN = "../../../server/mind-suport/src/main/java/com/globalgame/auto/json/";

	public static String JAVA_FTL_MESSAGECODE = null;
	public static String JAVA_FTL_SERVER = null;
	public static String PACKAGE_NAME = "com.mind.auto.msg";

	// 生成js text code 文件所在目录
//	public static String RESOURCE_JS_TEXTCODE_DIR = "../../../client/client_src/common/BSTextCode.js";
//	public static String JS_TEXTCODE_FTL_MESSAGECODE = null;
//	public static String RESOURCE_JS_MUSICCODE_DIR = "../../../client/client_src/common/BSMusicCode.js";
//	public static String JS_MUSICCODE_FTL_MESSAGECODE = null;
//	public static String JAVA_TEXTCODE_FTL_MESSAGECODE = "java_textcode.ftl";
//	public static String JAVA_EXCEL_TEXTCODE = "../../../server/mind-suport/src/main/java/com/globalgame/common/TextCodeConstants.java";

//	// 资源存放位置
//	public static String RESOURCE_GLOBAL_DIR = "../../../design/exls";

 
	
	// public static int excelType=0;
	private Configuration cfg;
	private static ToolsConf conf = null;
	public static String curPath = null;
	private List<DataBigType> bigTypes = new ArrayList<DataBigType>();
	private List<DataBaseType> baseTypes = new ArrayList<DataBaseType>();

	public static ToolsConf getInstance() {
		if (conf == null) {
			conf = new ToolsConf();
		}
		return conf;
	}

	private void initFtl() {
		// 初始化FreeMarker配置;
		// - 创建一个配置实例
		cfg = new Configuration();
		// - 设置模板目录.
		cfg.setClassForTemplateLoading(this.getClass(), ftl_dir);
		// - 设置模板延迟时间，测试环境设置为0，正是环境可提高数值.
		cfg.setTemplateUpdateDelay(0);
		// - 设置错误句柄
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
		cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
		// - 设置默认模板编码
		cfg.setDefaultEncoding("utf-8");
		// - 设置输出编码
		cfg.setOutputEncoding("utf-8");
		cfg.setLocale(Locale.SIMPLIFIED_CHINESE);
	}

	public void init() {
		initFtl();
		initDataBigTypes();
		initDataBaseType();
		initFile();

	}

	public Configuration getCfg() {
		return cfg;
	}

	private void initDataBigTypes() {
		String value = "base,0><base array,1><message,2><message array,3";
		String[] types = value.split("><");
		for (String type : types) {
			String[] objs = type.split(",");
			DataBigType bigType = new DataBigType(objs[0], Integer.parseInt(objs[1]));
			bigTypes.add(bigType);
		}
	}

	private void initFile() {
//		initFile(ToolsConf.JAVA_EXCEL2JSON_SCHEMAL,
//				ToolsConf.JAVA_SERVER_MESSAGE,
//				ToolsConf.JAVA_CLIENT_MESSAGE,
//				ToolsConf.JAVA_EXCEL2JSON_DATA,
//              ToolsConf.JS_MESSAGE);
		
		//生成消息相关目录
		if( file_write_switch_message){
			initFile(ToolsConf.JAVA_CLIENT_MESSAGE,ToolsConf.JAVA_SERVER_MESSAGE, ToolsConf.JS_MESSAGE);
		}
		//服务器开关目录
		if(file_write_switch_sever){
			initFile(ToolsConf.path_java_server);
			initFile(ToolsConf.path_json_server);
		}
	}
	
	private void initFile(String... filePath){
		for(String path:filePath){
			File f = new File(path);
			if (!f.exists()) {
				System.out.println("创建文件夹.." + path);
				f.mkdirs();
			}
		}
	}

	public int getMessageType() {
		return type;
	}

	public List<DataBigType> getDataBigTypes() {
		return bigTypes;
	}

	private void initDataBaseType() {
		String value = "Byte--byte,byte,byte,0><Short--short,short,short,0><Integer--int,int,int,0><Long--long,long,long,0><Float--float,float,float,0><Double--double,double,double,0><String--string,String,string,0><Byte[]--byte*,byte,byte,1><Short[]--short*,short,short,1><Integer[]--int*,int,int,1><Long[]--long*,long,long,1><double[]--double*,double,double,1><String[]--string*,String,string,1";
		String[] types = value.split("><");
		for (String type : types) {
			String[] objs = type.split(",");
			int bigType = Integer.parseInt(objs[3]);

			DataBaseType baseType = new DataBaseType(objs[0], objs[1], objs[2], getBigTypeByValue(bigType));
			baseTypes.add(baseType);
		}
	}

	public DataBigType getBigTypeByValue(int value) {
		for (DataBigType bigType : bigTypes) {
			if (bigType.getValue() == value) {
				return bigType;
			}
		}
		return null;
	}

	public List<DataBaseType> getDataBaseType() {
		return baseTypes;
	}

	public String getCurPath() {
		return curPath;
	}

}
