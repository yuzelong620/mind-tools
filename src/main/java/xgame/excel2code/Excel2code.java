package xgame.excel2code;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mind.xgame.tools.config.ToolsConf;
import com.mind.xgame.tools.util.ExcelColumn;

/**
 * 将excel解析成json以及生成对应的实体类
 * 
 * @author ninglong
 */
public class Excel2code {
	private static Logger logger = LoggerFactory.getLogger(Excel2code.class);

	public static void main(String[] args) {
		readParams(args);

		ToolsConf.getInstance().init();  

		StringBuilder jsJsonData = new StringBuilder();
		jsJsonData.append("var ConfigDataStorage={};\n");

		File dir = new File(ToolsConf.path_excel);
		for (File file : dir.listFiles()) {
			if (file.isDirectory())
				continue;
			if (!(file.getPath().endsWith(".xls")||file.getPath().endsWith(".xlsx")))
				continue;
			String beautiferJson = "";
			if (file.getName().equalsIgnoreCase("gameParams.xls")||file.getName().equalsIgnoreCase("gameParams.xlsx")) {
				if (ToolsConf.file_write_switch_sever) {
					beautiferJson = ExcelUtils.writeGameParamas(file, ToolsConf.path_json_server, ToolsConf.path_json_client,ToolsConf.path_java_server, ToolsConf.path_json_client);
				}
				else{
					beautiferJson = ExcelUtils.writeGameParamas(file, null,ToolsConf.path_json_client,null, ToolsConf.path_json_client);
				}
			} 
			else {
				List<ExcelColumn> fields = ExcelUtils.readSchema(file.getPath());
				if (fields != null && fields.size() > 0) {
					String fileName = file.getPath().substring(file.getPath().lastIndexOf("/") + 1,
							file.getPath().length() - 4);
					fileName = file.getName().substring(0, file.getName().length() - 4);
					logger.info("读取文件---------->" + file.getPath());
					// 客户端文件
					beautiferJson = ExcelUtils.writeJsonFile(file, ToolsConf.path_json_client, fields);// 客户端json文件
//					ExcelUtils.writeBeanFile(fileName, "js", fields, ToolsConf.jsPath, ExcelUtils.jsTemplate);
					// 服务器文件
					if (ToolsConf.file_write_switch_sever) {
						ExcelUtils.writeJsonFile(file, ToolsConf.path_json_server, fields);
						ExcelUtils.writeBeanFile(fileName, "java", fields, ToolsConf.path_java_server,ExcelUtils.javaTemplate);
					}
				}
			}
			jsJsonData.append("ConfigDataStorage.").append(file.getName().substring(0, file.getName().lastIndexOf("."))).append("=").append(beautiferJson).append(";").append("\n");
		}
		
		if (ToolsConf.file_write_switch_js) {
			jsJsonData.append("module.exports = ConfigDataStorage;");
			// 检查目录是否存在
			File jsonJsPath = new File(ToolsConf.js_jsonPath);
			if (!jsonJsPath.exists()) {
				jsonJsPath.mkdirs();
			}
			File clientJsonFile = new File(ToolsConf.js_jsonPath,ToolsConf.js_data_file_name);
			if (clientJsonFile.exists())
				clientJsonFile.delete();
			try {
				clientJsonFile.createNewFile();
				FileUtils.writeStringToFile(clientJsonFile, jsJsonData.toString(), "utf8");
			} catch (IOException e) {
				logger.info("", e);
			}
			logger.info("写入文件："+clientJsonFile.getAbsolutePath()); 
		}
		logger.info( "--------------------------------------------------------------------------------------------");
		logger.info( "--------------------------------------------------------------------------------------------");
		 
		logger.info("-----------------------------------工具执行完毕！---------------------------------------------");
		
		logger.info( "--------------------------------------------------------------------------------------------");
		logger.info( "--------------------------------------------------------------------------------------------");
	}

	private static void readParams(String[] args) {
		if (args != null && args.length > 0) {
			ToolsConf.path_excel = args[0];// 读取XXX.xls文件路径
			if (args.length > 1) {
				ToolsConf.path_json_client = args[1];// 输出客户端XXX.json路径
			}
			if (args.length > 2) {
				ToolsConf.path_json_server = args[2];// 输出服务器XXX.json路径
			}
			if (args.length > 3) {
				ToolsConf.path_java_server = args[3];// 输出服务器XXX_Json.java路径
			}
			if (args.length > 4) {
				ToolsConf.file_write_switch_sever = (1 == Integer.parseInt(args[4]));
			}
			if(args.length>5){
				ToolsConf.file_write_switch_js=true;
				ToolsConf.js_jsonPath=args[5];
			}
		}
		logger.info("--------------------------------------------------------------------------");
		logger.info("xxx.xls              文件输入路径:" + ToolsConf.path_excel);
		logger.info("客户端xxx.json        文件输出路径:" + ToolsConf.path_json_client);
		logger.info("服务器 xxx.json           输出路径:" + ToolsConf.path_json_server);
		logger.info("服务器xxx_json.java   文件输出路径:" + ToolsConf.path_java_server);
		logger.info("服务器                                          文件生成开关：" + ToolsConf.file_write_switch_sever);
		logger.info("--------------------------------------------------------------------------");
	}

	/** 需要根据语言转换的字段 **/
	public static Map<String, List<String>> translateTextMap = new HashMap<String, List<String>>();

	/**
	 * 读取TranslateText.xls文件 ， 用于自动生成过滤多语言
	 * 
	 * @param excelPath
	 */
	public static void readExcel(String excelPath) {
		try {
			File file = new File(excelPath + "/TranslateText.xls");
			String[][] result = getData(file, 1);
			int rowLength = result.length;
			for (int i = 0; i < rowLength; i++) {
				List<String> list = new ArrayList<>();
				for (int j = 0; j < result[i].length; j++) {
					String value = result[i][j];
					if (value.length() > 1 && j == 1) {
						String resultValue = value.substring(1, value.length() - 1);
						String[] arr = resultValue.split(",");
						Collections.addAll(list, arr);
					} else {
					}
				}
				translateTextMap.put(result[i][0], list);
			}
			for (Map.Entry<String, List<String>> entry : translateTextMap.entrySet()) {
				String key = entry.getKey();
				List<String> list = entry.getValue();
				StringBuffer sb = new StringBuffer();
				sb.append(key).append("   ");
				for (String s : list) {
					sb.append(s).append(",");
				}

			}
		} catch (Exception e) {
			logger.error("", e);
		}

		logger.info("执行完了！");

	}

	public static String[][] getData(File file, int ignoreRows) throws FileNotFoundException, IOException {
		List<String[]> result = new ArrayList<String[]>();
		int rowSize = 0;
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		// 打开HSSFWorkbook
		POIFSFileSystem fs = new POIFSFileSystem(in);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFCell cell = null;
		for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
			HSSFSheet st = wb.getSheetAt(sheetIndex);
			// 第一行为标题，不取
			for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {
				HSSFRow row = st.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				int tempRowSize = row.getLastCellNum() + 1;
				if (tempRowSize > rowSize) {
					rowSize = tempRowSize;
				}
				String[] values = new String[rowSize];
				Arrays.fill(values, "");
				boolean hasValue = false;
				for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
					String value = "";
					cell = row.getCell(columnIndex);
					if (cell != null) {
						// 注意：一定要设成这个，否则可能会出现乱码
						// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_STRING:
							value = cell.getStringCellValue();
							break;
						case HSSFCell.CELL_TYPE_NUMERIC:
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								if (date != null) {
									value = new SimpleDateFormat("yyyy-MM-dd").format(date);
								} else {
									value = "";
								}
							} else {
								value = new DecimalFormat("0").format(cell.getNumericCellValue());
							}
							break;
						case HSSFCell.CELL_TYPE_FORMULA:
							// 导入时如果为公式生成的数据则无值
							if (!cell.getStringCellValue().equals("")) {
								value = cell.getStringCellValue();
							} else {
								value = cell.getNumericCellValue() + "";
							}
							break;
						case HSSFCell.CELL_TYPE_BLANK:
							break;
						case HSSFCell.CELL_TYPE_ERROR:
							value = "";
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN:
							value = (cell.getBooleanCellValue() == true ? "Y" : "N");
							break;
						default:
							value = "";
						}
					}
					if (columnIndex == 0 && value.trim().equals("")) {
						break;
					}
					values[columnIndex] = rightTrim(value);
					hasValue = true;
				}

				if (hasValue) {
					result.add(values);
				}
			}
		}
		in.close();
		String[][] returnArray = new String[result.size()][rowSize];
		for (int i = 0; i < returnArray.length; i++) {
			returnArray[i] = (String[]) result.get(i);
		}
		return returnArray;
	}

	/**
	 * 去掉字符串右边的空格
	 * 
	 * @param str
	 *            要处理的字符串
	 * @return 处理后的字符串
	 */
	public static String rightTrim(String str) {
		if (str == null) {
			return "";
		}
		int length = str.length();
		for (int i = length - 1; i >= 0; i--) {
			if (str.charAt(i) != 0x20) {
				break;
			}
			length--;
		}
		return str.substring(0, length);
	}

}