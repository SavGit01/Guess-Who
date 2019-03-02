import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
* @author of Assignment Savas Semirli, 2018
* 
*/

public class BinaryGuessPlayer implements Player {

	private HashMap<String, String> playerCharacterAttributes; //Stores all chosen characters data
	private ArrayList<HashMap<String, String>> allCharacters; //Stores all possible opponents characters, updating after each guess
	static File configurationFile; //The configuration file
	private int index; //Index of character guessed
	
	//Setup player data
    public BinaryGuessPlayer(String gameFilename, String chosenName) throws IOException {    	
    	
    	//Open file to read from.
    	configurationFile = new File(gameFilename);
    	
    	//Get and store all possible characters the opponent might have
		this.allCharacters = get();
		
		//Get and store character(chosenName) data
		for(int i = 0; i < this.allCharacters.size(); i++) {
			if(this.allCharacters.get(i).get("Name").equals(chosenName)) {
				this.playerCharacterAttributes = this.allCharacters.get(i);
			}
		}
    } // end of RandomGuessPlayer()

    //Make a binary guess
    public Guess guess() {
   			
    	//If there are more than 1 character to choose from, find and make the best binary attribute guess
    	if(this.allCharacters.size() > 1) {
    		
    		//Set index if the guess to be made is a Person guess. See findBestGuess() comments for further explanation
    		Guess guessToMake = findBestGuess(this.allCharacters);
    		if(guessToMake.getType() == Guess.GuessType.Person) {
    			for(int i = 0; i < allCharacters.size(); i++) {
    				if(guessToMake.getValue().equals(this.allCharacters.get(i).get("Name"))) {
    					index = i;
    				}
    			}
    		}
    		//Returns best guess for this round.
    		return findBestGuess(this.allCharacters);
    	}
    	//If there is only 1 character to choose from, guess that persons name
    	else {
    		this.index = 0;
    		return new Guess(Guess.GuessType.Person, "", this.allCharacters.get(0).get("Name"));
    	}
    } // end of guess()

    //Answer a guess made to you
	public boolean answer(Guess currGuess) {
		
		//Answer for a person guess
    	if(currGuess.getType().equals(Guess.GuessType.Person)) {
    		if(currGuess.getValue().equals(this.playerCharacterAttributes.get("Name"))) {
    			return true;   		
    		}
    		else {
    			return false;
    		}
    	}
    	
    	//Answer for an attribute guess
		if(this.playerCharacterAttributes.get(currGuess.getAttribute()).equals(currGuess.getValue())) {
			return true;
		}
		else {
			return false;
		}
    } // end of answer()

	//Update player data based on answer given to you for a guess you made
	public boolean receiveAnswer(Guess currGuess, boolean answer) {
		
		ArrayList<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
		
		//If you guessed correct person
		if(currGuess.getType() == Guess.GuessType.Person && answer == true) {
			//Keep correct person, and remove the rest, then return true
			temp.add(this.allCharacters.get(this.index));
			this.allCharacters.removeAll(this.allCharacters);
			this.allCharacters.addAll(temp);
			return true;
    	}
		//If you guessed incorrect person
		else if(currGuess.getType() == Guess.GuessType.Person && answer == false){
			//Remove the incorrect person guessed
			this.allCharacters.remove(index);
			return false;
		}
		//If you guessed correct attribute
		else if(currGuess.getType() == Guess.GuessType.Attribute && answer == true) {
			//Find character who does NOT have the attribute/value pair you guessed, and remove them
			for(int i = 0; i < this.allCharacters.size(); i++) {
				if(!this.allCharacters.get(i).get(currGuess.getAttribute()).equals(currGuess.getValue())) {
					this.allCharacters.remove(i);
					i--;
				}
			}
			return false;
		}
		//If you guessed incorrect attribute
		else if(currGuess.getType() == Guess.GuessType.Attribute && answer == false) {
			//Find character who DOES have the attribute/value pair you guessed, and remove them
			for(int i = 0; i < this.allCharacters.size(); i++) {
				if(this.allCharacters.get(i).get(currGuess.getAttribute()).equals(currGuess.getValue())) {
					this.allCharacters.remove(i);
					i--;
				}
			}
			return false;
		}
		//If invalid answer conditions occur
		else {
			System.out.println("Error: Received Answer conditions are invalid");
			return false;
		}
    } // end of receiveAnswer()
	
	//Generic read method to return all characters and their data
	public static ArrayList<HashMap<String, String>> get() {
		
		ArrayList<HashMap<String, String>> getAllCharacters; //Store characters
		HashMap<String, String> character; //Store read in character data
		BufferedReader reader; //Reader file
		String line; //Store read line
		int count; //Used to track what stage of character data reading we are at
		boolean addedLastCharacter;
		
		getAllCharacters = new ArrayList<HashMap<String, String>>(); //Initialize
		character = null; //Initialize
		reader = null; //Initialize
		line = null; //Initialize
		count = 0; //Initialize
		addedLastCharacter = false; //Initialize
		
		//Open new buffered reader
		try {
			reader = new BufferedReader(new FileReader(configurationFile)); //Initialize
		} catch (FileNotFoundException e) {
			System.out.println("Exception Caught: Configuration File Not Found");
			e.printStackTrace();
		}
		
		//Read line
		try {
			line = reader.readLine();
		} catch (IOException e) {
			System.out.println("Exception Caught: Could Not Read Line");
			e.printStackTrace();
		}

		
		/**PLEASE NOTE! The configuration file must have a structure like:
		 * 
		 * =================
		 * GLOBAL ATTRIBUTE
		 * ---
		 * (Empty Line)
		 * PLAYER 1
		 * --- Data
		 * (Empty Line)
		 * PLAYER 2
		 * --- Data
		 * (Empty Line)
		 * PLAYER 3
		 * --- Data
		 * =================
		 * 
		 * Otherwise it will skip the first character.
		 * I could have made it so that it reads it, and just stores the first value of the attribute 
		 * as the attribute/value pair, but this would affect the population halving approach with the binary player.
		 * And so, I have made it that only characters are read, skipping the global attributes.
		 */
		
		
		//This is used to skip over the initial global attributes, as we do not need them
		while(!line.isEmpty()) {
			try {
				line = reader.readLine();
			} catch (IOException e) {
				System.out.println("Exception Caught: Could Not Read Line");
				e.printStackTrace();
			}
		}
		
		//After finding the first empty line, read another line, and start the character data read/store process
		try {
			line = reader.readLine();
		} catch (IOException e) {
			System.out.println("Exception Caught: Could Not Read Line");
			e.printStackTrace();
		}
		
		//Loop until line is null
		while(line != null) {
			
			addedLastCharacter = false;
			
			//If this is the start of a new character, the first line will be it's name. So read and assigned that.
			if(count == 0) {
				character = new HashMap<String, String>();
				character.put("Name", line);
				count = 1;
			}
			//If this is not the start of a new character, read in attribute/value pairs.
			else {
				String[] attributes = line.split(" ");
				character.put(attributes[0], attributes[1]);
			}
			
			//Read line
			try {
				line = reader.readLine();
			} catch (IOException e) {
				System.out.println("Exception Caught: Could Not Read Line");
				e.printStackTrace();
			}
			
			//If the line is not null, but it is empty, indicating there may be more to come.
			if(line != null) {
				if(line.isEmpty()) {
					getAllCharacters.add(character); //Add read in character array to the main array of characters.
					addedLastCharacter = true;
					count = 0; //Reset tracker for start of new character
					
					//Read line
					try {
						line = reader.readLine();
					} catch (IOException e) {
						System.out.println("Exception Caught: Could Not Read Line");
						e.printStackTrace();
					}
				}	
			}
		}
		
		//If last character read, could not be added to  main array of characters due to line==null default exit, then add it now.
		if(addedLastCharacter == false) {
			getAllCharacters.add(character);
			count = 0; //Reset tracker for start of new character
		}
		
		//Read line
		try {
			line = reader.readLine();
		} catch (IOException e) {
			System.out.println("Exception Caught: Could Not Read Line");
			e.printStackTrace();
		}
		
		//Close reader
		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("Exception Caught: Could Not Close Reader");
			e.printStackTrace();
		}
		
		//Return accumulated array of characters
		return getAllCharacters;
	}//end of get()
		
	//Finds a guess that halves the population (specific to binary player).
	//Also, it will return a person guess, if guessing any attribute will not
	//remove a character (example: they are all clones with different names).
	public static Guess findBestGuess(ArrayList<HashMap <String, String>> allCharacters) {
		
		double count; //score of the value of an attribute
		double totalCount; //Running total of an attribute score
		double halfOfPopulation; //Half of the population size
		double difference; //Used to find the attribute that removes closest to the half of a population as possible
		int indexOfValueToGuess; //Index of the value of an attribute
		double highestScoreForAttribute; //Used to determine the highest scoring attribute (the most fruitful attribute)
		String currAtt; //The attribute currently under scoring
		String currAttVal; //Value of attribute currently under scoring 
		String bestAttToGuess; //Best attribute to guess
		String bestAttToGuessVal; //Value of the best attribute to guess
		ArrayList<String> attributes; //All possible attributes stored here
		ArrayList<Double> result; //Store the number of characters an attribute removes for each possible character the opponent might have
		ArrayList<ArrayList<Double>> allResults; //Store all results, each index, if the index of the character
		
		halfOfPopulation = (double)allCharacters.size()/2;//Initialize
		allResults = new ArrayList<ArrayList<Double>>();//Initialize
		attributes = new ArrayList<String>(allCharacters.get(0).keySet());//Initialize
		attributes.remove("Name");//Initialize
		bestAttToGuess = null;//Initialize
		
		//Iterate through all attributes
		for(int i = 0; i < attributes.size(); i++) {
			totalCount = 0; //Initialize
			result = new ArrayList<Double>(); //Initialize
			currAtt = attributes.get(i); //Initialize
			
			//For each attribute, iterate through all characters
			//Assuming you opponent is character j, get the attribute value
			//from them, and guess that attribute value/pair to see
			//how many characters that guess would remove.
			for(int j = 0; j < allCharacters.size(); j++) {
				count = 0; //Initialize
				currAttVal = allCharacters.get(j).get(currAtt); //Initialize
				
				//Check how many people you would remove
				for(int k = 0; k < allCharacters.size(); k++) {
					
					/**alternate "!" for checking for removals or matches*/
					if(currAttVal.equals(allCharacters.get(k).get(currAtt))){
						count++; //Iterate each time a character is removed
					}
				}
				//As long as the count doesn't equal total number of characters,
				//in other words, everyone has this att/val pair, so it's invalid.
				//Add the count to the result list of the player you assumed the opponent was.
				if(count != allCharacters.size()) {
					result.add(count);
					totalCount += count; //Add count to totalCount, to track the total score, if we assumed this character was our opponent
				}
			}
			result.add(totalCount); //Finally add totalCount to the result list of the player you assumed the opponent was.
			allResults.add(result); //And add result to allResults list.
			
		}

		highestScoreForAttribute = 0; //Initialize
		indexOfValueToGuess = allCharacters.size()+1; //Initialize
		
		//go through all result arraylists
		for(int i = 0; i < allResults.size(); i++) {
		
			/**Note:
			 * Example:
			 * allResults.get(0) = the first attribute
			 * allResults.get(0).get(0) = the first result value for that attribute
			 */
			
			//Find the highest scoring list, of which it's index, is the index of the character we assumed our opponent was
			//This approach would increase our chances of picking an attribute value guess pair, from the most fruitful results list.
			if(allResults.get(i).get(allResults.get(i).size()-1) > highestScoreForAttribute) {
				highestScoreForAttribute = allResults.get(i).get(allResults.get(i).size()-1);
				bestAttToGuess = attributes.get(i);
				
				difference = allResults.get(i).size()+1; //Initialize
				
				//Find the attribute that has the removal score closest to half the 
				//population, from the most fruitful result array selected above
				if(indexOfValueToGuess > allCharacters.size()) {
					//find the score closest to half population
					for(int j = 0; j < allResults.get(i).size()-1; j++) {
						//If this particular attribute removal score is NOT equal to the number of characters, then do
						if(allResults.get(i).get(j) != allCharacters.size()) {
							
							//If removal score is less than or equal to half of population
							if(allResults.get(i).get(j) <= halfOfPopulation) {
								if((halfOfPopulation - allResults.get(i).get(j)) < difference){
									difference = halfOfPopulation - allResults.get(i).get(j);
									indexOfValueToGuess = j;
									break;
								}
							}
							//If removal score is equal to or greater than half of population
							else {
								
								if(allResults.get(i).get(j) >= halfOfPopulation) {
									if((allResults.get(i).get(j) - halfOfPopulation) < difference){
										difference = allResults.get(i).get(j) - halfOfPopulation;
										indexOfValueToGuess = j;
										break;
									}
								}
							}
						}
						//Else select differect attribute
						else {
							continue;
						}
					}
				}
				else {
					break;
				}
			}	
		}
		//In the case that all characters are clones, with different names, systematically guess the names starting from 1st character.
		if(indexOfValueToGuess > allCharacters.size()) {
			return new Guess(Guess.GuessType.Person, "", allCharacters.get(0).get("Name"));
		}
		//Make the attribute/value guess determined
		else {
			bestAttToGuessVal = allCharacters.get(indexOfValueToGuess).get(bestAttToGuess);
			
			return new Guess(Guess.GuessType.Attribute, bestAttToGuess, bestAttToGuessVal);
		}
	}//end of findBestGuess()

} // end of class BinaryGuessPlayer
