package age;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVReader {
	
	private static final int CLASS_INDEX = 0;

	public static void main(String[] args){
		
	}
	
	public static void createMappingFromLine(String[] tokens,
			Map<String, Object> values,
			Map<String, String> fieldTypes){
		for(int i = 1; i < tokens.length; i++){
			String[] subtokens = tokens[i].split(":");
			String fieldName = subtokens[0];
			String value = subtokens[1];
			String type = subtokens[2];
			values.put(fieldName, SkillObjectCreator.valueOf(type, value));
			fieldTypes.put(fieldName, type);
		}
	}

	public static String getClassNameFromEntry(String[] tokens){
		if(tokens.length > 0){
			return tokens[CLASS_INDEX];
		}else{
			return null;
		}
	}
	
	public static List<String> readCSV(Path path){
		List<String> content = new ArrayList<>();
		try{
			Files.lines(path).forEach( (String line) -> {
				content.add(line);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
}
