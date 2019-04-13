import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Writer extends Client {

	private ArrayList<Thread> connections = new ArrayList<>();
	
	public static void main(String[] args) {
		new Writer();	
	}

	public Writer() {

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
			
			Random random = new Random();

			while (true) {
				
				try {
					//Sleep for some time
					//I don't want to write or delete something all the time
					Thread.sleep(random.nextInt(2500) + 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//Delete or write
				if(random.nextInt(2) == 1) {
					System.out.println("Delete is called.");
					requestDelete();
				} else {
					System.out.println("Write is called.");
					requestWrite();
				}
				
				answerFromServer();
			}

		}
		
		private void requestWrite() {
			
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
				output.flush();
				
				//output.writeUTF(this.toString());
				
				System.out.println("Request is sent.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			
		}
		
		private void requestDelete() {
			
			//Pick something from the enum (valid airport codes)
		    int pick = random.nextInt(AirportCodes.values().length);
		    
		    try {
		    	
				output.writeUTF("DELETE " + AirportCodes.values()[pick].toString());
				output.flush();				
				
				System.out.println("Request is sent.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
	}

}
