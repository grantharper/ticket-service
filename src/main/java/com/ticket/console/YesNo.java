package com.ticket.console;

public enum YesNo {

	YES(1, "Yes"), NO(0, "No");
	
	private final int code;
	private final String label;
	
	YesNo(int code, String label){
		this.code = code;
		this.label = label;
	}
	
	boolean booleanValueOf(){
		if(code == 1){
			return true;
		}else{
			return false;
		}
	}
	
}
