package com.mobiquityinc.reader.parsing;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.parsers.DefaultPackerParser;

public class TestDefaultPackerParserNegative {
	
	private DefaultPackerParser parser;
	
	// valid format: "c : (a1,b1,Xc1) ... (an,bn,Xcn)";
	// c - package capacity
	// (am,bm,Xcm) - Item triplet:
	// am = the m's item index
	// bm = the m's item weight 
	
	
	@Before
	public void init(){
		parser = new DefaultPackerParser();
	}

	@Test
	public void testNoCapacityWithSeperator(){
		wrongFormatGenericTest(" : (1,53.38,€45) (2,88.62,€98)");
	}
	
	@Test
	public void testNoCapacityNoSeperator(){
		wrongFormatGenericTest(" (1,53.38,€45) (2,88.62,€98)");
	}
	
	@Test
	public void testNoCapacityWrongOrder(){
		wrongFormatGenericTest(" (1,53.38,€45) (2,88.62,€98) : 82");
	}
	
	@Test
	public void testMissingCurrancySign(){
		wrongFormatGenericTest(" 8200 : (1,53.38,€45) (2,88.62,98)");
	}
	
	@Test
	public void testWrongTripletForm1(){
		wrongFormatGenericTest(" 8200 : (1,53.38,€45,26) (2,88.62,98,55)");
	}
	
	@Test
	public void testWrongTripletForm2(){
		wrongFormatGenericTest(" 8200 : (1,53.38) (2,88.62,98,55,90)");
	}
	
	@Test
	public void testWrongTripletForm3(){
		wrongFormatGenericTest(" 8200 : (1.53.38,13,15) (2,88.62,98)");
	}
	
	@Test
	public void testWrongTripletForm4(){
		wrongFormatGenericTest(" 8200 : (1,53.38.13,15) (2,88.62,98)");
	}
	
	@Test
	public void testWrongTripletSeperator(){
		wrongFormatGenericTest(" 8200 : (1|53.38|€45) (2,88.62,€98)");
	}
	
	@Test
	public void testWrongTripletNoSpace(){
		wrongFormatGenericTest(" 8200 : (1,53.38,€45)(2,88.62,€98)");
	}
	
	@Test
	public void testWrongTripletNotADecimal(){
		wrongFormatGenericTest(" 8200 : (A,53.38,€45) (2,88.62,€98)");
	}
	
	@Test
	public void testWrongWeight1(){
		float bigWeight = DefaultPackerParser.MAX_WEIGHT + 50.3f;
		wrongFormatGenericTest(" 8200 : (1," + bigWeight +",€45) (2,88.62,€98)");
	}

	@Test
	public void testWrongWeight2(){
		wrongFormatGenericTest(" 8200 : (1,-153.38,€45) (2,88.62,€98)");
	}
	
	@Test
	public void testWrongWeight3(){
		wrongFormatGenericTest(" 8200 : (1,53.38,€45) (2,0,€98)");
	}
	
	@Test
	public void testWTooMuchItems(){
		// MAX_ITEMS_NUM + 1
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < (DefaultPackerParser.MAX_ITEMS_NUM + 1); i++){
			builder.append("(1,53.38,€45) ");
		}
		wrongFormatGenericTest(" 82 : " + builder.toString());
	}
	
	@Test
	public void testWrongCurrencySide(){
		wrongFormatGenericTest(" 82 : (1,53.38,45€) (2,34.55,98€)");
	}
	
	@Test
	public void testInvalidPackageCapacity1(){
		wrongFormatGenericTest(" -5 : (1,53.38,€45) (2,14.55,€98)");
	}
	
	@Test
	public void testInvalidPackageCapacity2(){
		wrongFormatGenericTest((DefaultPackerParser.MAX_PACKAGE_CAP + 5) + " : (1,53.38,€45) (2,14.55,€98)");
	}
	
	@Test
	public void testCurrencySignsMimatch(){
		wrongFormatGenericTest(" 82 : (1,53.38,$45) (2,44.78,€98)");
	}
	
	@Test
	public void testEmptyTriplet(){
		wrongFormatGenericTest(" 82 : () (2,44.78,€98)");
	}
	
	@Test
	public void testDuplicateIds(){
		wrongFormatGenericTest(" 82 : (1,44.78,€98) (2,44.78,€98) (2,44.78,€98) (3,44.78,€98)");
	}
	
	@Test
	public void testNegativeCost(){
		wrongFormatGenericTest(" 82 : (1,44.78,€-98) (2,44.78,€98) (3,44.78,€98) (4,44.78,€98)");
	}
	
	@Test
	public void testNegativeId(){
		wrongFormatGenericTest(" 82 : (-1,44.78,€98) (2,44.78,€98) (3,44.78,€98) (4,44.78,€98)");
	}
	
	private void wrongFormatGenericTest(String line){
		try{
			parser.parse(line);
			printTestedLine(line);
			System.out.println("Test Failed. Expected APIException");
		} catch (APIException e) {
			// test passed - exception expected when incorrect parameters are being passed
			System.out.println("Test Passed. " + e.getMessage());
			return;
		} catch (Throwable t){
			System.out.println("Unexpected throwable: " + t.getMessage());
		}
		System.out.println("\n");
		fail("APIException was expected");
	}
	
	private void printTestedLine(String line){
		System.out.print("Test line: " + line);
	}
	
}
