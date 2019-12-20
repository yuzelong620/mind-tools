package xgame.cocostudio;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mind.core.util.StringUtils;
import com.mind.xgame.cocostudio.model.LabelTextModel;
import com.mind.xgame.tools.util.JsonToExcelLoader;

public class JsonModify {
	private static Logger logger = LoggerFactory.getLogger(JsonModify.class);
	public static void main(String[] args) {
//		if(args==null||args.length!=2){
//			logger.error("paragram  num  is wrong ...need num=2");
//			return;
//		}
		String[] tmps=new String[]{"/Users/jorsun/work/workspace/hqhl/bs/branches/release_zh/client/client_res/Resources/ui"
				,"/Users/jorsun/work/workspace/hqhl/bs/branches/release_zh/client/client_res/Resources/ui"};
//		String[] tmps=new String[]{"/Users/jorsun/work/workspace/hqhl/bs/trunk/client/client_ui/Json"
//				,"/Users/jorsun/work/workspace/hqhl/bs/trunk/client/client_res/Resources/ui"};
		
		File dir = new File(tmps[0]);
		if (dir.isDirectory()) {
		   Set<LabelTextModel> labelTextModels=new HashSet<LabelTextModel>();
			for (File file : dir.listFiles()) {
				if (!file.isDirectory() && file.getPath().endsWith(".json")) {
					JSONObject jsonObject=	loadJson(file);
//					System.out.print(file.getName()+":");
					modifyJson(jsonObject);
					String outPath=file.getName();
					//saveJson(args[1]+"/"+outPath,jsonObject);
					JSONObject obj=jsonObject.getJSONObject("widgetTree");
					findChildLabel(file,obj,labelTextModels);
				}
				
			}
			File file=new File("/Users/jorsun/work/workspace/xxxxx/Rename.json");
			if(file.exists()){
				file.delete();
			}
			try {
				FileUtils.writeStringToFile(file,
						JSON.toJSONString(labelTextModels), "utf8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JsonToExcelLoader.toExcel("/Users/jorsun/work/workspace/xxxxx","Rename.xls",JSON.toJSONString(labelTextModels));
		}
	}
	private static void saveJson(String filePath,JSONObject jsonObject){
		File file=new File(filePath);
		if(file.exists()){
			file.delete();
		}
		try {
			FileUtils.writeStringToFile(file,
					jsonObject.toJSONString(), "utf8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private static JSONObject  modifyJson(JSONObject jsonObject){
		JSONArray textures=jsonObject.getJSONArray("textures");
		JSONObject obj=jsonObject.getJSONObject("widgetTree");
		Set<String> usePlist=new HashSet<String>();
		findChildUsePlist(obj, usePlist);
		Iterator  iterator=textures.iterator();
		while (iterator.hasNext()) {
			String plist = (String) iterator.next();
			boolean isUsed=false;
			for(String str:usePlist){
				if(plist.indexOf(str)>=0){
					isUsed=true;
					break;
				}
			}
			if(!isUsed){
				iterator.remove();
			}
		}
		//System.out.println(JSON.toJSONString(textures));
		return jsonObject;
	}
	
	private static void findChildLabel(File file,	JSONObject jsonObject,Set<LabelTextModel> textModels ){
		String jsonName=file.getName();
		JSONArray children=jsonObject.getJSONArray("children");
		if(children!=null){
			for(int i=0;i<children.size();i++){
				JSONObject object=children.getJSONObject(i);
				JSONObject options=	object.getJSONObject("options");
				if(options.getString("classname").equalsIgnoreCase("Label")
				   ||options.getString("classname").equalsIgnoreCase("Button")){
					if( options.getString("text")!=null&& options.getString("text").length()>0){
						LabelTextModel model=new LabelTextModel(jsonName,options.getString("classname")
								, options.getString("name")
								, options.getString("text")
								,file.lastModified()
								,DateFormat.getDateTimeInstance().format(new Date(file.lastModified())));
						textModels.add(model);
					}
				}else if(options.getString("classname").equalsIgnoreCase("TextField")){
					if( options.getString("placeHolder")!=null&& options.getString("placeHolder").length()>0){
						LabelTextModel model=new LabelTextModel(jsonName,options.getString("classname")
								, options.getString("name")
								, options.getString("placeHolder")
								,file.lastModified()
								,DateFormat.getDateTimeInstance().format(new Date(file.lastModified())));
						textModels.add(model);
					}
				}
				findChildLabel(file,object, textModels);
			}
		}
	}
	private static void findChildUsePlist(JSONObject jsonObject,	Set<String> usedPlist){
		JSONArray children=jsonObject.getJSONArray("children");
		if(children!=null){
			for(int i=0;i<children.size();i++){
				JSONObject object=children.getJSONObject(i);
				JSONObject options=	object.getJSONObject("options");
				
				for(String key:	options.keySet()){
					Object object2=	options.get(key);
					if (object2 instanceof JSONObject) {
						JSONObject plist=(JSONObject)object2;
						String plistFile=plist.getString("plistFile");
						if(!StringUtils.isEmpty(plistFile)){
							usedPlist.add(plistFile);
						}
					}
					
				}
//				JSONObject fileNameData=options.getJSONObject("fileNameData");
//				if(fileNameData!=null){
//					String plistFile=fileNameData.getString("plistFile");
//					if(!StringUtils.isEmpty(plistFile)){
//						usedPlist.add(plistFile);
//					}
//				}
//				JSONObject normalData=options.getJSONObject("normalData");
//				if(normalData!=null){
//					String plistFile=normalData.getString("plistFile");
//					if(!StringUtils.isEmpty(plistFile)){
//						usedPlist.add(plistFile);
//					}
//				}
//				JSONObject normalData=options.getJSONObject("normalData");
//				if(normalData!=null){
//					String plistFile=normalData.getString("plistFile");
//					if(!StringUtils.isEmpty(plistFile)){
//						usedPlist.add(plistFile);
//					}
//				}
				findChildUsePlist(object, usedPlist);
			}
		}
	}
	private static JSONObject loadJson(File file){
		byte[] bytes = null;
		JSONObject json=null;
		try {
			Reader f = new InputStreamReader(new FileInputStream(file));          
		    BufferedReader fb = new BufferedReader(f);  
		    StringBuffer sb = new StringBuffer("");  
		    String s = "";  
		    while((s = fb.readLine()) != null) {  
		        sb = sb.append(s);  
		    }
		    json=JSON.parseObject(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("failed to read file " + file.getAbsolutePath());
		}
		return json;
	}
	
}
