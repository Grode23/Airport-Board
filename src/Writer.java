import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Writer extends Client {

	private ArrayList<Thread> connections = new ArrayList<>();

	public Writer(String id) {

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

				requestWriting();
				answerFromServer();
			}

		}
		
		private void requestWriting() {
			
			int pick;
			String code, stage, date;
			
			//Get a random airport code
		    pick = random.nextInt(AirportCodes.values().length);
		    code = AirportCodes.values()[pick].toString();
		    
		    //Get a random stage
		    pick = random.nextInt(2);
		    if(pick == 1)
		    	stage = "arrival";
		    else
		    	stage = "departure";
		    
		    //Get a random time of the day
		    pick = random.nextInt(24);
		    date = String.valueOf(pick) + ":";
		    pick = random.nextInt(60);
		    date += String.valueOf(pick);
		    
		    try {
				output.writeUTF("WRITE" + code + " " + stage + " " + date);
				output.writeUTF(this.toString());
				
				System.out.println("Request is sent.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			
		}		
		
	}

}
