package xgame.tools.util;

import java.io.File;
import java.io.IOException;

import net.sf.json.JSONArray;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public final class JsonUtil {
	public static String jsonBeautifier(JSONArray originalJsonArray){
		ObjectMapper objectMapper = new ObjectMapper(); 
		objectMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);  
		String formattedJson = "";
		try {
			JsonNode tree= objectMapper.readTree(originalJsonArray.toString());
			formattedJson = objectMapper.writeValueAsString(tree);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		
		return formattedJson;
	}
	
	public static JsonNode toJsonArray(String jsonString){
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
			JsonNode rootNode = objectMapper.readValue(jsonString, JsonNode.class);
			

			return rootNode;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
