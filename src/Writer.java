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
				
				int randomNum = random.nextInt(3);
				//Pick a random number so I can get a valid airport code
			    int pick = random.nextInt(AirportCodes.values().length);
				
				//Delete or write
				if(randomNum == 1) {
					System.out.println("Delete is called.");
					requestDelete(pick);
				} else if(randomNum == 2){
					System.out.println("Write is called.");
					requestWrite(pick);
				} else {
					System.out.println("Edit is called.");
					requestEdit(pick);
				}
				
				answerFromServer();
			}

		}
		
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
		
		private void requestDelete(int pick) {
					    
		    try {
		    	
				output.writeUTF("DELETE " + AirportCodes.values()[pick].toString());
				output.flush();				
				
				System.out.println("Request is sent.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}

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
		
		private String[] randomValuesForBoard(int pick) {
			
			String[] information = new String[3];
			
			//Get a random airport code
		    information[0] = AirportCodes.values()[pick].toString();
		    
		    //Get a random stage
		    pick = random.nextInt(2);
		    if(pick == 1)
		    	information[1] = "arrival";
		    else
		    	information[1] = "departure";
		    
		    //Get a random time of the day
		    pick = random.nextInt(24);
		    information[2] = String.valueOf(pick) + ":";
		    pick = random.nextInt(60);
		    information[2] += String.valueOf(pick);
	
			return information;
	
		}
	}

}
