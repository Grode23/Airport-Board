import java.util.ArrayList;
import java.util.concurrent.Semaphore;
/**
 * Board is responsible for the information gathering
 * It's being called by many threads, so it has to deal with multithreading issues
 * Board can be called ONLY by server
 * 
 * There are some primary methods (readItem, deleteItem, writeItem, editItem) 
 * and some secondary just for keeping my code simple and clean  
 */
public class Board {

	/**
	 * semaphoreExtra exists just because the readers shouldn't read when
	 * a delete or an edit is being applied
	 * Other than that, it's just an extra semaphore with no other capacity
	 */
	private Semaphore semaphore = new Semaphore(1);
	private Semaphore semaphoreExtra = new Semaphore(1);

	//Elements of the board
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
	//Search for an item
	//Useful for a lot of the others primary methods
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
//READ
	public String readItem(String code) {

		//If someone deletes or edits an item, wait
		//Because it might change the item that I am going to read
		while (semaphoreExtra.availablePermits() == 0) {
		}
		System.out.println("Reading");

		// parameter code is "READ <CODE>", so I keep only the part I need
		String newCode = code.substring(5, code.length());

		int index;

		//If this code exists, read about it
		if ((index = searchItem(newCode)) != -1) {

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// If someone deletes or edits, do this again, because the chosen item might be
			// deleted
			if (semaphoreExtra.availablePermits() == 0) {
				System.out.println("Reading must be done again");
				return readItem(code);
			}

			System.out.println("Reading is done.");
			return "ROK " + newCode + " " + stages.get(index) + " " + dates.get(index);
		}

		System.out.println("READING FAILED");
		return "RERR";

	}
	
//WRITE
	public String writeItem(String code, String stage, String date) {

		// Check if it already exists
		for (int i = 0; i < codes.size(); i++) {
			if (codes.get(i).equals(code)) {
				System.out.println("WRITING FAILED");
				return "WERR";
			}
		}

		try {
			//Lock it and do the work
			semaphore.acquire();
			System.out.println("Writing");

			Thread.sleep(5000);

		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} finally {
			
			//Write the new item
			codes.add(code);
			stages.add(stage);
			dates.add(date);

			System.out.println("Writing is done.");
			semaphore.release();

		}

		return "WOK";
		
	}
	
//DELETE
	public String deleteItem(String code) {

		int index;

		// Parameter code is "DELETE <CODE>", so I keep only the part I need
		code = code.substring(7, code.length());

		//Return error if the item doesn't exist
		if ((index = searchItem(code)) == -1) {
			System.out.println("DELETE FAILED");
			return "DERR";
		}

		try {
			// Stops writing and editing
			semaphore.acquire();
			// Stops even the readings (with the while loop into readItem)
			semaphoreExtra.acquire();

			System.out.println("Deleting");

			Thread.sleep(5000);

			//Remove the item
			codes.remove(index);
			stages.remove(index);
			dates.remove(index);

			System.out.println("Deletion is done.");
			semaphoreExtra.release();
			semaphore.release();
			return "DOK";

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return null;

	}

//EDIT
	public String editItem(String code, String stage, String date) {

		int index;

		//If it exists, lock the method and get the index
		//If not, return error
		if ((index = searchItem(code)) != -1) {
			try {
				// Stops writing and deleting
				semaphore.acquire();
				// Stops even the readings (with the while loop into readItem)
				semaphoreExtra.acquire();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("EDIT FAILED");
			return "EERR";
		}

		try {

			System.out.println("Editing");

			Thread.sleep(5000);

			// Edit the item
			stages.set(index, stage);
			dates.set(index, date);

			System.out.println("Editing is done.");
			semaphoreExtra.release();
			semaphore.release();
			return "EOK";

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//It's not going to happen
		return null;

	}

}
