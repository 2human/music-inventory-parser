package app;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import objects.Collections;
import objects.SheetInfo;


/**
 * 
 * Parses music handwritten inscriptions in music collections recorded to Word document. Within each collection are numerous sources.
 * Each source may contain numerous entries (handwritten melodic incipits, which are sometimes accompanied by text incipits)
 * Author recorded these into .docx Word files with consistent syntax, making it possible to parse collections into table format
 * 
 * @author Andrew
 * 
 */

public class ParseCollectionApp {
	public static void main(String[] args) {
		Collections collections = new Collections(getCurrentCollectionFiles(), false);
//		//write spreadsheets
		deleteOldSpreadhsset();		
		writeSpreadsheet(collections);
		openSpreadsheet();	
		//write database
//		writeDatabase(collections);
		
		System.out.println("Operations complete.");
	}
	
	private static void deleteOldSpreadhsset() {
		File file = new File("src/main/resources/spreadsheet outputs/parsed_collections.xlsx");
		file.delete();
	} 
	
	@SuppressWarnings("unused")
	private static File[] getCurrentCollectionFiles() {
		File[] files = {
				new File("src/main/resources/finalized collections/CT Hartford, Connecticut Historical Society.docx"),
			};
		return files;
	}	
	
	@SuppressWarnings("unused")
	private static File[] getPreParsedFiles() {
		File[] files = { 				
				new File("src/main/resources/finalized collections/MA Cambridge, Harvard, Divinity School Library--sacred music INVENTORY.docx"),
				new File("src/main/resources/finalized collections/MA Cambridge, Harvard, Harvard University Archives--sacred music INVENTORY.docx"),
				new File("src/main/resources/finalized collections/MA Cambridge, Harvard, Loeb Music Library--sacred music INVENTORY.docx"),
				new File("src/main/resources/finalized collections/MA Cambridge, Harvard, Widener Library--sacred music INVENTORY.docx"),

			};
		return files;
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
	private static File[] getFinalizedCollectionFiles() {
		File [] files = {
			new File("src/main/resources/finalized collections/CT Hartford, Connecticut Historical Society.docx"),
			new File ("src/main/resources/finalized collections/CT Hartford, Watkinson Library, Trinity College.docx"),
			new File ("src/main/resources/finalized collections/MA Petersham, Nym Cooke collection.docx"),
			new File ("src/main/resources/finalized collections/MA Boston, Congregational Library and Archives.docx"),
			new File("src/main/resources/finalized collections/MA Boston, Boston Athenaeum.docx"),
			new File ("src/main/resources/finalized collections/MA Andover, Andover Center for History and Culture.docx"),
			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 1.docx"),
			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 2.docx"),
			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 3.docx"),
			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 4.docx"),
			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 5.docx"),
			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 6.docx"),
			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 7.docx"),
			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 8.docx"),
			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 9.docx"),
			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 10.docx"),
			new File("src/main/resources/finalized collections/MA Boston, Massachusetts Historical Society--sacred music INVENTORY.docx")
			};
		return files;
	}
	
	//write collections to database
	@SuppressWarnings("unused")
	private static void writeDatabase(Collections collections) {
		//write collection data to database		
		String schema = "testcollections", 						//database 					
				databasePath = "jdbc:mysql://localhost:3306/",		//information
				user = "root",
				password = "password";
		collections.toDatabase(databasePath, schema, "collections", user, password);
		collections.getSources().toDatabase(databasePath, schema, "sources", user, password);
		System.out.println(collections.getEntries().toArrayList().size());
		collections.getEntries().toDatabase(databasePath, schema, "entries", user, password);
	}
	
}