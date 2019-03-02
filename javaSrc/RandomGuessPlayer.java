import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/** 
* @author of Assignment Savas Semirli, 2018
* 
*/

public class RandomGuessPlayer implements Player {
	
	static BufferedReader reader;
	static File configurationFile;
	
	static HashMap<String, String> character;
	static ArrayList<HashMap<String, String>> allCharacters2;
	
	Random rand; //Used for random number generation (from zero/0 (inclusive) to limit (exclusive))
	private HashMap<String, String> playerCharacterAttributes; //Stores all chosen characters data
	private ArrayList<HashMap<String, String>> allCharacters; //Stores all possible opponents characters, updating after each guess
	private String attributeToGuessValue; //The attribute to guess
	private String attributeToGuess; // The value of the attribute to guess
	private int index; //Index of character guessed
	
	//Setup player data
    public RandomGuessPlayer(String gameFilename, String chosenName) throws IOException {    
    	
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

    //Make a random guess
    public Guess guess() {
    	
    	boolean allCharactersAreClones = false; //Used to check if remaining characters have the same attribute/value pairs (not considering Name)
    	boolean guessIsValid; //Used to check if the guess removes at least 1 person
    	ArrayList<String> keySet; //Used to obtain key set of hashMap
    	rand = new Random(); //Initialize random number generator
    	int keySetIndex; //Used to select an attribute from the key set
    	
    	guessIsValid = false; //Initialize
    	
    	//Do until valid guess is decided
	    while(guessIsValid == false) {
	    	//Do if there is more than one character to choose from.
	    	if(this.allCharacters.size() > 1) {
		    	this.index = rand.nextInt(this.allCharacters.size()); //Selecting a random character
		    	keySet = new ArrayList<String>(this.allCharacters.get(this.index).keySet()); //Get key/attribute set of selected character
		    	
		    	keySetIndex = rand.nextInt(keySet.size()); //Select a random key/attribute to guess
		    	
		    	//Prevent choosing Name as there are more than 1 characters still in play.
		    	while(keySet.get(keySetIndex).equals("Name")) {
		    		keySetIndex = rand.nextInt(keySet.size());
		    	}
		    	
		    	this.attributeToGuess = keySet.get(keySetIndex); //assign key/attribute to a variable
		    	this.attributeToGuessValue = this.allCharacters.get(this.index).get(this.attributeToGuess); //assign key/attribute's value to a variable
		    	
		    	//check if guess is valid (in other words, the guess will remove at least one person)
		    	guessIsValid = isGuessValid();
		    	
		    	//Do if guess if valid
		    	if(guessIsValid == true) {
		    		return new Guess(Guess.GuessType.Attribute, this.attributeToGuess, this.attributeToGuessValue);
		    	}
		    	//else check why its not valid
		    	else {
		    		
		    		//check to see if all remaining characters are clones. Checks if this is the reason
		    		allCharactersAreClones = areAllCharactersClones();
		    		
		    		//if the reason for invalid guess is that everyone has same attributes, then just make person guess.
		    		if(allCharactersAreClones == true) {
		    			return new Guess(Guess.GuessType.Person, "", this.allCharacters.get(index).get("Name"));
		    		}
		    		//else if remaining characters aren't clones, and the guess is still invalid
		    		//this means it's just that the guess made, had an attribute where the value
		    		//of that attribute in all characters was the same, and hence would not have
		    		//removed a character, making it invalid. So, continue to reselect.
		    		else {
		    			continue;
		    		}
		    	}
	    	}
	    	//else guess the person, as there is only one possibility left
	    	else {
	    		this.index = 0;
	    		return new Guess(Guess.GuessType.Person, "", this.allCharacters.get(index).get("Name"));
	    	}
    	}
		return null;
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
	
	//Check if guess is valid
	private boolean isGuessValid() {
		int count = 0; //Used to track how many characters have the same attribute/value combination for the selected attribute ONLY
		
		//Checking
		for(int i = 0; i < this.allCharacters.size(); i++) {
			if(this.allCharacters.get(i).get(attributeToGuess).equals(attributeToGuessValue)){
				count++; //Tracking
			}
		}
		//If the number of characters who have the same attribute/value combination equals the total number of members
		if(count == this.allCharacters.size()) {
			return false;
		}
		//else it's valid
		else {
			return true;
		}
	}

	//Check if all remaining characters are the same for all but their names
	private boolean areAllCharactersClones() {
		 
		ArrayList<String> keySet1; //Used to identify the comparing set of keys data
		ArrayList<String> keySet2; //Used to identify the comparing set of values data
		ArrayList<String> valueSet1; //Used to identify the set of keys to be compared
		ArrayList<String> valueSet2; //Used to identify the set of values to be compared
		boolean allAreClones = false; //Used to determine if all characters are clones or not
		 
		keySet1 = new ArrayList<String>(this.allCharacters.get(0).keySet()); //Initialize key data to compare with
		keySet1.remove("Name"); //Remove name key as we aren't comparing names
		valueSet1 = new ArrayList<String>(this.allCharacters.get(0).values()); //Initialize value data to compare with
		valueSet1.remove(this.allCharacters.get(0).get("Name")); //Remove name value, as we aren't comparig names
		
		//Perform check
		for(int i = 1; i < this.allCharacters.size(); i++) {
			keySet2 = new ArrayList<String>(this.allCharacters.get(i).keySet()); //Initialize key data to be compared
			keySet2.remove("Name"); //Remove name key as we aren't comparing names
			valueSet2 = new ArrayList<String>(this.allCharacters.get(i).values()); //Initialize value data to be compared
			valueSet2.remove(this.allCharacters.get(i).get("Name")); //Remove name value as we aren't comparing names
			
			//Checking if ALL characters are clones
			if(keySet2.equals(keySet1) && valueSet2.equals(valueSet1)){
				allAreClones = true;
			}
			//else, return as there is still one or more characters to be removed via normal guess method.
			else {
				allAreClones = false;
				break;
			}
		}
		return allAreClones;
	 }
			
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

} // end of class RandomGuessPlayer
