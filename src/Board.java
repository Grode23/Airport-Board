import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Board {
	
	/**
	 * Reader can read only when 
	 */
	private Semaphore semaphore = new Semaphore(1);
	private Semaphore semaphoreDelete = new Semaphore(1);

	private ArrayList<String> codes = new ArrayList<>();
	private ArrayList<String> stages = new ArrayList<>();
	private ArrayList<String> dates = new ArrayList<>();
	

	public Board() {
		
		// Add items to Board - Testing purpose only
		codes.add("AMS");
		stages.add("depacture");
		dates.add("4:20");
		
		codes.add("ATH");
		stages.add("depacture");
		dates.add("2:10");

		codes.add("SKG");
		stages.add("arrival");
		dates.add("23:10");

		codes.add("DEL");
		stages.add("arrival");
		dates.add("5:07");

		codes.add("PVG");
		stages.add("depacture");
		dates.add("18:56");
		
	}	


	public int searchItem(String code) {
		
		for (int i = 0; i < codes.size(); i++) {
			if (codes.get(i).equals(code)) {
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				return i;
			}
		}
		return -1;

	}
	
	public String readItem(String code) {
		
		System.out.println("Reader requested to read");
		
		while(semaphoreDelete.availablePermits() == 0) {}
		System.out.println("Reading starts");
		
		//parameter code is "READ <CODE>", so I keep only the part I need
		String newCode = code.substring(5, code.length());

		int index;
		
		if((index = searchItem(newCode)) != -1) {
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//If someone is deleting, do this again, because the chosen item might be deleted
			if(semaphoreDelete.availablePermits() == 0) {
				System.out.println("Reading again");
				return readItem(code);
			}
			
			System.out.println("Reading is done.");
			return "ROK " + newCode + " " + stages.get(index) + " " + dates.get(index);
		}
		
		System.out.println("READING FAILED");
		return "RERR";
		
	}
	
	public String writeItem(String code, String stage, String date) {	
		
		// Check if it already exists
		for (int i = 0; i < codes.size(); i++) {
			if (codes.get(i).equals(code)) {
				System.out.println("WRITING FAILED");
				return "WERR";
			}
		}

		try {
			semaphore.acquire();
			System.out.println("Writing");
			
			Thread.sleep(5000);

		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		codes.add(code);
		stages.add(stage);
		dates.add(date);
		
		System.out.println("Writing is done.");
		semaphore.release();

		return "WOK";
	}
	
	public String deleteItem(String code) {
		
		int index;
		
		//Parameter code is "DELETE <CODE>", so I keep only the part I need
		code = code.substring(7, code.length());
		
		if((index = searchItem(code)) == -1) {	
			System.out.println("DELETE FAILED");
			return "DERR";		
		}
		
		try {
			//Stops writing
			semaphore.acquire();
			//Stops even the readings (with the while loop into readItem)
			semaphoreDelete.acquire();
			
			System.out.println("Deleting");
			
			Thread.sleep(5000);
			
			codes.remove(index);
			stages.remove(index);
			dates.remove(index);
				
			System.out.println("Deletion is done.");
			semaphoreDelete.release();
			semaphore.release();
			return "DOK";
			
	
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		
		return null;

	}

}
