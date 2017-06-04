# ticket-service

This is an application designed to perform the following functions.

Requirements:
	1. Find the number of seats available within a venue
		a. Seats that are neither held nor reserved are defined as available
	2. Find and hold the best available seats on behalf of the customer
		a. Each ticket hold should expire within a set number of seconds
	3. Reserve and commit a specific group of held seats for a customer

Assumptions:
	1. State will only be available while the application is running
	2. Users will interface with the application through a command line
	3. The best available seats are seats that fulfill the following requirements:
		a. grouped together
		b. closest to the stage
		c. closest to the middle of the row
	4. The arena will be of a rectangular shape
	5. The command line interface will allow the user to become different customers by entering an email address
	6. Hold initiated by a customer will be honored even if the customer switches
	7. Customers can hold as many seats as they wish
	8. There will be no interactive countdown clock
	
After cloning the repository, run the following commands from the project root to build and run the ticket service

mvn clean install
mvn exec:java