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
		addItem("AMS", "aaa", "4:20");
		addItem("ATH", "bbb", "21:00");
		addItem("SKG", "ccc", "23:10");
	}

	public String addItem(String code, String stage, String date) {

		// Check if it already exists
		for (int i = 0; i < codes.size(); i++) {
			if (codes.get(i).equals(code)) {
				return "WERR";
			}
		}

		ReentrantLock lock = new ReentrantLock();

		lock.lock();

		codes.add(code);
		stages.add(stage);
		dates.add(date);

		lock.unlock();

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
		return 0;

	}
	
	public String readItem(String code) {
		
		//A read is happening
		//readsNow.incrementAndGet();		
		
		//parameter code is "READ <CODE>", so I keep only the part I need
		code = code.substring(5, code.length());
		
		//While someone is deleting, wait
		while(semaphoreDel.availablePermits() == 0) {}
		
		int index;
		
		if((index = searchItem(code)) != 0) {
			//readsNow.decrementAndGet();
			//If someone is deleting, do this again, because the chosen item might be deleted
			if(semaphoreDel.availablePermits() == 0) {
				System.out.println("Reading again");
				readItem(code);
			}
			return "ROK " + code + " " + stages.get(index) + " " + dates.get(index);
		}
		readsNow.decrementAndGet();
		return "RERR";
		
	}
	
	public void deleteItem() {
		try {
			//While there are some reads happening, don't delete anything
			//while(readsNow.get() != 0) {}
			semaphore.acquire();
			
			
			
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			semaphore.release();
		}
		
		
		
		
	}

}
