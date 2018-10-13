package com.mobiquityinc.reader.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.mobiquityinc.entities.ParsedLineEntity;
import com.mobiquityinc.packer.parsers.DefaultPackerParser;

/*
 * Positive tests for DefaultPackerParser
 * Send lines to the parsers that should return a ParsedLineEntity
 */

public class TestDefaultPackerParserPositive {

	private DefaultPackerParser parser;
	
	@Before
	public void init(){
		parser = new DefaultPackerParser();
	}
	
	@Test
	public void testDefaultLine(){
		ParsedLineEntity parsedLine = genericTest("81 : (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)");
		assertNotNull(parsedLine);
		// note that there is one triplet with weight more than capacity and it will not be included in results
		assertEquals(5,parsedLine.getItemsList().size());
		assertEquals(Integer.valueOf(8100), parsedLine.getMaxWeight());
	}
	
	@Test
	public void testDifferentCurrency(){
		ParsedLineEntity parsedLine = genericTest("81 : (1,53.38,$45) (3,78.48,$3) (5,30.18,$9) ");
		assertNotNull(parsedLine);
		// note that there is one triplet with weight more than capacity and it will not be included in results
		assertEquals(3,parsedLine.getItemsList().size());
		assertEquals(Integer.valueOf(8100), parsedLine.getMaxWeight());
	}
	
	@Test
	public void testPrecision(){
		ParsedLineEntity parsedLine = genericTest("81 : (1,80.001,$45) (3,1.001,$3) (5,90.18,$9) ");
		assertNotNull(parsedLine);
		// note that there is one triplet with weight more than capacity and it will not be included in results
		assertEquals(2,parsedLine.getItemsList().size());
		assertEquals(Integer.valueOf(100), parsedLine.getItemsList().get(0).getWeight());
		assertEquals(Integer.valueOf(8000), parsedLine.getItemsList().get(1).getWeight());
		assertEquals(Integer.valueOf(8100), parsedLine.getMaxWeight());
	}
		

	private ParsedLineEntity genericTest(String line){
		ParsedLineEntity parsedLine = null;
		try{
			parsedLine =  parser.parse(line);
			printTestedLine(line);
		} catch (Throwable t){
			System.out.println("Unexpected Throwable: " + t.getMessage());
			fail("Unexpected Throwabl");
		}
		return parsedLine;
	}
	
	private void printTestedLine(String line){
		System.out.println("Test line: " + line);
	}
}
