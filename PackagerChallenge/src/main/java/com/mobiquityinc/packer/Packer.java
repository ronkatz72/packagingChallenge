package com.mobiquityinc.packer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.mobiquityinc.com.viewer.DefaultPackagingViewer;
import com.mobiquityinc.entities.ParsedLineEntity;
import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.controller.LineParserController;
import com.mobiquityinc.packer.controller.PackerController;
import com.mobiquityinc.packer.parsers.DefaultPackerParser;
import com.mobiquityinc.packer.stratetgy.KnapSackPackagingStrategy;

/***
 * 
 * @author ron.katz72@gmail.com
 *
 * Package Challenge
 * 
 * This program accept as its first argument a path to a filename.
 * The input file contains several lines, each representing a test case.
 * 
 * Line structure: "Capacity : (id1,w1,Sc1) (id2,w2,Sc2) ... (idn,wn,Scn)"
 * 
 * Where: Capacity is the capacity of the package
 *        idm is the mth item's id - integer
 *        wm  is the mth item's weight - float (see below about its precision)
 *        S   is the currency sign of the cost (must be the same for all items)
 *        cm  is the mth item's cost - integer
 *        
 *  Output: The program return a String with line per each input line.
 *  		for each set of items with a capacity the respective line in the output
 *  		includes the selected items that we choose to put into the box such that
 *          the items will me with minimum weight and maximum value.
 *          Example: 
 *		100 : (1,100.00,$40) (2,100.00,$50) (3,50.00 $100) (4,50.00,$100)
 *          	will return : 3,4 since those two items together weight 100 but their cumulative value is 200$
 *              Choosing item1 or item2 will result in same package weight but its value will be less than 200$
 *          
 *          In case no item can be added to the packe
 *          as in the following example input:
 *          100 : (1,200.00,$50)
 *          The result output line will include only a hyphen (-) 
 *          
 *  Exception: In case input is not valid, com.mobiquityinc.excption.APIException is being thrown.
 *  
 *  Constraints:
 *  
 *	 MAX WEIGHT              = 100
 *	 MAX ITEMS NUMBER        =  15
 *	 MAX PACKAGE CAPACITY    = 100
 *
 *   The program can be used by calling com.mobiquityinc.packer.Packer static method:
 *	 String pack(String filepath);
 *   Where filepath is an absolute file path to the input file
 *   The method return the results as a String as explained above.
 *   
 *   Solution:
 *   
 *   Patterns used in this solution:
 *   
 *   1) Strategy pattern:
 *   	Controllers are injected with concrete implementation of an interface
 *   	This makes it easy to change implementation.
 *   	In our case: the controller is not aware of the parsing or packaging algorithm
 *   	This usually done by Spring for us however, I didn't use here Spring framework
 *   	and kept the code basic
 *   
 *   2) MVC - Model Viewer Controller
 *   	The Packer class contains two controllers: Parser & Packer
 *   	The controllers decide what to do with the information received.
 *   	In our case the information received from a file but this can be changed easily since the
 *   	controllers are not aware where from the information arrives.
 *   	The controllers sends the information to be processed in the model which in turn
 *   	informs the Viewer. The Viewer in our case is just a class which create a string of ids
 *   	from the selected items. the Packer class then use this view to create the final String answer.
 *   
 *   	Algorithm:
 *   	This problem is a classic Knapsack:
 *   	https://en.wikipedia.org/wiki/Knapsack_problem
 * 		
 * 		Therefore I used this algorithm to solve the problem
 * 		The implementation of the algorithm can be found in the class KnapSackPackagingStrategy 
 * 		which implements the interface IPackagingAlgorithm.
 * 
 * 		There is one twist in this problem as the weight is not given as an integer
 * 		To overcome this obstacle I assumed a constant precision for the weight number
 * 		In the example given with this challenge there were two digits after the decimal dot
 * 		Therefore the precision in the example is 2 and by multiply each weight by 10 to the power of 2
 * 		We receive a valid knapsack problem.
 * 
 * 		The precision can be changed by using the designated Packer constructor which takes
 * 		as a parameters a filepath as well as an integer representing the weight precision
 * 		By using the Packer constructor which received only a filepath
 * 		the precision is set to 2 as a default.
 * 		The computed weightFactor is then transfered to the parser implementation
 * 		which uses it to correct the data (see documentation in the parser class itself: DefaultPackerParser)
 * 
 * 		Complexity
 *  	Time  Complexity: O(nW)
 *  	Space Complexity: O(nW)
 *  
 *  	Where: W is the max capacity fixed by the weight factor
 *  		   So, if capacity = w and the precision is 2 then W = 100*w
 *  
 *  		   n = number of items.
 *  
 * 		Build:
 * 		Standard maven - use 'mvn clean install' from root directory
 *
 */

public class Packer {
	
	private final String         filePath;
	private LineParserController parserController;
	private PackerController     packagerController;
	private int weightFactor = 1;
	
	public Packer(String filePath){
		this(filePath,2);
	}
	
	public Packer(String filePath, int weightPrecision){
		this.filePath = filePath;
		this.weightFactor = (int) Math.pow(10d, (double)weightPrecision);
		parserController     = new LineParserController(new DefaultPackerParser(weightFactor));
		packagerController   = new PackerController(new KnapSackPackagingStrategy(),
													new DefaultPackagingViewer());
	}
	
	private String process(){
		InputStreamReader inputStream = null;
		try {
			// by using UTF-8 encoding we can handle signs such as '€'
			inputStream = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
			throw new APIException("Exception when trying to open file: " + e.getMessage());
		}
		String line;
		List<String> strResults = new ArrayList<>();

		// Ensures that each resource is closed at the end of the statement
		try(BufferedReader reader = new BufferedReader(inputStream)) {
				while ((line = reader.readLine()) != null){
					// parse each line to a ParsedLineEntity - note that the weight and maxCapacity
					// are corrected according to the correct factor by which the parser was constructed
					// This
					ParsedLineEntity parsedLine = parserController.parse(line);
					
					// Send the information to the packer implementation
					// the packer compute the selected items and by using the viewer return the result string
					strResults.add(packagerController.selectItems(parsedLine.getItemsList(),
												   				  parsedLine.getMaxWeight()));
				}
				
		} 
		catch (IOException e) {
			throw new APIException("IOException when processing file: " + e.getMessage());
		} 
		
		// build from all results string the output
		return buildStringResult(strResults);
	}
	
	/* 
	 * each result will be placed in a new line
	 */	
	private static String buildStringResult(List<String> strResults){
		StringBuilder builder = new StringBuilder();
		if (!strResults.isEmpty()){
			builder.append(strResults.remove(0));
			while (!strResults.isEmpty()){
				builder.append("\n");
				builder.append(strResults.remove(0));
			}
		}
		return builder.toString();
	}

	public static String pack(String filePath){
		Packer packer = new Packer(filePath);
		return packer.process();
	}
	
	
	public static void main(String[] args){
		if(args.length == 1){
			System.out.println(Packer.pack(args[0]));
		} else {
			throw new APIException("Full file path is requierd as a parameter");
		}
	}
	
}
