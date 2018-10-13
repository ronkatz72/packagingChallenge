package com.mobiquityinc.packer.controller;

import com.mobiquityinc.entities.ParsedLineEntity;
import com.mobiquityinc.packer.parsers.IParsingAlgorithm;

/* A line parse for the following format:
 *  W : (id1,w1,c1) ... (idn,wn,cn)
 *  
 *  Where:
 *  W = total possible weight of the package
 *  triple : idx = item's id, wx = item's weight, cx = item's cost 
 */

public class LineParserController {

	private IParsingAlgorithm parsingAlgorithm;
	
	// inject parsing algorithm
	public LineParserController(IParsingAlgorithm parsingAlgorithm){
		this.parsingAlgorithm = parsingAlgorithm;
	}
	

	public ParsedLineEntity parse(String line){
		return parsingAlgorithm.parse(line);
	}

}
