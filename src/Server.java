import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

	public static final int PORT = 1234;

	private ServerSocket ss;
	private ArrayList<Thread> connections = new ArrayList<>();
	private Board board = new Board();

	public static void main(String[] args) {			
		new Server();
	}
	
	public Server() {
		
		try {

			ss = new ServerSocket(PORT);

			while (true) {

				// Once per connection
				Socket socket = ss.accept();
				System.out.println("Client connected.");
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

		private void handleClients(String input) {			
			
			if(input.substring(0, 4).equals("READ")) {
				System.out.println("Reader requested to read");
				
				try {

					output.writeUTF(board.readItem(input));
					output.flush();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else if (input.substring(0, 5).equals("WRITE")) {
				System.out.println("Writer requested to write");
				
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
				System.out.println("Writer requested a deletion");

				try {
					output.writeUTF(board.readItem(input));
					output.flush();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		private String[] readInformation(String input) {
			
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
