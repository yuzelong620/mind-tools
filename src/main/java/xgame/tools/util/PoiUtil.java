package xgame.tools.util;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;

/**
 * 2010-9-24上午10:12:33
 * 
 * @author Appo
 *
 */
public class PoiUtil {
	private final static Logger logger = Logger.getLogger(PoiUtil.class);
	
	public static String getStringValue(HSSFCell cell){
		if(cell == null){
			return "";
		}
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			return cell.toString();
		case HSSFCell.CELL_TYPE_NUMERIC:
			String str = cell.toString();
			if(str.endsWith(".0")){
				return str.substring(0,str.length()-2);
			}else{
				return str;
			}

		default:
			return cell.toString();
		}
	}
}
