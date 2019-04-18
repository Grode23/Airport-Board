import java.io.IOException;
import java.net.Socket;

/**
 * One of the sub-classes of client
 * This kind of client's job is reading from the board (as a normal citizen)
 * It has some sleep time to make it looks closer to reality 
 */
public class Reader extends Client {

	public static void main(String[] args) {
		new Reader();
	}

	/**
	 * Same as the rest of the threads Keep every instance of this inner class in an
	 * Arraylist And start the threads
	 */
	public Reader() {
		super();

		// Add the connection to an Arraylist
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
					Thread.sleep(random.nextInt(400));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				requestRead();
				answerFromServer();
			}

		}

		private void requestRead() {

			// Pick something from the enum (valid airport codes)
			int pick = random.nextInt(AirportCodes.values().length);

			try {
				// Send request to server
				output.writeUTF("READ " + AirportCodes.values()[pick].toString());
				output.flush();

				System.out.println("Request is sent.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}

}
