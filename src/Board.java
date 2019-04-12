import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Board {
	
	/**
	 * ReadsNow is the current reads that are happening.
	 * Without it, it would be possible to read a value that has been deleted some milliseconds ago.
	 * 
	 * SemaphoreDel 
	 */
	private Semaphore semaphoreDel = new Semaphore(1); 
	private Semaphore semaphore = new Semaphore(1);
	private AtomicInteger readsNow = new AtomicInteger(0);

	private ArrayList<String> codes = new ArrayList<>();
	private ArrayList<String> stages = new ArrayList<>();
	private ArrayList<String> dates = new ArrayList<>();
	

	public Board() {
		// Add items to Board - Testing purpose only
		writeItem("AMS", "depacture", "4:20");
		writeItem("ATH", "departure", "21:00");
		writeItem("SKG", "arrival", "23:10");
		writeItem("DEL", "arrival", "23:50");
		writeItem("PVG", "departure", "13:15");
		
	}	

	public String writeItem(String code, String stage, String date) {
		
		// Check if it already exists
		for (int i = 0; i < codes.size(); i++) {
			if (codes.get(i).equals(code)) {
				return "WERR";
			}
		}

		try {
			semaphore.acquire();
			
			Thread.sleep(5000);

		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		codes.add(code);
		stages.add(stage);
		dates.add(date);

		semaphore.release();
		
		return "WOK";
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
		
		//A read is happening
		//readsNow.incrementAndGet();		
		
		//parameter code is "READ <CODE>", so I keep only the part I need
		code = code.substring(5, code.length());
		
		//While someone is deleting, wait
		while(semaphoreDel.availablePermits() == 0) {}
		
		int index;
		
		if((index = searchItem(code)) != -1) {
			//readsNow.decrementAndGet();
			//If someone is deleting, do this again, because the chosen item might be deleted
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "ROK " + code + " " + stages.get(index) + " " + dates.get(index);
		}
		
		readsNow.decrementAndGet();
		return "RERR";
		
	}
	
	public String deleteItem(String code) {
		try {
			
			semaphore.acquire();
			
			//parameter code is "DELETE <CODE>", so I keep only the part I need
			code = code.substring(7, code.length());
			int index;
			
			if((index = searchItem(code)) != -1) {
				codes.remove(index);
				stages.remove(index);
				dates.remove(index);
				
				semaphore.release();
				return "DOK";
			}
	
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 			
		
		semaphore.release();
		return "DERR";

	}

}
