
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Reader{
	
	public static final int NUM_OF_READERS = 3;

	private ArrayList<Thread> connections = new ArrayList<>();
	private String id;
	
	
	public static void main(String[] args) {
		
		new Reader("R");
		
	}

	public Reader(String id) {
		this.id = id;
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


	private class Connection implements Runnable {


		// Stream for giving and getting information
		DataInputStream input;
		DataOutputStream output;

		public Connection(Socket socket) {
			
			try {
				
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			
			System.out.println("Run of Client");

			while (true) {

				//String inputText = input.readUTF();
				requestRead();
				read();

			}

		}

		private void requestRead() {
			Scanner sc = new Scanner(System.in);

			System.out.println("What do you want?");
			String request = sc.nextLine();

			try {
				output.writeUTF(request);
				output.flush();
				System.out.println("Request is sent.");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		private void read() {
			try {
				String line = input.readUTF();
				
				System.out.println(line);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}



}
