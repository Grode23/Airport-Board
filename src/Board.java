import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Board {

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

	public String searchItem(String code, Server.Connection conn) {
		
		for (int i = 0; i < codes.size(); i++) {
			if (codes.get(i).equals(code)) {
				
				String stage = stages.get(i);
				String date = dates.get(i);
				
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				return "ROK " + code + " " + stage + " " + date;
			}
		}
		return "RERR";

	}

}
