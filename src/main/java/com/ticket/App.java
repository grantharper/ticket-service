package com.ticket;

import java.io.Console;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		Console console = System.console();
		String keyboardInput = "";
		
		System.out.println("Welcome to Awesome Venue!");
		while(!keyboardInput.equalsIgnoreCase("q")){
			//prompt the user
			keyboardInput = console.readLine("Enter whatever you want and enter q to quit:\n");
			System.out.println("You entered: " + keyboardInput);
		}
		
	}
}
