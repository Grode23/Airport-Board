import java.io.IOException;
import java.net.Socket;

/**
 * One of the sub-classes of client
 * This kind of client's job associates with changes to the board(as an employee)
 * It has some sleep time to make it looks closer to reality 
 */
public class Writer extends Client {
	
	public static void main(String[] args) {
		new Writer();
	}

	/**
	 * Same as the rest of the threads Keep every instance of this inner class in an
	 * Arraylist And start the threads
	 */
	public Writer() {
		super();
		
		Connection conn = new Connection(socket);
		Thread thread = new Thread(conn);
		connections.add(thread);
		thread.start();
	}

	private class Connection extends Client.Connection {

		public Connection(Socket socket) {
			super(socket);
		}

		@Override
		public void run() {
			
			// Do this forever
			// Request and get an answer
			while (true) {

				try {
					// Sleep for some time
					// I don't want to write or delete something all the time
					Thread.sleep(random.nextInt(3000) + 500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				int randomNum = random.nextInt(3);
				// Pick a random number so I can get a valid airport code
				int pick = random.nextInt(AirportCodes.values().length);

				// Delete, write or edit
				if (randomNum == 1) {
					System.out.println("Delete is called.");
					requestDelete(pick);
				} else if (randomNum == 2) {
					System.out.println("Write is called.");
					requestWrite(pick);
				} else {
					System.out.println("Edit is called.");
					requestEdit(pick);
				}

				answerFromServer();
			}

		}

		//Writer picks some random values and sends them to the server
		private void requestWrite(int pick) {

			String[] information = randomValuesForBoard(pick);

			try {
				output.writeUTF("WRITE" + information[0] + " " + information[1] + " " + information[2]);
				output.flush();

				System.out.println("Request is sent.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
		
		//Writer picks a random airport code and send it to the server
		private void requestDelete(int pick) {

			try {

				output.writeUTF("DELETE " + AirportCodes.values()[pick].toString());
				output.flush();

				System.out.println("Request is sent.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
		
		//Writer picks some random values and sends them to the server
		private void requestEdit(int pick) {

			String[] information = randomValuesForBoard(pick);

			try {
				output.writeUTF("EDIT" + information[0] + " " + information[1] + " " + information[2]);
				output.flush();

				System.out.println("Request is sent.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		//Used by the primary methods
		//Pick some random (but still valid) values
		private String[] randomValuesForBoard(int pick) {

			String[] information = new String[3];

			// Get a random airport code
			information[0] = AirportCodes.values()[pick].toString();

			// Get a random stage
			pick = random.nextInt(2);
			if (pick == 1)
				information[1] = "arrival";
			else
				information[1] = "departure";

			// Get a random time of the day
			pick = random.nextInt(24);
			information[2] = String.valueOf(pick) + ":";
			pick = random.nextInt(60);
			information[2] += String.valueOf(pick);

			return information;

		}
	}

}
