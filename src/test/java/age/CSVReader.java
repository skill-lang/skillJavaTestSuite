package age;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ust.skill.common.java.internal.SkillObject;

public class CSVReader {
	
	private static final int CLASS_INDEX = 0;
	private static final int FIELD_NAME_SUBINDEX = 0;
	private static final int TYPE_SUBINDEX = 1;
	private static final int VALUE_SUBINDEX = 2;

	public static void main(String[] args){
		Path path = Paths.get(System.getProperty("user.dir"), "src", "test", "resources");
		if(args.length == 0){
			path = path.resolve("values.csv");
		}

		Map<String, Object> values = new HashMap<>();
		Map<String, String> fieldTypes = new HashMap<>();

		for(String line : readCSV(path)){
			try {
				String[] tokens = line.split(";");
				String className = getClassNameFromEntry(tokens);
				createMappingFromLine(tokens, values, fieldTypes);
				SkillObject obj = SkillObjectCreator.instantiateSkillObject(className, values, fieldTypes);
				System.out.println(obj.prettyString());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println(path.toAbsolutePath().toString());
	}
	
	public static void createMappingFromLine(String[] tokens,
			Map<String, Object> values,
			Map<String, String> fieldTypes){
		for(int i = 1; i < tokens.length; i++){
			String[] subtokens = tokens[i].split(":");
			String fieldName = subtokens[FIELD_NAME_SUBINDEX];
			String value = subtokens[VALUE_SUBINDEX];
			String type = subtokens[TYPE_SUBINDEX];
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
