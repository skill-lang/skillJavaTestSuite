package age;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
	
	private static final int CLASS_INDEX = 0;

	public static void main(String[] args){
		
	}
	
	public static String getClassNameFromLine(String line){
		String[] tokens = line.split(";");
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
