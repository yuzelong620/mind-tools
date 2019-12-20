package xgame.tools.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.codehaus.jackson.JsonNode;

import com.google.common.collect.Maps;

public class JsonToExcelLoader {
	
	public static List<String> getTitles(JsonNode array){
		List<String> titles = new ArrayList<String>();
		Iterator<JsonNode> iterator = array.iterator();
		while(iterator.hasNext()){
			JsonNode jsonObj = iterator.next();
			Iterator<String> iterator2 = jsonObj.getFieldNames();
			while(iterator2.hasNext()){
				String jsonValue = iterator2.next();
				if(titles.contains(jsonValue)){
					continue;
				}
				titles.add(jsonValue);
				System.out.println(jsonValue);
			}
		}
		return titles;
	}
	
	public static void toExcel(String path,String fileName,String jsonString){
		
		HSSFWorkbook  wb = new HSSFWorkbook();


		HSSFSheet sheet = wb.createSheet("data");
		HSSFRow headerRow = sheet.createRow(0);

		
		JsonNode array = JsonUtils.toJsonArray(jsonString);
		
		Map<String,Integer> title2IndexMap = Maps.newHashMap();
		List<String> titles = getTitles(array);
		System.out.println(titles.size());
		for (int i = 0; i < titles.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(titles.get(i));
            title2IndexMap.put(titles.get(i), i);
        }
		
		HSSFRow row;
		int rownum = 1;
		Iterator<JsonNode> iterator = array.iterator();
		while(iterator.hasNext()){
			JsonNode jsonObj = iterator.next();
			row = sheet.createRow(rownum);
			Iterator<String> iterator2 = jsonObj.getFieldNames();
			while(iterator2.hasNext()){
				String fieldName = iterator2.next();
				int index = title2IndexMap.get(fieldName);
				Cell cell = row.createCell(index);
				JsonNode jsonValue = jsonObj.get(fieldName);
				if(jsonValue.isInt()){
					cell.setCellValue(jsonValue.getIntValue());
				}else if(jsonValue.isDouble()){
					cell.setCellValue(jsonValue.getDoubleValue());
				}else if(jsonValue.isObject() || jsonValue.isArray()){
					cell.setCellValue(jsonValue.toString());
				}else{
					cell.setCellValue(jsonValue.getValueAsText());
				}
			}
			
			rownum++;
		}
		
		
		File tmplFile = new File(path,fileName);
		FileOutputStream out;
        
        try {
        	out = new FileOutputStream(tmplFile);
        	wb.write(out);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
