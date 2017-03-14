package age;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

	public static void main(String[] args){
		
	}
	
	public static List<String> readCSV(Path path){
		List<String> content = new ArrayList<>();
		try{
			Files.lines(path).forEach( (String line) -> {
				content.add(line);
			});
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}
		return null;
	}
	
}
