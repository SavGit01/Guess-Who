import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ReadFile {
	
	static BufferedReader reader;
	static File configurationFile;
	
	static HashMap<String, String> character;
	static ArrayList<HashMap<String, String>> allCharacters;
	
	//Open file to read from.
	public static void setConfigurationFile(String filename) throws FileNotFoundException {
		configurationFile = new File(filename);
	}
	
	//Generic read method to return all characters and their data
	public static ArrayList<HashMap<String, String>> get() {
		
		String line = null;
		int count;
		
		allCharacters = new ArrayList<HashMap<String, String>>();
		count = 0;

		try {
			reader = new BufferedReader(new FileReader(configurationFile));
		} catch (FileNotFoundException e) {
			System.out.println("Exception Caught: Configuration File Not Found");
			e.printStackTrace();
		}
		
		try {
			line = reader.readLine();
		} catch (IOException e) {
			System.out.println("Exception Caught: Could Not Read Line");
			e.printStackTrace();
		}

		while(!line.isEmpty()) {
			try {
				line = reader.readLine();
			} catch (IOException e) {
				System.out.println("Exception Caught: Could Not Read Line");
				e.printStackTrace();
			}
		}
		
		try {
			line = reader.readLine();
		} catch (IOException e) {
			System.out.println("Exception Caught: Could Not Read Line");
			e.printStackTrace();
		}
		
		while(line != null) {
			
			if(count == 0) {
				character = new HashMap<String, String>();
				character.put("Name", line);
				count = 1;
			}
			else {
				String[] attributes = line.split(" ");
				character.put(attributes[0], attributes[1]);
			}
			
			try {
				line = reader.readLine();
			} catch (IOException e) {
				System.out.println("Exception Caught: Could Not Read Line");
				e.printStackTrace();
			}
			
			if(line != null) {
				if(line.isEmpty()) {
					allCharacters.add(character);
					count = 0;
					try {
						line = reader.readLine();
					} catch (IOException e) {
						System.out.println("Exception Caught: Could Not Read Line");
						e.printStackTrace();
					}
				}	
			}
		}
		
		allCharacters.add(character);
		count = 0;
		
		try {
			line = reader.readLine();
		} catch (IOException e) {
			System.out.println("Exception Caught: Could Not Read Line");
			e.printStackTrace();
		}

		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("Exception Caught: Could Not Close Reader");
			e.printStackTrace();
		}
		
		return allCharacters;
	}
	
}
