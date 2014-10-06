import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.Iterator;

public class SheetReader {
	private static final int ARBITRARY_COL_SIZE = 26;
	private int rows;
	private int cols;
	
	public SheetReader() {
		rows = 0;
		cols = 0;
	}
	
	public String[][] readSheet(String filename) {
		try {
		    FileInputStream file = new FileInputStream(new File(filename));
		  	XSSFWorkbook workbook = new XSSFWorkbook(file);
		    XSSFSheet sheet = workbook.getSheetAt(0);
		    Iterator<Row> rowIterator = sheet.iterator();
		    rows = sheet.getLastRowNum() + 1;
	        cols = ARBITRARY_COL_SIZE;
	        
	        String[][] data = new String[rows][cols];
	        int rowIndex = 0;
	        int colIndex = 0;
	        int largestCol = 0;
	        
		    /* Iterate Rows */
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();
		        Iterator<Cell> cellIterator = row.cellIterator();
		        rowIndex = row.getRowNum();
	            
		        /* Iterate Columns */
		        while(cellIterator.hasNext()) {
		            Cell cell = cellIterator.next();
		            switch(cell.getCellType()) {
		            
		                case Cell.CELL_TYPE_BOOLEAN:
		                	data[rowIndex][colIndex] = String.valueOf(cell.getBooleanCellValue());
		                    break;
		                case Cell.CELL_TYPE_NUMERIC:
		                	data[rowIndex][colIndex] = String.valueOf(cell.getNumericCellValue());
		                    break;
		                case Cell.CELL_TYPE_STRING:
		                	data[rowIndex][colIndex] = cell.getStringCellValue();
		                    break;
		            }
		            colIndex++;
		            largestCol = Math.max(largestCol, colIndex);
		        }
		        colIndex = 0;
		    }
		    cols = largestCol;
		    file.close();
		    workbook.close();
			return data;
		} 
		
		catch (FileNotFoundException e) {
		    e.printStackTrace();
		} 
		
		catch (IOException e) {
		    e.printStackTrace();
		}
		return null;
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getCols() {
		return cols;
	}
	
}
