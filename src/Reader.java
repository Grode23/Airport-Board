import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Reader extends Client{
		
	private ArrayList<Thread> connections = new ArrayList<>();	
	
	public static void main(String[] args) {
		new Reader();	
	}

	public Reader() {

		Socket socket;

		try {

			socket = new Socket("localhost", Server.PORT);		
			
			Connection conn = new Connection(socket);
			Thread thread = new Thread(conn);
			connections.add(thread);
			thread.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private class Connection extends Client.Connection {

		public Connection(Socket socket) {
			super(socket);
		}

		@Override
		public void run() {
			
			while (true) {
				requestRead();
				answerFromServer();
			}

		}

		private void requestRead() {	
			
			//Pick something from the enum (valid airport codes)
		    int pick = random.nextInt(AirportCodes.values().length);
		    
		    try {
				output.writeUTF("READ " + AirportCodes.values()[pick].toString());
				output.writeUTF(this.toString());
				
				
				System.out.println("Request is sent.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		

	}

}
