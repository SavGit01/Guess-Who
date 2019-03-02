import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class dataGen {

	public static void main(String[] args) throws IOException {
		
		ArrayList<ArrayList<String>> allAttributes;
		ArrayList<String> attribute;
		String[] intermediate;
		BufferedReader reader;
		BufferedWriter writer;
		File file;
		String line;
		int numberOfCharacters;
		
		line = null;
		
		allAttributes = new ArrayList<ArrayList<String>>();
		file = new File("myGame.config");
		reader = new BufferedReader(new FileReader("game1.config"));
		writer = new BufferedWriter(new FileWriter(file));
		
		
		try {
			line = reader.readLine();
		} catch (IOException e) {
			System.out.println("Exception Caught: Could Not Read Line");
			e.printStackTrace();
		}

		
		while(!line.isEmpty()) {
			attribute = new ArrayList<String> ();
			intermediate = line.split(" ");
			
			for(int i = 0; i < intermediate.length; i++) {
				attribute.add(intermediate[i]);
			}
			
			allAttributes.add(attribute);
			
			try {
				line = reader.readLine();
			} catch (IOException e) {
				System.out.println("Exception Caught: Could Not Read Line");
				e.printStackTrace();
			}
		}

		reader.close();
		
		writer.write("aaaaaaaaaaaaaaa \n\n");
		
		numberOfCharacters = 100;
		
		String att;
		String val;
		Random rand = new Random();
		int index;
		
		for(int i = 1; i < numberOfCharacters+1; i++) {
			
			writer.write("P"+i+ "\n");
			
			for(int j = 0; j < allAttributes.size(); j++) {
				
				System.out.println(allAttributes);
				System.out.println(allAttributes.get(j).get(0));
				att = allAttributes.get(j).get(0);
				
				
				index = rand.nextInt(allAttributes.get(j).size());
				while(index == 0) {
					index = rand.nextInt(allAttributes.get(j).size());
				}
				val = allAttributes.get(j).get(index);
				
				writer.write(att + " " + val + "\n");
			}
			writer.write("\n");
		}
		
		writer.close();
	}

}
