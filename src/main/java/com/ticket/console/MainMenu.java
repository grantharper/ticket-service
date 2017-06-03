package com.ticket.console;

public enum MainMenu {

	SEATS_AVAILABLE(1, "Find out how many seats are still available"), SELECT_SEATS(0, "Select Seats"),
	LOGOUT(3, "Logout");

	private final int code;
	private final String label;

	MainMenu(int code, String label){
		this.code = code;
		this.label = label;
	}


}
