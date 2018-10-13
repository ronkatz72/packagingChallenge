package com.mobiquityinc.packer.parsers;

public class ParsersTools {
	
	public static boolean isNullOrEmpty(String str) {
		return (null == str || str.equals(""));
	}
	
	public static boolean isNumeric(char c){
		int prefixAsciiVal = (int)c;
		// ascii(0) = 48, ascii(9)= 57
		if (48 <= prefixAsciiVal && prefixAsciiVal <= 57){
			return true;
		}
		return false;
	}

}
