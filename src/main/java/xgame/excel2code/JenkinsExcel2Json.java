/**
 * 
 */
package xgame.excel2code;

import java.io.File;
import java.util.List;

import com.mind.xgame.tools.config.ToolsConf;
import com.mind.xgame.tools.util.ExcelColumn;

/**
 * jenkins将excel转了json
 * @author ninglong
 */
public class JenkinsExcel2Json {
	public static void main(String[] args) {
		String curPath = JenkinsExcel2Json.class.getResource("/").getPath();
		curPath = curPath.substring(0,curPath.indexOf("tools"));
//		String excelPath =  curPath+"excel";
		String excelPath ="D:\\xlsFile";
		String jsonPath = curPath+"mind-suport/src/main/json";
		String javaPath = curPath+"mind-suport/src/main/java/com/globalgame/auto/json/";
		String java_textCode = curPath+"mind-suport/src/main/java/com/globalgame/common/TextCodeConstants.java";
		
		Excel2code.readExcel(excelPath);
		File javaTexCode = new File(java_textCode);
		if(!javaTexCode.exists()){
			javaTexCode.mkdirs();
		}
		ToolsConf.getInstance().init();
		File dir = new File(excelPath);
		for (File file : dir.listFiles()) {
			if(file.isDirectory())continue;
			if(!file.getPath().endsWith(".xls"))continue;
			if(file.getName().equalsIgnoreCase("GameParams.xls")){
				ExcelUtils.writeGameParamas(file,jsonPath,"",javaPath);
			}else{
				List<ExcelColumn> fields = ExcelUtils.readSchema(file.getPath());
				if(fields!=null && fields.size()>0){
					ExcelUtils.writeJsonFile(file, jsonPath, fields);
					String fileName = file.getPath().substring(file.getPath().lastIndexOf("\\")+1,file.getPath().length()-4);
					ExcelUtils.writeBeanFile(fileName,"java",fields,javaPath,ExcelUtils.javaTemplate);
					if (file.getName().equalsIgnoreCase("text.xls")) {
						ExcelUtils.writeCodeFile(file.getPath(),"",java_textCode,fields);
					}
				}
			}
		}
		System.out.println("执行结束。。。。。。。。。");
	}
}