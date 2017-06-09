# Ticket Service

This is an application designed to allow users to discover, temporarily hold, and reserve seats from an arbitrary venue.

### Requirements
1. Find the number of seats available within a venue
	1. Seats that are neither held nor reserved are defined as available
2. Find and hold the best available seats on behalf of the customer
	1. Each ticket hold should expire within a set number of seconds
3. Reserve and commit a specific group of held seats for a customer

### Assumptions
1. State will not be persisted beyond the process life of the application
2. At most one user will interface with the application through command line prompts at a time
3. The "best available" seats are seats that fulfill the following requirements in order:
	1. grouped together if at all possible
	2. closest to the stage
	3. closest to the middle of the row
4. The venue will be of a rectangular shape
5. The command line interface will allow the user to become different customers by providing different email addresses
6. Holds will be relinquished if a customer does not confirm the reservation within the hold time
7. Customers can hold as many seats as they wish (as long as there are enough available in the venue)
	
### Execution from a terminal
- `git clone https://github.com/grantharper/ticket-service.git`
- `cd ticket-service`
- `mvn clean install`
- `mvn exec:java`

### Proposed next steps
- Move from an in-memory to a disk-based storage database for persistence beyond life of the application
- Expand application beyond one sample venue
- Create web interface