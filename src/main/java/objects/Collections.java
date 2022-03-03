package objects;

import java.io.File;
import writers.DatabaseWriter;
import writers.SpreadsheetWriter;



public class Collections {
	Collection[] collections;
	private static int columnWidths[] = { 25, 100 };		//widths of columns for spreadsheet cells corresponding to field index
	
	/**
	 * Parse music collection file and construct music collection object
	 * @param file Music collection file to be parsed
	 */
	public Collections(CollectionFile collectionFile){
		collections = new Collection[1];
		collections[0]= new Collection(collectionFile);
	}
	
	/**
	 * Parse music collection file and construct music collection object
	 * @param files Array of music collection files to be parsed
	 */
	public Collections(CollectionFile[] collectionFiles){
		collections = new Collection[collectionFiles.length];
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < collectionFiles.length; i++) {
			collections[i] = new Collection(collectionFiles[i]);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("It took " + (endTime - startTime) + " milliseconds");
	}
	
	/**
	 * Write music collection information to spreadsheet.
	 * @param fileName Name of spreadsheet output file.
	 * @param path Path file will be written to.
	 */
	public void toSpreadsheet(SheetInfo sheetInfo) {
		try {
			ColumnInfo columnInfo = new ColumnInfo(Collection.getFields(), columnWidths);
			SpreadsheetWriter sw = new SpreadsheetWriter(columnInfo, sheetInfo);
			for(Collection collection: collections) {
				sw.writeRow(collection.toArray());	//write source information to spreadsheet
			}
			sw.closeStream();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * Write collection information to mySQL database.
	 * @param path Database path.
	 * @param schema Schema name.
	 * @param table	Name of table collection information will be written to.
	 * @param user	Database user login name.
	 * @param password Database user login password.
	 */
	public void toDatabase(String path, String schema, String table, String user, String password) {
		DatabaseWriter db = new DatabaseWriter(path, schema, table, user, password);
		String fields = db.fieldsToSQLStr(Collection.getFields());
		String rowStr;
		for(Collection collection: collections) {
			rowStr = db.entryToSQLStr(collection.toArray());
			db.writeData(fields, rowStr);
		}
	}	
	
	/**
	 * 
	 * @return Array of music collection objects.
	 */
	public Collection[] toArray() {
		return collections;
	}
	
	/**
	 * 
	 * @return Sources object containing all sources contained within this array of collections. 
	 */
	public Sources getSources() {
		Sources sources = new Sources(this);
		return sources;
	}
	
	/**
	 * 
	 * @return Entries object containing all entries within this array of collections.
	 */
	public Entries getEntries() {
		Entries entries = new Entries(this);
		return entries;
	}
	
}
	
	
	

