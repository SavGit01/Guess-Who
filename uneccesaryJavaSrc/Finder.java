import java.util.ArrayList;
import java.util.HashMap;

public class Finder {
	
	static double count;
	static double totalCount;
	static double highestCount;
	static double halfOfPopulation;
	static String currAtt;
	static String currAttVal;
	static String bestAttToGuess;
	static String bestAttToGuessVal;
	static ArrayList<String> attributes;
	static ArrayList<Double> result;
	static ArrayList<ArrayList<Double>> allResults;
	
	
	public static Guess findBestGuess(ArrayList<HashMap <String, String>> allCharacters) {
		
		halfOfPopulation = allCharacters.size()/2;
		allResults = new ArrayList<ArrayList<Double>>();
		attributes = new ArrayList<String>(allCharacters.get(0).keySet());
		attributes.remove("Name");
		
		//Pick an attribute
		for(int i = 0; i < attributes.size(); i++) {
			totalCount = 0;
			result = new ArrayList<Double>();
			currAtt = attributes.get(i);
			
			//Assume your opponent is this character:
			for(int j = 0; j < allCharacters.size(); j++) {
				count = 0;
				currAttVal = allCharacters.get(j).get(currAtt);
				//Check how many people you would remove if you guessed this combination for this person
				for(int k = 0; k < allCharacters.size(); k++) {
					
					/**alternate ! for checking for removals or matched*/
					
					if(currAttVal.equals(allCharacters.get(k).get(currAtt))){
						count++; //Number of characters removed
					}
				}
				if(count != allCharacters.size()) {
					result.add(count);
					totalCount += count;
				}
			}
			result.add(totalCount);
			allResults.add(result);
			
		}

		double highestScoreForAttribute = 0;
//		double highestScoreForValue = 0;
		
//		int indexOfHighestScoringValue = 0;
		int indexOfValueToGuess = allCharacters.size()+1;
		
		//go through all result arraylists
		for(int i = 0; i < allResults.size(); i++) {
		
			/**
			 * allResults.get(0) = the first attribute
			 * allResults.get(0).get(0) = the first result value for that attribute
			 */
			//find the highest scoring array list
			if(allResults.get(i).get(allResults.get(i).size()-1) > highestScoreForAttribute) {
				highestScoreForAttribute = allResults.get(i).get(allResults.get(i).size()-1);
				bestAttToGuess = attributes.get(i);
				
				// X find the highest score in that array list
				//Find the score thats equals halfPopulation
				for(int j = 0; j < allResults.get(i).size()-1; j++) {
					if(allResults.get(i).get(j) != allCharacters.size()) {
//						if(allResults.get(i).get(j) > highestScoreForValue) {
//							highestScoreForValue = allResults.get(i).get(j);
//							indexOfHighestScoringValue = j;
//						}
						if(allResults.get(i).get(j) == halfOfPopulation) {
		
							indexOfValueToGuess = j;
							System.out.println("found one that equals half");
							break;
						}
						else {
							continue;
						}
						
					}
				}
				
				double difference = allResults.get(i).size();
				
				
				if(indexOfValueToGuess > allCharacters.size()) {
					//find the score closest to half population
					for(int j = 0; j < allResults.get(i).size()-1; j++) {
						if(allResults.get(i).get(j) != allCharacters.size()) {
//							if(allResults.get(i).get(j) > highestScoreForValue) {
//								highestScoreForValue = allResults.get(i).get(j);
//								indexOfHighestScoringValue = j;
//							}
							if(allResults.get(i).get(j) != halfOfPopulation) {
								
								if(allResults.get(i).get(j) < halfOfPopulation) {
									if((halfOfPopulation - allResults.get(i).get(j)) < difference){
										difference = halfOfPopulation - allResults.get(i).get(j);
										indexOfValueToGuess = j;
										System.out.println("found one close to half 1");
										break;
									}
								}
								else {
									
									if(allResults.get(i).get(j) > halfOfPopulation) {
										if((allResults.get(i).get(j) - halfOfPopulation) < difference){
											difference = allResults.get(i).get(j) - halfOfPopulation;
											indexOfValueToGuess = j;
											System.out.println("found one close to half 2");
											break;
										}
									}
									
								}
			
								
							}
							else {
								continue;
							}
							
						}
					}
				}
				else {
					break;
				}
			}	
			
		}
		
		bestAttToGuessVal = allCharacters.get(indexOfValueToGuess).get(bestAttToGuess);
		
		return new Guess(Guess.GuessType.Attribute, bestAttToGuess, bestAttToGuessVal);
	}

}
