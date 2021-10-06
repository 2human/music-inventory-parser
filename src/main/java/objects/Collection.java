package objects;

import java.io.File;

import parsers.CollectionParser;
import writers.DatabaseWriter;
import writers.SpreadsheetWriter;

/**
 * 
 * @author Andrew
 * Parse Word collection document
 *
 */


public class Collection {
	
	private static String[] fields = {"collection_name", "collection_description"};	//labels for collection table columns	
	private static int columnWidths[] = { 25, 100 };		//widths of columns for spreadsheet cells corresponding to field index
	private Sources sources = new Sources();				//source objects contained within collection
	private String collectionName,							//name of collection
					collectionDescription = "";				//information about collection
	String[] collection = new String[2];					//array containing collection information, [0] being name, and [1] being description
	
	//constructor with no parameters
	public Collection() {										
		
	}

	/**
	 * Construct Collection object with music collection file.
	 * 
	 * @param file Music collection file, prepared for parsing.
	 *
	 */
	public Collection(File file){
		CollectionParser pc = new CollectionParser(file, this);	//constructor for collection parser
		pc.execute();																//execute collection parser
		collection[0] = collectionName;							//record collection name to array
		collection[1] = collectionDescription.trim();			//record collection description to array
	}
	
	/**
	 * Construct Collection object with music collection file.
	 * 
	 * @param files	files to be parsed
	 * @param preParsed files were recorded in new format optimized for parsing
	 */
	public Collection(File file, boolean preParsed){
		CollectionParser pc = new CollectionParser(file, this);	//constructor for collection parser
		pc.execute();																//execute collection parser
		collection[0] = collectionName;							//record collection name to array
		collection[1] = collectionDescription.trim();			//record collection description to array
	}
	
	public String toString() {
		return "Collection Name: " + collectionName + 
				"\n\n------------------------------------Collection Description------------------------------------\n" + collectionDescription + 
				"\n\n------------------------------------End of Collection Description------------------------------------";
	}
	
	//return array containing collection fields
	public String[] toArray() {
		return collection;
	}
	
	//get collection name
	public String getName() {
		return collectionName;
	}
	
	//get collection description
	public String getDescription() {
		return collectionDescription;
	}

	//return collection of source objects for current collection
	public Sources getSources(){	
		return sources;
	}
	
	public void addSource(Source source) {
		sources.addSource(source);
	}
	
	//return column labels for collection 
	public static String[] getFields() {
		return fields;
	}
	
	//set name of collection
	public void setName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	//set description of collection
	public void setDescription(StringBuilder description) {
		this.collectionDescription = description.toString();
	}
	
	//write collection data to spreadsheet
	public void toSpreadsheet(SheetInfo sheetInfo) {
		try {
			ColumnInfo columnInfo = new ColumnInfo(Entry.getFields(), columnWidths);
			SpreadsheetWriter sw = new SpreadsheetWriter(columnInfo, sheetInfo);
			sw.writeRow(toArray());	//write source information to spreadsheet
			sw.closeStream();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	//write individual collection to database
	public void toDatabase(String path, String schema, String table, String user, String password) {
		DatabaseWriter db = new DatabaseWriter(path, schema, table, user, password);
		String fields = db.fieldsToSQLStr(getFields());
		String rowStr;
		rowStr = db.entryToSQLStr(collection);
		db.writeData(fields, rowStr);
	}	
	
}
	