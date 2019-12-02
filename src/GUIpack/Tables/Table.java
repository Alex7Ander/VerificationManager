package GUIpack.Tables;

import java.util.ArrayList;
import java.util.List;

public interface Table {
	
	public int getColCount();	
	public int getRowCount();
	
	void addRow();	
	void deleteRow(int index);
	void clear();	
	void clearColumn(int columnIndex);	
	String getHead(int index);
	
	void setHead(int index, String text);	
	String getCellValue(int col, int row);	
	void setCellValue(int col, int row, String text);
	
	void getColumn(int index, ArrayList<String> columnValues);	
	void getColumnToDouble(int index, ArrayList<Double> columnValues);
	
	void setColumn(int index, List<String> columnValues);	
	void setColumnFromDouble(int index, List<Double> columnValues);
	
	void setVisible(boolean visibleStatus);

	void delete();

}
