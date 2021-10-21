package writers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import objects.ColumnInfo;
import objects.SheetInfo;

public class SpreadsheetWriter {
	
	private Workbook workbook;			//workbook being created
	private Sheet sheet;				//sheet within workbook
	private CellStyle style;
	private FileOutputStream fos;		//excel file output
	private Row row;					//spreadsheet row
	private Cell cell;					//spreadsheet cell
	private int curRow;							//row to which next entry will be written
	private static final int WIDTH_CONSTANT = 256;	//constant by which cell width will be multiplied
												//Apache POI cell width unit of measurement is 1/256th of character width
	private File file;
	
	public SpreadsheetWriter(ColumnInfo columnInfo, SheetInfo sheetInfo) throws Exception{
		file = sheetInfo.getWorkbookFile();		//excel file being read/written to
		if(file.exists()) {										//check if file exists
			initializeExistingWorkbook(file);
		}
		else {
			createBlankWorkbook(file);
		}
		initializeCurrentSheet(sheetInfo.getSheetName());
		initializeCellStyle();
		fos = new FileOutputStream(file);
		initializeCurrentRow();
		if(isNewSheet()) {
			for(int i = 0; i < columnInfo.getLabels().length; i++) {			//for the length of column label array	
				writeColumnLabel(columnInfo.getLabels(), i);
				formatColumn(columnInfo.getWidths(), i);
			}
		}
	}
	
	//opens existing workbook for editing
	private void initializeExistingWorkbook(File file) throws Exception{
		FileInputStream fis = new FileInputStream(file);
		workbook = new XSSFWorkbook(fis);					//if it does exist, open file	
	}	
	
	private void createBlankWorkbook(File file){
		workbook = new XSSFWorkbook();						//create blank workbook		
	}	
	
	//initialize sheet that is about to be written to
	private void initializeCurrentSheet(String sheetName){
		sheet = workbook.getSheet(sheetName);				//get sheet that will be written to
		if(sheet == null) {									//if sheet does not exist										
			sheet = workbook.createSheet(sheetName);			//create sheet
		}	
	}
	
	//create style that will be applied to forthcoming cells that are created
	private void initializeCellStyle() {
		style = workbook.createCellStyle();
		style.setWrapText(true);			//make it so text in sheet will be wrapped
	}
	
	//initialize current row for operations
	private void initializeCurrentRow() {
		curRow = sheet.getLastRowNum() + 1;		//set first row that will be written to
		//if new sheet (current row is 1), start at 0 instead of 1(no last row)	to prepare to write column labels
		if(isNewSheet()) curRow--;
		row = sheet.createRow(curRow++);		//create new row to 
	}
	
	private boolean isNewSheet() {
		return curRow == 1;					//only cases when row will be 1 is when it is new sheet
	}
	
	
	private void writeColumnLabel(String[] columnLabels, int index) {
		cell = row.createCell(index);					//create cell
		cell.setCellStyle(style);						//apply cell style
		cell.setCellValue(columnLabels[index]);			//then write column label	
		System.out.println(columnLabels[index]);
	}
	
	private void formatColumn(int[] columnWidths, int index) {
		sheet.setColumnWidth(index, columnWidths[index] * WIDTH_CONSTANT);	//set width of column
	}
	
	//write row with string array as parameter
	public void writeRow(String[] dataArr) throws Exception {
		row = sheet.createRow(curRow++);			//create new row that will be written to
		for(int i = 0; i < dataArr.length; i++) {	//for all strings in data array
			writeCell(dataArr, i);
		}
	}
	
	private void writeCell(String[] dataArr, int index) {
		cell = row.createCell(index);				//create cell to be written to
		if(index > 0) applyCellStyle();
		cell.setCellValue(dataArr[index]);			//write string to cell
	}
	
	//apply cell style to current cell
	private void applyCellStyle() {
		cell.setCellStyle(style);		
	}
	
	//finalize document and close stream
	public void closeStream() throws IOException {
		workbook.write(fos);						//write data to document
		fos.close();								//close stream
	}
	
}
