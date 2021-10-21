/**
 * 
 */
package objects;

import java.util.ArrayList;
import java.util.List;

import writers.DatabaseWriter;
import writers.SpreadsheetWriter;

/**
 * @author Andrew
 *	Array of entries objects
 */
public class Entries {

	private static int columnWidths[] = { 25, 10, 20, 20, 35, 30, 10, 75, 75, 15 }; //widths of columns for spreadsheet cells corresponding to field index
	List<Entry> entries;			//list containing entry objects
	
	//default constructor
	public Entries(){
		entries = new ArrayList<Entry>();
	}
	
	//constructor with array of source objects as parameter
	public Entries(Source[] sources){
		entries = new ArrayList<Entry>();
		//add all entries in all sources to list
		for(Source source: sources) {			
			for(Entry entry: source.getEntries().toArrayList()) {
				entries.add(entry);
			}
		}
	}
	
	//constructor with collection object as parameter
	public Entries(Collection collection){
		entries = new ArrayList<Entry>();
		//add all entries from all sources within collection to list
		for(Source source: collection.getSources().toArrayList()) {
			for(Entry entry: source.getEntries().toArrayList()) {
				entries.add(entry);
			}
		}
	}
	
	//constructor with array of collections as parameter
	public Entries(Collections collections){
		entries = new ArrayList<Entry>();
		//add all entries within all sources within all collections to list
		for(Collection collection: collections.toArray()) {
			for(Source source: collection.getSources().toArrayList()) {
				for(Entry entry: source.getEntries().toArrayList()) {
					entries.add(entry);
				}
			}
		}
	}
		
	//add entries within entry object to spreadsheet
	public void toSpreadsheet(SheetInfo sheetInfo) {
		try {
			ColumnInfo columnInfo = new ColumnInfo(Entry.getFields(), columnWidths);
			for(int i = 0; i < Entry.getFields().length; i++) {
				System.out.println(Entry.getFields()[i]);
			}
			SpreadsheetWriter sw = new SpreadsheetWriter(columnInfo, sheetInfo);
			for(Entry entry: entries) {
				sw.writeRow(entry.toArray());	//write entry information to spreadsheet
			}
			sw.closeStream();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void toDatabase(String path, String schema, String table, String user, String password) {
		DatabaseWriter db = new DatabaseWriter(path, schema, table, user, password);
		String fields = db.fieldsToSQLStr(Entry.getFields());
		String rowStr;
		for(Entry entry: entries) {
			rowStr = db.entryToSQLStr(entry.toArray());			//parse array into string for SQL statement
			db.writeData(fields, rowStr);						//write entry to database
		}
	}

	//return entries array
	public List<Entry> toArrayList(){
		return entries;
	}


	/**
	 * Add Entry object to list.
	 * @param entry Entry object.
	 */
	public void add(Entry entry) {
		entries.add(entry);
	}
	
	public int getCount() {
		return entries.size();
	}

}
