package main.java.uk.ac.ox.cs.krr.lopster;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 
/* This class contains methods for creating rows of an excel spreadsheet
 * and writing data on cells of the spreadsheet
*/

public class ExcelWriter {
	protected HSSFWorkbook m_WorkBook;
    protected HSSFSheet m_Sheet;
    
    public ExcelWriter() {
    	m_WorkBook = new HSSFWorkbook();
        m_Sheet = m_WorkBook.createSheet();
    }
    
    //this method transcripts an array of attributes at the top row of a spreadsheet
    public void createAttributes(String... attributes) {
    	HSSFRow attRow = m_Sheet.createRow(0);
    	for (int i=0; i<attributes.length; i++) {
    		HSSFCell cell = attRow.createCell(i);
    		cell.setCellValue(attributes[i]);
    	}
    }
    
    //this method creates a new row on the specified row number
    //and transcripts dataArray at that row
    public HSSFRow newRow(int rowNum, String... dataArray) {
    	HSSFRow row = m_Sheet.createRow(rowNum);
    	for (int i=0; i<dataArray.length; i++) {
    		HSSFCell cell = row.createCell(i);
    		cell.setCellValue(dataArray[i]);
    	}
    	return row;
    }
    
    //this method creates a new row on the spcified row number
    //with the specified number of columns
    public HSSFRow newRow(int rowNum, int totalCol) {
    	HSSFRow row = m_Sheet.createRow(rowNum);
    	for (int i=0; i<totalCol; i++) {
    		row.createCell(i);
    	}
    	return row;
    }
    
    //writes string data at the specified row and column
    public void writeCell(HSSFRow row, int colNum, String data) {
    	HSSFCell cell = row.getCell(colNum);
    	cell.setCellValue(data);
    }
    
    //writes int data at the specified row and column
    public void writeCell(HSSFRow row, int colNum, int data) {
    	HSSFCell cell = row.getCell(colNum);
    	cell.setCellValue(data);
    }
    
    //writes double data at the specified row and column
    public void writeCell(HSSFRow row, int colNum, double data) {
    	HSSFCell cell = row.getCell(colNum);
    	cell.setCellValue(data);
    }
    
    //writes long data at the specified row and column  
    public void writeCell(HSSFRow row, int colNum, long data) {
    	HSSFCell cell = row.getCell(colNum);
    	cell.setCellValue(data);
    }
    
    //adjusts the columnd width to fit the contents for each column
    public void adjustAllColumns(int totalColumns){
    	for (int i=0; i<totalColumns; i++){
    		m_Sheet.autoSizeColumn(i);
    	}
    }
 
    //writes all the data stored in m_WorkBook at the file specified 
    //by fileName
    public void writeToExcelFile(String fileName) {
        try {
        	FileOutputStream out = new FileOutputStream(fileName);
            m_WorkBook.write(out);
            out.close();
        }
        catch(IOException e) { 
        	e.printStackTrace();
        }
    }
 
}