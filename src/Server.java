import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
			
			System.out.println("Run of Server connection");

			try {
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());

				while (true) {

					//Server sleeps if nothing is sent because otherwise it would drain big part of the CPU
					while (input.available() == 0) {
						Thread.sleep(5);
					}

					String inputText = input.readUTF();
					System.out.println("Reader requested something");
					handleClients(inputText);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		private void handleClients(String input) {			
			
			try {	
				
				output.writeUTF(board.searchItem(input, this));
				output.flush();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

	}

}
