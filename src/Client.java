import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * Client is an abstract class for Reader and Writer
 * It contains all the common stuff of them
 * Such as Inner class Connection, methods and objects
 */
public abstract class Client {
	
	protected Random random = new Random();
	protected ArrayList<Thread> connections = new ArrayList<>();	
	protected Socket socket;
	
	//Constructor of the super class
	public Client() {

		try {
			// Connection with the server
			socket = new Socket("localhost", Server.PORT);			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected abstract class Connection implements Runnable {

		// Stream for giving and getting information
		private DataInputStream input;
		protected DataOutputStream output;

		public Connection(Socket socket) {

			try {
				//Initialize streams
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {}
		
		//Gets the response from the server
		protected void answerFromServer() {
			try {
				
				String line = input.readUTF();	
				System.out.println(line);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		


	}

}
