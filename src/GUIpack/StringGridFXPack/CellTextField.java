package GUIpack.StringGridFXPack;

import javafx.scene.control.TextField;

public class CellTextField extends TextField {
	
	private int rowIndex;
	private int colIndex;
	
	CellTextField(int row, int col){
		super();
		this.rowIndex = row;
		this.colIndex = col;
	}
	
	public int getRowIndex() {
		return rowIndex;
	}
	public int getColIndex() {
		return colIndex;
	}	
}