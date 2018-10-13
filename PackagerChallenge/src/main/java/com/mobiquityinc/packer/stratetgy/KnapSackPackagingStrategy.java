package com.mobiquityinc.packer.stratetgy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mobiquityinc.entities.ItemEntity;

/*
 * Knapsack algorithm implementation
 * see https://en.wikipedia.org/wiki/Knapsack_problem for details about the algorithm
 * 
 */

public class KnapSackPackagingStrategy implements IPackagingAlgorithm {
	
	private List<ItemEntity> removeTooBigItems(final List<ItemEntity> itemsList, final int capacity){
		
		List<ItemEntity> valieItems = new ArrayList<>();
		
		for (ItemEntity item :itemsList){
			if (item.getWeight() <= capacity)
				valieItems.add(item);
		}
		
		return valieItems;
	}
	
	@Override
	public List<ItemEntity> selectItems(final List<ItemEntity> itemsList, final int capacity) {
		
		// in case input is not applicable for calculation
		// return empty list as a result
		// not applicable : empty or null itemList or negative capacity
		if (!isCalculationRequired(itemsList, capacity)){
			return Collections.emptyList();
		}
		
		// make sure there are no items with larger weight than the capacity
		// to avoid unnecessary calculations
		List<ItemEntity> validItems = removeTooBigItems(itemsList, capacity);
		if (validItems.isEmpty()){
			return validItems;
		}
		
		// calculate matrix and return selected items
		ItemEntity[] items = validItems.toArray(new ItemEntity[validItems.size()]);
		return findOptimalChoise(items, capacity);
	}
	
	private boolean isCalculationRequired(List<ItemEntity> itemsList, int capacity){
		if (itemsList == null   ||
			itemsList.isEmpty() ||
			capacity < 0 ){
			return false;
		} else
			return true;
	}
	
	private List<ItemEntity> findOptimalChoise(ItemEntity[] items, Integer capacity) {

		    // knapsack matrix
		    int[][] matrix = createMatrix(items.length, capacity);
		    
		    // perform iterative knapsack algorithm (dynamic programming)
		    int maxValue = calculateMaxValue(matrix, items, items.length, capacity);
		    
		    return calculateSelectedItems(matrix, items.length, items, capacity, maxValue);
		}
	
	private int[][] createMatrix(int numOfItems, int capacity){
		int[][] matrix = new int[numOfItems + 1][capacity + 1];
	    for (int i = 0; i <= capacity; i++)
		      matrix[0][i] = 0;
	    return matrix;
	}
	
	private int calculateMaxValue(int[][] matrix, ItemEntity[] items, int numOfItems, int capacity){
	    // we iterate on items
	    for (int i = 1; i <= numOfItems; i++) {
	      // we iterate on each capacity
	      for (int j = 0; j <= capacity; j++) {
	        if (items[i - 1].getWeight() > j)
	          matrix[i][j] = matrix[i-1][j];
	        else
	          // we maximize value at this rank in the matrix
	          matrix[i][j] = Math.max(matrix[i-1][j], matrix[i-1][j - items[i-1].getWeight()] 
					  + items[i-1].getCost());
	      }
	    }
	    
	    return matrix[numOfItems][capacity];
	}
	
	private List<ItemEntity> calculateSelectedItems(int[][] matrix, int numOfItems,
											  ItemEntity[] items, int capacity,
											  int maxValue){
		
		  List<ItemEntity> choosenItems = new ArrayList<>();

		  for (int i = numOfItems; i > 0  &&  maxValue > 0; i--) {
		     if (maxValue != matrix[i-1][capacity]) {
		    	 // add item to package
		         choosenItems.add(items[i-1]);
		        
		        // remove items value and weight - item is in package
		         maxValue -= items[i-1].getCost();
		         capacity -= items[i-1].getWeight();
		      }
		    }  
	  
		 return choosenItems;
	}
}
