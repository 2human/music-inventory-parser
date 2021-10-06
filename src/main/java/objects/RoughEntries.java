package objects;

import java.util.ArrayList;

public class RoughEntries {
	
	private ArrayList<RoughEntry> roughEntries;
	
	public RoughEntries() {
		roughEntries = new ArrayList<RoughEntry>();
	}

	public ArrayList<RoughEntry> toArrayList() {
		return roughEntries;
	}
	
	public void add(RoughEntry roughEntry) {
		roughEntries.add(roughEntry);
	}
	
	public int getCount() {
		return roughEntries.size();
	}

	

}
