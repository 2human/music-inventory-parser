package app;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import objects.CollectionFile;
import objects.Collections;
import objects.SheetInfo;
import writers.DatabaseWriter;


/**
 * 
 * Parses music handwritten inscriptions in music collections recorded to Word document. Within each collection are numerous sources.
 * Each source may contain numerous entries (handwritten melodic incipits, which are sometimes accompanied by text incipits)
 * Author recorded these into .docx Word files with consistent syntax, making it possible to parse collections into table format
 * 
 * @author Andrew
 * 
 */

public class ParseInventoriesApp {
	public static void main(String[] args) {
		Collections collections = new Collections(getCurrentCollectionFiles());
		//write spreadsheets
		generateSpreadsheet(collections);
		
		
//		initializeDatabaseTables("music-inventory-final");		
		//write database
//		writeDatabase(collections, "music-inventory");
		
		System.out.println("Operations complete.");
	}
	
	private static void generateSpreadsheet(Collections collections) {
		deleteOldSpreadhsset();		
		writeSpreadsheet(collections);
		openSpreadsheet();
	}
	
	private static void deleteOldSpreadhsset() {
		File file = new File("src/main/resources/spreadsheet outputs/parsed_collections.xlsx");
		file.delete();
	} 
	
	//TODO: turn collection files into object, containing whether each is true or not!
	@SuppressWarnings("unused")
	private static CollectionFile[] getCurrentCollectionFiles() {
		CollectionFile[] collectionFiles = {				
				new CollectionFile(
						"src/main/resources/finalized collections/CT Middletown, Olin Library.docx",
						true
				)
		};
		return collectionFiles;
	}	
	
	//write collection(s) to spreadsheet
	@SuppressWarnings("unused")
	private static void writeSpreadsheet(Collections collections) {
		String workbookName = "parsed_collections";
		String workbookPath = "src/main/resources/spreadsheet outputs/";
		SheetInfo sheetInfo = new SheetInfo(workbookPath, workbookName, "collections");
		collections.toSpreadsheet(sheetInfo);
		sheetInfo = new SheetInfo(workbookPath, workbookName, "sources");
		collections.getSources().toSpreadsheet(sheetInfo);
		sheetInfo = new SheetInfo(workbookPath, workbookName, "entries");
		collections.getEntries().toSpreadsheet(sheetInfo);
	}
	
	@SuppressWarnings("unused")
	private static void openSpreadsheet() {
		File file = new File("src/main/resources/spreadsheet outputs/parsed_collections.xlsx");
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private static CollectionFile[] getAllCollectionFiles() {
		CollectionFile[] collectionFiles = {
			new CollectionFile(
					"src/main/resources/finalized collections/CT Hartford, Connecticut Historical Society.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/CT Hartford, Watkinson Library, Trinity College.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/MA Petersham, Nym Cooke collection.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/MA Boston, Congregational Library and Archives.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/MA Boston, Boston Athenaeum.docx",
					false),
			new CollectionFile (
					"src/main/resources/finalized collections/MA Andover, Andover Center for History and Culture.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society - 1.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society - 2.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society - 3.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society - 4.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society - 5.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society - 6.docx", 
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society - 7.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society - 8.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society - 9.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society - 10.docx",
					false),
			new CollectionFile(
					"src/main/resources/finalized collections/MA Boston, Massachusetts Historical Society.docx",
					false),						
			new CollectionFile(
					"src/main/resources/finalized collections/MA Cambridge, Harvard, Houghton Library.docx",
					true),		
			new CollectionFile(
					"src/main/resources/finalized collections/MA Cambridge, Harvard, Divinity School Library.docx",
					true),
			new CollectionFile(
					"src/main/resources/finalized collections/MA Cambridge, Harvard, Harvard University Archives.docx",
					true),
			new CollectionFile(
					"src/main/resources/finalized collections/MA Cambridge, Harvard, Loeb Music Library.docx",
					true),
			new CollectionFile(
					"src/main/resources/finalized collections/MA Cambridge, Harvard, Widener Library.docx",
					true),
			new CollectionFile(
				"src/main/resources/finalized collections/RI Providence, Brown University, John Carter Brown Library.docx",
				true
			),		
			new CollectionFile(
					"src/main/resources/finalized collections/CT Hartford, Connecticut Historical Society.docx",
					true
			),		
			new CollectionFile(
					"src/main/resources/finalized collections/RI Providence, Rhode Island Historical Society.docx",
					true
			),					
			new CollectionFile(
					"src/main/resources/finalized collections/RI Providence, Brown University, John Hay Library.docx",
					true
			),	
			new CollectionFile(
					"src/main/resources/finalized collections/RI Providence, Brown University, John Carter Brown Library.docx",
					true
			),		
			new CollectionFile(
					"src/main/resources/finalized collections/RI Providence, Providence Athenaeum.docx",
					true
			),		
			new CollectionFile(
					"src/main/resources/finalized collections/RI Providence, Rhode Island Historical Society.docx",
					true
			),					
			new CollectionFile(
					"src/main/resources/finalized collections/RI Providence, Brown University, John Hay Library.docx",
					true
			)
			};
		return collectionFiles;
	}
	
	//write collections to database
	@SuppressWarnings("unused")
	private static void writeDatabase(Collections collections, String schema) {
		//write collection data to database		
		String databasePath = getLocalDBPath(),		//information
				user = "root",
				password = "password";
		collections.toDatabase(databasePath, schema, "collections", user, password);
		collections.getSources().toDatabase(databasePath, schema, "sources", user, password);
		System.out.println(collections.getEntries().toArrayList().size());
		collections.getEntries().toDatabase(databasePath, schema, "entries", user, password);
	}
	
	@SuppressWarnings("unused")
	private static void initializeDatabaseTables(String schema) {
		String databasePath = getRemoteDBPath(),		//information
				user = "musicinventory",
				password = "musicinventory";
		DatabaseWriter db = new DatabaseWriter(databasePath, schema, user, password);
		db.initializeTables();
	}
	
	private static String getLocalDBPath() {
		return "jdbc:mysql://localhost:3306/";
	}
	
	private static String getRemoteDBPath() {
		return "jdbc:mysql://musicinventory.cjatxj6dhysn.us-east-2.rds.amazonaws.com:3306/";
	}
}