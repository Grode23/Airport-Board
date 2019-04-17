import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Server is responsible for every connection between itself and the clients
 * Server handles the clients (all kinds). Gets the input and transform it as it needs
 * Every clients' output gets passed to board through here
 * Whenever board stuff is done, the input goes here and finally gets send back to the client
 * 
 * P.S. There is a lot of prints to show the current state of the process
 */
public class Server {

	//The one and only port
	public static final int PORT = 1234;

	private ServerSocket ss;
	private ArrayList<Thread> connections = new ArrayList<>();
	private Board board = new Board();

	public static void main(String[] args) {			
		new Server();
	}
	
	public Server() {
		System.out.println("Server is running");
		
		try {

			ss = new ServerSocket(PORT);

			while (true) {

				// Once per connection/client
				Socket socket = ss.accept();
				System.out.println("Client connected.");
				
				//Create and start a new connection with a client
				Connection conn = new Connection(socket);
				Thread thread = new Thread(conn);
				connections.add(thread);
				thread.start();

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public class Connection implements Runnable {

		Socket socket;

		// Stream for giving and getting information
		DataInputStream input;
		DataOutputStream output;

		public Connection(Socket socket) {
			this.socket = socket;
			
		}

		@Override
		public void run() {
			

			try {
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());

				while (true) {

					//Server sleeps if nothing is sent because otherwise it would drain big part of the CPU
					while (input.available() == 0) {
						Thread.sleep(5);
					}
					
					//If there is an input, go on
					if(!input.equals(null)) {
						
						String inputText = input.readUTF();						
						handleClients(inputText);
					
					} else {
						System.out.println("Client is gone.");
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		/**
		 * Input is being read and gets sent wherever it has to
		 * With other words, server reads the first word of the message, 
		 * so it can understand what kind of request it is
		 * Finally, server calls the appropriate board method 
		 */
		private void handleClients(String input) {			
			
			if(input.substring(0, 4).equals("READ")) {
				
				try {

					output.writeUTF(board.readItem(input));
					output.flush();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else if (input.substring(0, 5).equals("WRITE")) {
				
				//Information[0] -> code
				//Information[1] -> stage
				//Information[2] -> date
				String[] information = new String[3];
				information = readInformation(input);
				
				try {
					output.writeUTF(board.writeItem(information[0], information[1], information[2]));
					output.flush();

				} catch (IOException e) {
					e.printStackTrace();
				}

				
			} else if(input.substring(0, 6).equals("DELETE")) {

				try {
					output.writeUTF(board.deleteItem(input));
					output.flush();

				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if(input.substring(0, 4).equals("EDIT")){
			
				//Information[0] -> code
				//Information[1] -> stage
				//Information[2] -> date
				String[] information = new String[3];
				information = readInformation(input);
				
				try {
					output.writeUTF(board.editItem(information[0], information[1], information[2]));
					output.flush();

				} catch (IOException e) {
					e.printStackTrace();
				}


			}
			
		}
		
		//Whenever the input is complicated (writes and edits) this method is called
		//Arguments get stored and returned via string array
		private String[] readInformation(String input) {
			
			//array that will store the information I need
			String[] result = new String[3];
			
			Pattern pattern = Pattern.compile("(WRITE|EDIT)\\s*([A-Z]{3})\\s*(arrival|departure)\\s*(\\d{1,2}:\\d{1,2})");
			Matcher matcher = pattern.matcher(input);

			while (matcher.find()) {
				
				//Code
				result[0] = matcher.group(2);

				//Stage
				result[1] = matcher.group(3);
				
				//Date
				result[2] = matcher.group(4);
			
			}
			
			return result;
		}
		
	}

}
