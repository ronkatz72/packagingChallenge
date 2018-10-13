package com.mobiquityinc.packer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mobiquityinc.exception.APIException;

public class TestPacker {

	/* testPackerExample:
	 * Test the results of the example from the assignment paper
	 */
	@Test
	public void testPackerExample(){
		String expectedResult = "4\n-\n2,7\n8,9";
		String itemsResults = Packer.pack("./src/test/resources/exampleFile.txt");
		assertEquals(expectedResult, itemsResults);
		System.out.println("Test passed. Example file.");
	}
	
	
	/* testMoreThan15Items:
	 * Test for the constraint of number of items
	 */
	@Test
	public void testMoreThan15Items(){
		try{
			Packer.pack("./src/test/resources/moreThan15Items.txt");
		}catch (APIException e) {
			// test passed - exception expected when incorrect parameters are being passed
			System.out.println("Test Passed. 15 items limit.");
			return;
		} catch (Throwable t){
			System.out.println("Unexpected throwable: " + t.getMessage());
		}
		System.out.println("\n");
		fail("APIException was expected");
	}
	
	/* testFullCapacityLowerToHigerWeight:
	 * Since capacity is 90 and items are of weight: 30,30,30,20,40 and each item cost is 100$
	 * We expect to get the item with weight 20 and two items with weight 30
	 * That is since this way the weight will be minimal (80) and the price will be 300$
	 * Since the items are being sorted by their weight, the items will be:
	 * the two first 30's weight (i.e. id 1 & 2) and then id 4
	 */
	@Test
	public void testFullCapacityLowerToHigerWeight(){
		String expectedResult = "1,2,4";
		String itemsResults = Packer.pack("./src/test/resources/fullCapacityLowerToHigerWeight.txt");
		assertEquals(expectedResult, itemsResults);
		System.out.println("Test passed. FullCapacityLowerToHigerWeight file.");
	}
	
	/* testCumulativeValue
	 * input: 100 : (1,100.00,$40) (2,100.00,$200) (3,50.00,$100) (4,50.00,$100) (5,25.00,$1) (6,25.00,$100)
	 * result : select 3, 5 and 6 in order to receive value of 201$ when weight of
	 *          results is always maximum possible
	 */
	@Test
	public void testCumulativeValue(){
		String expectedResult = "3,5,6";
		String itemsResults = Packer.pack("./src/test/resources/cumulativeValue.txt");
		assertEquals(expectedResult, itemsResults);
		System.out.println("Test passed. cumulativeValue file.");
	}
	
	/* testPrecisionValidWeight:
	 * 100 : (1,100.001,$40) and precision 2 - weight neglects the 3rd digit after the dot
	 * and therefore is a valid weight
	 */
	@Test
	public void testPrecisionValidWeight(){
		String expectedResult = "1";
		String itemsResults = Packer.pack("./src/test/resources/precisionValidWeight.txt");
		assertEquals(expectedResult, itemsResults);
		System.out.println("Test passed. PrecisionValidWeight.");
	}
	
	
	/* testPrecisionInvalidWeight:
	 * 100 : (1,100.01,$40) and precision 2 - weight is too high
	 */
	@Test
	public void testPrecisionInvalidWeight(){
		try{
			Packer.pack("./src/test/resources/precisionInvalidWeight.txt");
		}catch (APIException e) {
			// test passed - exception expected when incorrect parameters are being passed
			System.out.println("Test Passed. PrecisionInvalidWeight.");
			return;
		} catch (Throwable t){
			System.out.println("Unexpected throwable: " + t.getMessage());
		}
		System.out.println("\n");
		fail("APIException was expected");
	}
	
	/* testCapacityZero:
	 * Zero capacity returns an empty set
	 */
	@Test
	public void testCapacityZero(){
		String expectedResult = "-";
		String itemsResults = Packer.pack("./src/test/resources/capacityZero.txt");
		assertEquals(expectedResult, itemsResults);
		System.out.println("Test passed. CapacityZero.");
	}
	
	
	/* testCapacityNegative:
	 * Negative capacity is invalid
	 */
	@Test
	public void testCapacityNegative(){
		try{
			Packer.pack("./src/test/resources/capacityNegative.txt");
		}catch (APIException e) {
			// test passed - exception expected when incorrect parameters are being passed
			System.out.println("Test Passed. CapacityNegative.");
			return;
		} catch (Throwable t){
			System.out.println("Unexpected throwable: " + t.getMessage());
		}
		System.out.println("\n");
		fail("APIException was expected");
	}
	
	/* testAllItems:
	 * capacity = 100, each item weight = 1 --> all 15 items should be in package
	 */
	@Test
	public void testAllItems(){
		String expectedResult = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15";
		String itemsResults = Packer.pack("./src/test/resources/allItems.txt");
		assertEquals(expectedResult, itemsResults);
		System.out.println("Test passed. AllItems.");
	}
}