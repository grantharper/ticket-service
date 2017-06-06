package com.ticket.console;

/**
 * enum to support the menu for confirmation of seat selection
 */
public enum YesNo {

	YES(1, "Yes"), NO(0, "No");
	
	private final int code;
	private final String label;
	
	YesNo(int code, String label){
		this.code = code;
		this.label = label;
	}
	
	/**
	 * @return the boolean value of the yes/no stored
	 */
	boolean booleanValueOf(){
		if(code == 1){
			return true;
		}else{
			return false;
		}
	}
	
}
