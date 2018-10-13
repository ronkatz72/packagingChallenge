package com.mobiquityinc.packer.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mobiquityinc.entities.ItemEntity;
import com.mobiquityinc.entities.ParsedLineEntity;
import com.mobiquityinc.exception.APIException;

public class DefaultPackerParser implements IParsingAlgorithm {

	private static final String weightSep  = ":";
	private static final String tripletSep = "\\s+";
	private static final String valsSep    = ",";
	
	public static Float   MAX_WEIGHT      = 100.0f;
	public static Integer MAX_ITEMS_NUM   = 15;
	public static Integer MAX_PACKAGE_CAP = 100;
	
	private String line;
	private Character currencySign = null;
	private int weightFactor = 1;
	
	private final Set<Integer> idsSet = new HashSet<>();
	
	
	/*
	 * Constructors
	 */
	
	// by using the default send weight factor is 10 to the power of 2
	// i.e. weight precision of 2 decimal places after the dot
	public DefaultPackerParser(){
		this(100);
	}
		
	// receive weightFactor by which weight and max capacity will be multiply
	// this is needed in order to use the knapsack algorithm that works only with Integers
	public DefaultPackerParser(int weightFactor){
		this.weightFactor = weightFactor;
	}
	
	// use if one wishes to change the constraints
	public DefaultPackerParser(float max_weight, int max_items_num, int max_package_cap, int weightFactor){
		this(weightFactor);
		MAX_WEIGHT = max_weight;
		MAX_ITEMS_NUM = max_items_num;
		MAX_PACKAGE_CAP = max_package_cap;
	}
	
	
	
	
	/* get the max weight from the line
	 * or throw APIException if line is not in correct format
	 */	
	private Integer parseMaxWeight(final String maxWeightStr){
		Integer packageCapacity = null;
		try{
			packageCapacity = Integer.parseInt(maxWeightStr.trim());
			if (packageCapacity <= MAX_PACKAGE_CAP && packageCapacity >= 0){
				// place to print info to log
			} else {
				throw new APIException("Line: " + line + " too invalid packageCapacity " + packageCapacity);
			}
		} catch (NumberFormatException nfe){
			throw new APIException("Line: " + line + " weight is not a number");
		}
		return packageCapacity * weightFactor;
	}
	
	
	/* receive triplets list in format (index,weight,cost)
	 * return List of Item entities represents the triplets
	 * Weight value is multiply by weightFactor to allow knapsack algorithm to work
	 * 
	 * throw APIException in case an invalid triplet detected
	 */
	private List<ItemEntity> parseTriplets(final String tripletsStr, final int maxWeight){
		
		List<ItemEntity> selectedItems = new ArrayList<>();

			String[] parts = tripletsStr.trim().split(tripletSep);
			if (parts.length > MAX_ITEMS_NUM){
				throw new APIException("Line: " + line + " too many triplets, max triplets number: " + MAX_ITEMS_NUM);
			}
			for (String triplet : parts){

					String values[] = stripTriplet(triplet);
					
					if (values.length != 3){
						throw new APIException("Line: " + line + " invalie triplet found : " + tripletsStr);
					}
					
					try{
						// 1) parse id and verify there are no duplicate and id is bigger than 0
						Integer id = Integer.parseInt(values[0].trim());
						verifyId(id);

						// 2) parse weight, verify if valid and correct it by weightFactor
						Float weight = Float.parseFloat(values[1].trim());
						if (!isValidWeight(weight)){
							throw new APIException("Line: " + line + " invalie triplet found (weight is not valid): " + tripletsStr);
						}
						
						Integer correctedWeight = (int)(weight*weightFactor);
						
						// 3) parse cost, make sure cost is bigger than 0
						Integer cost = Integer.parseInt(stripCurrencySign(values[2]));
						verifyCost(cost);
						
						// 4) skip triple that it's weight is bigger than the max allowed weight
						// 	  There is no point to check this item as it will not fit the package for sure
						if (correctedWeight <= maxWeight){
							selectedItems.add(new ItemEntity(id, correctedWeight, cost));
						}
					} catch (NumberFormatException npe){
						throw new APIException("Line: " + line + " invalie triplet found: " + tripletsStr);
					}

				// 5) sort by weight to ensure item order so the result
				//    will include the lowest weight for the same price of different items combination
				//    so if items 1 & 3 weight 100, items 2 & 7 weight 80 and both
				//    combinations cost the same then items 2 & 7 will be selected
				Collections.sort(selectedItems);
			}

		
		return selectedItems;
	}
	
	private String[] stripTriplet(String str){
		String triplet = str.trim();
		if (!triplet.startsWith("(") && triplet.endsWith(")")){
			throw new APIException("Line: " + line + " invalie triplet found: " + str);
		} 
		
		String tripleVals = triplet.substring(1, triplet.length() - 1);
		return tripleVals.split(valsSep);
	}
	
	
	/* verify currency sign:
	 * first currency sign in a line should be the same for all prices
	 * of all items or otherwise we can't compare
	 * 
	 * saves the first currency sign or throws APIException if :
	 *  - New sign found does not match with the first sign found in the line
	 *  - There is not currency sign (i.e. it is a number)
	 * 
	 */
	private String stripCurrencySign(String tripletsStr){

		tripletsStr = tripletsStr.trim();
		
		Character csign = tripletsStr.charAt(0);
		
		// get first currency sign seen to verify the currency in use is the same for all items
		// this is to make sure we compare prices with the same unit
		if (currencySign == null){
			currencySign = csign;
		}
		
		if (ParsersTools.isNumeric(csign)){
			throw new APIException("Line: " + line + " invalie triplet found: " + tripletsStr);
		}
		
		if (!currencySign.equals(csign)){
			throw new APIException("Line: " + line + " different currency signs where found: " + tripletsStr);
		}
		
		return tripletsStr.substring(1).trim();
	}
	
	// Verify that item's weight is in range
	// note that a weight of 100.001 and capacity of 100 is valid
	// when precision is 2 (i.e. weightFactor = 100)
	// however, weight of 100.01 and capacity of 100 is not valid
	private boolean isValidWeight(float weight){
		if ((int)(weight*weightFactor) > (int)(MAX_WEIGHT*weightFactor) || weight <= 0)
			return false;
		else
			return true;
	}
	
	private void verifyCost(int cost){
		if (cost <= 0){
			throw new APIException("Line: " + line + " Cost must be positive");
		}
	}
	
	private void verifyId(Integer id){
		if (id < 0 || idsSet.contains(id)){
			throw new APIException("Line: " + line + " duplicate or negative id(s)");
		} else{
			idsSet.add(id);
		}
	}
	
	private void clean(){
		currencySign = null;
	}
	
	
	// Exposed method by the interface
	// Allows the user to send a line an receive back an entity represents it
	@Override
	public ParsedLineEntity parse(String line) {
		
		ParsedLineEntity parsedLineEntity = null;
		idsSet.clear();
		this.line = line;
		if (!ParsersTools.isNullOrEmpty(line)){
			String[] parts = line.split(weightSep);
			if (parts.length != 2){
				throw new APIException("Line: " + line + " is not in format <weight> : <list of items>");
			}
			clean();
			parsedLineEntity = new ParsedLineEntity();
			int maxWeight = parseMaxWeight(parts[0]);
			parsedLineEntity.setMaxWeight(maxWeight);
			parsedLineEntity.setItemsList(parseTriplets(parts[1], maxWeight));
		}
		return parsedLineEntity;
	}



}
