import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public abstract class Client {
	
	protected Random random = new Random();

	protected class Connection implements Runnable {

		// Stream for giving and getting information
		protected DataInputStream input;
		protected DataOutputStream output;

		public Connection(Socket socket) {

			try {

				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {}

		@Override
		public String toString() {
			return super.getClass().getCanonicalName();
		}
		
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
