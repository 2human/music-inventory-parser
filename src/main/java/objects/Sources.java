package objects;

import java.util.ArrayList;
import java.util.List;

import writers.DatabaseWriter;
import writers.SpreadsheetWriter;

/**
 * @author Andrew
 *	array of source objects
 *	methods for writing to string, spreadsheets, or database
 */


public class Sources {

	private static int columnWidths[] = { 25, 15, 30, 30, 30, 75, 75, 75 };		//widths of columns for spreadsheet cells corresponding to field index
	List<Source> sources;		//list containing source objects
	
	//default constructor
	public Sources(){
		sources = new ArrayList<Source>();
	}
	
	//constructor with collection as parameter
	public Sources(Collection collection){
		sources = new ArrayList<Source>();			
		for(Source source: collection.getSources().toArrayList()) {	//add sources within collection to an individual colle
			addSource(source);
		}
	}
	
	//constructor with collection array as parameter
	public Sources(Collections collections){
		sources = new ArrayList<Source>();
		for(Collection collection: collections.toArray()) {
			for(Source source: collection.getSources().toArrayList()) {
				addSource(source);
			}
		}
	}
	
	//add sources within sources object to spreadsheet
	public void toSpreadsheet(SheetInfo sheetInfo) {
		try {
			ColumnInfo columnInfo = new ColumnInfo(Source.getFields(), columnWidths);
			SpreadsheetWriter sw = new SpreadsheetWriter(columnInfo, sheetInfo);
			for(Source source: sources) {	
				sw.writeRow(source.toArray());	//write source information to spreadsheet
			}
			sw.closeStream();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//write sources to database
	public void toDatabase(String path, String schema, String table, String user, String password) {
		DatabaseWriter db = new DatabaseWriter(path, schema, table, user, password);
		String fields = db.fieldsToSQLStr(Source.getFields());
		String rowStr;
		for(Source source: sources) {
			rowStr = db.entryToSQLStr(source.toArray());		//construct SQL statement from source array
			db.writeData(fields, rowStr);						//write source to database
		}
	}
	
	//add source to sources list
	public void addSource(Source source) {
		sources.add(source);
	}
	
	//return source list
	public Sources getSources(){
		return this;
	}
	
	public List<Source> toArrayList(){
		return sources;
	}
	
	/**
	 * Get count of total entries in all sources.
	 * @return Entry count.
	 */
	public int getEntryCount() {
		int entryCount = 0;
		return entryCount;
	}
	
	

}
