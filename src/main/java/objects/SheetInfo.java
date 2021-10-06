package objects;

import java.io.File;
/**
 * Contains information about spreadsheet to be written by SpreadsheetWriter.
 * @author Andrew
 *
 */

public class SheetInfo {

	String sheetName;
	String workbookPath;
	String workbookName;
	
	public SheetInfo(String workbookPath, String workbookName, String sheetName){
		this.workbookPath = workbookPath;
		this.workbookName = workbookName;
		this.sheetName = sheetName;
	}	

	public String getWorkbookPath() {
		return workbookPath;
	}

	public String getWorkbookName() {
		return workbookName;
	}

	public String getSheetName() {
		return sheetName;
	}
	
	public File getWorkbookFile() {
		String path = workbookPath + workbookName + ".xlsx";
		return new File(path);
	}
	
}
