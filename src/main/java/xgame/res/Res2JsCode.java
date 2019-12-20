package xgame.res;

import java.io.File;

public class Res2JsCode {
	static String path="/Users/jorsun/work/workspace/hqhl/bs/trunk/client/BlackSails/"; 
	public static void main(String args[]) {
		File dir = new File(path+"res");
		findFile(dir);
	}
	private static void findFile(File file){
		if (!file.isDirectory()) {
			if(!file.getName().endsWith(".DS_Store")){
				String url=file.getAbsolutePath().replaceAll(path, "");
				String key=url.replaceAll("\\u002E","_");
				key=key.replaceAll("/", "_");
				System.out.println(key+":\""+url+"\",");
			}
		}else{
			if (file.isDirectory()) {
				for (File file2 : file.listFiles()) {
					findFile(file2);
				}
			}
		}
	}
	
}
