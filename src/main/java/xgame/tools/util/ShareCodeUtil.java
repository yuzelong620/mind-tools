/**
 * 
 */
package xgame.tools.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * 邀请码生成器，算法原理：<br/>
 * 1) 获取id: 1127738 <br/>
 * 2) 使用自定义进制转为：gpm6 <br/>
 * 3) 转为字符串，并在后面加'O'字符：gpm6O <br/>
 * 4）在后面随机产生若干个随机数字字符：gpm6O7 <br/>
 * 转为自定义进制后就不会出现O这个字符，然后在后面加个'O'，这样就能确定唯一性。最后在后面产生一些随机字符进行补全。<br/>
 * @author jiayu.qiu
 */
public class ShareCodeUtil {

	/** 自定义进制*/
    private static final char[] r = new char[]{'Q','1','W', 'E','0', '8', 'A', 'S', '2', 'D', 'Z', 'X', '9', 'C', '7', 'P', '5', 'I', 'K', '3', 'M', 'J', 'U', 'F', 'R', '4', 'V', 'Y', 'L', 'T', 'N', '6', 'B', 'G', 'H'};

    /** (不能与自定义进制有重复) */
    private static final char b='O';

    /** 进制长度 */
    private static final int binLen = r.length;

    /** 序列最大长度 */
    private static final int s=12;

    /**
     * 根据ID生成指定随机码
     */
    public static String toSerialCode(int id) {
        char[] buf=new char[32];
        int charPos=32;
        while((id / binLen) > 0) {
            int ind=(int)(id % binLen);
            buf[--charPos]=r[ind];
            id /= binLen;
        }
        buf[--charPos]=r[(int)(id % binLen)];
        String str=new String(buf, charPos, (32 - charPos));
        // 不够长度的自动随机补全
        if(str.length() < s) {
            StringBuilder sb = new StringBuilder();
            sb.append(b);
            Random rnd = new Random();
            for(int i=1;i < s - str.length();i++) {
            		sb.append(r[rnd.nextInt(binLen)]);
            }
            str+=sb.toString();
        }
        return str;
    }

    /**
     * 解码
     */
    public static long codeToId(String code) {
        char chs[] = code.toCharArray();
        int res = 0;
        for(int i=0; i < chs.length; i++) {
            int ind=0;
            for(int j=0; j < binLen; j++) {
                if(chs[i] == r[j]) {
                    ind=j;
                    break;
                }
            }
            if(chs[i] == b) {
                break;
            }
            if(i > 0) {
                res=res * binLen + ind;
            } else {
                res=ind;
            }
        }
        return res;
    }
    
    public static void main(String[] args) throws Exception{
    		Set<String> set = new HashSet<String>();
    		int[]ids =new int[]{101,202,303,404,505,606,707,808,909,1010};
    		JSONArray jsonData = new JSONArray();
    		for(int id:ids){
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("sheet1");
            sheet.setColumnWidth(1, 4500);
            HSSFRow row = sheet.createRow(0);
            row.setHeight((short) 500);// 设定行的高度
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("平台id");
            cell = row.createCell(1);
            cell.setCellValue("礼品码");
    			for(int i=0;i<50000;i++){
        			String str = toSerialCode(id);
        			set.add(str);
        			
        			JSONObject tmpl = new JSONObject();
        			tmpl.put("ptId", id);
        			tmpl.put("code", str);
        			jsonData.add(tmpl);
        			
                row = sheet.createRow(i+1);
                row.setHeight((short) 500);// 设定行的高度
                cell = row.createCell(0);
                cell.setCellValue(id);
                cell = row.createCell(1);
                cell.setCellValue(str);
        		}
            FileOutputStream os = new FileOutputStream("/Users/ninglong/work/bs/trunk/server/tools/mind-tools/src/main/java/com/mind/xgame/excel2code/"+id+".xls");
            wb.write(os);
            os.close();
    		}
		String beautiferJson = JsonUtils.jsonBeautifier(jsonData);
    		File jsonFile = new File("/Users/ninglong/work/bs/trunk/server/tools/mind-tools/src/main/java/com/mind/xgame/excel2code/giftCode.json");
    		jsonFile.createNewFile();
    		FileUtils.writeStringToFile(jsonFile, beautiferJson,"utf8");
    		System.out.println("一共生成礼品码:"+set.size()+"个");
	}
}