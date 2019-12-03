package GUIpack.StringGridFXPack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import GUIpack.Tables.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StringGridFX implements Table {

	private int colCount;
	private int rowCount;
	private VBox vBox;

	protected ObservableList<HBox> lines;	
	protected ObservableList<Label> heads;
	protected ObservableList<ObservableList<CellTextField>> cells;
	protected StringGridPosition myPosition;

	private double height;
	private double celWidth;
	
	public StringGridFX(int ColCount, int RowCount, StringGridPosition position) {
		myPosition = position;
		celWidth = myPosition.getWidth()/ColCount - 1;
		height = 27 * RowCount + 5;
		
		myPosition.getScrollContainer().setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		myPosition.getAnchorContainer().setPrefHeight(height);
		
		vBox = new VBox();
		myPosition.getScrollContainer().setContent(myPosition.getAnchorContainer());
		lines = FXCollections.observableArrayList();
		heads = FXCollections.observableArrayList();
		cells = FXCollections.observableArrayList();
		
		HBox headLine = new HBox();
		for (int h = 0; h < ColCount; h++) {
			Label hLabel = new Label("-");
			hLabel.setPrefWidth(celWidth-1);
			hLabel.setStyle("-fx-text-fill: black;" + 
						    "-fx-font-family: Arial Narrow;" + 
							"-fx-font-size: 14;");			
			heads.add(hLabel);
		}
		headLine.getChildren().addAll(heads);
		lines.add(headLine);
		
		for (int i=0; i<RowCount; i++) {
			HBox line = new HBox();
			ObservableList<CellTextField> cellsOfCurrentLine = FXCollections.observableArrayList();
			for (int j=0; j<ColCount; j++) {	
				CellTextField cell = new CellTextField(i, j);
				cell.setPrefWidth(celWidth-1);
				cell.setStyle("-fx-background-radius:0; "
							+ "-fx-border-color:black; "
							+ "-fx-border-width:1;"
							+ "-fx-font-family: Arial Narrow;"
							+ "-fx-font-size: 12;");
				cell.setOnKeyPressed(new EventHandler<KeyEvent>(){
			        @Override
			        public void handle(KeyEvent key){
			            if (key.getCode().equals(KeyCode.ENTER)){
			            	final int _i = cell.getRowIndex() + 1;
			            	final int _j = cell.getColIndex();
			            	cells.get(_i).get(_j).requestFocus();
			            }
			        }
			    });
				cellsOfCurrentLine.add(cell);
			}
			if (i == 0) this.colCount = cellsOfCurrentLine.size();
			cells.add(cellsOfCurrentLine);
			line.getChildren().addAll(cellsOfCurrentLine);
			lines.add(line);
		}
		vBox.getChildren().addAll(lines);
		myPosition.getAnchorContainer().getChildren().addAll(vBox);		
		this.rowCount = lines.size() - 1;
	}
	
	public StringGridFX(int ColCount, int RowCount, StringGridPosition position, ArrayList<String> values) {
		this(ColCount, RowCount, position);		
		for (int i = 0; i < ColCount; i++) {
			heads.get(i).setText(values.get(i));
		}
	}
	
	public StringGridFX(int ColCount, int RowCount, StringGridPosition position, ArrayList<String> values, boolean VisibleStatus) {
		this(ColCount, RowCount, position, values);
		this.setVisible(VisibleStatus);
	}
	public StringGridFX(int ColCount, int RowCount, StringGridPosition position, boolean VisibleStatus) {
		this(ColCount, RowCount, position);
		this.setVisible(VisibleStatus);
	}
	
	@Override
	public int getColCount() {
		return this.colCount;
	}
	
	@Override
	public int getRowCount() {
		return this.rowCount;
	}
	
	@Override
	public void addRow() {
		HBox line = new HBox();
		ObservableList<CellTextField> cellsOfCurrentLine = FXCollections.observableArrayList();
		for (int j = 0; j < this.colCount; j++) {				
			CellTextField cell = new CellTextField(cells.size(), j);
			cell.setPrefWidth(celWidth-1);
			cell.setStyle("-fx-background-radius:0; "
						+ "-fx-border-color:black; "
						+ "-fx-border-width:1;"
						+ "-fx-font-family: Arial Narrow;"
						+ "-fx-font-size: 12;");
			cell.setOnKeyPressed(new EventHandler<KeyEvent>(){
		        @Override
		        public void handle(KeyEvent key){
		            if (key.getCode().equals(KeyCode.ENTER)){
		            	final int _i = cell.getRowIndex() + 1;
		            	final int _j = cell.getColIndex();
		            	cells.get(_i).get(_j).requestFocus();
		            }
		        }
		    });
			cellsOfCurrentLine.add(cell);
		}
		cells.add(cellsOfCurrentLine);
		line.getChildren().addAll(cellsOfCurrentLine);
		lines.add(line);
		vBox.getChildren().add(line);
		rowCount++;
		height = 27 * this.rowCount + 20;
		myPosition.getAnchorContainer().setPrefHeight(height);
	}
	
	@Override
	public void deleteRow(int index) {
		if (index > 0) {	
			vBox.getChildren().remove(index);
			Iterator<ObservableList<CellTextField>> cellsIt = cells.iterator();
			int i = 0;
			while(i < index) {
				if (cellsIt.hasNext()) {
					cellsIt.next();						
					i++;
				}
			}
			cellsIt.remove();				
			rowCount = cells.size();
			
			height = 27 * this.rowCount + 20;
			myPosition.getAnchorContainer().setPrefHeight(height);
		}				
	}

	@Override
	public void clear() {
		for (int i=0; i<this.colCount; i++) {
			for (int j=0; j<this.rowCount; j++) {
				cells.get(j).get(i).setText("");
			}
		}
	}
	
	@Override
	public void clearColumn(int columnIndex) {
		for (int i=0; i<this.rowCount; i++) {
			cells.get(i).get(columnIndex).setText("");
		}
	}
	
	@Override
	public String getHead(int index) {
		try {			
			return heads.get(index).getText();
		}
		catch(Exception exp) {
			return null;
		}
	}
	
	@Override
	public void setHead(int index, String text) {
		try {
			heads.get(index).setText(text);
		}
		catch(Exception exp) {
			//
		}
	}
	
	@Override
	public String getCellValue(int col, int row) {
		try {
			return cells.get(row).get(col).getText();
		}
		catch(Exception exp) {
			return null;
		}
	}
	
	@Override
	public void setCellValue(int col, int row, String text) {
		try {
			cells.get(row).get(col).setText(text);
		}
		catch(Exception exp) {
			//
		}
	}
	
	@Override
	public void getColumn(int index, List<String> columnValues) {
		columnValues.clear();
		for (int i = 0; i < this.rowCount; i++) {
			columnValues.add(this.cells.get(i).get(index).getText());
		}
	}
	
	@Override
	public void getColumnToDouble(int index, List<Double> columnValues) {
		columnValues.clear();
		for (int i = 0; i < this.rowCount; i++) {
			try {
				columnValues.add(Double.parseDouble(this.cells.get(i).get(index).getText()));
			} catch (NumberFormatException nfExp) {
				columnValues.add((double) 0);
			}			
		}		
	}
	
	@Override
	public void setColumn(int index, List<String> columnValues) {
		if (columnValues.size() > rowCount) {
			while (columnValues.size() != rowCount) {
				addRow();
			}
		}	
		for (int i=0; i<columnValues.size(); i++) { 
			try {
				cells.get(i).get(index).setText(columnValues.get(i));
			} catch(Exception exp) {
				break;
			} 
		}
	}
	
	@Override
	public void setColumnFromDouble(int index, List<Double> columnValues) {
		if (columnValues.size() > rowCount) {
			while (columnValues.size() != rowCount) {
				addRow();
			}
		}	
		for (int i=0; i < this.rowCount; i++) {
			try {
				cells.get(i).get(index).setText(columnValues.get(i).toString());
			} catch(Exception exp) {
				break;
			}
		}
	}
	
	@Override
	public void setVisible(boolean visibleStatus) {
		myPosition.getAnchorContainer().setVisible(visibleStatus);
	}

	@Override
	public void delete() {
		for (HBox line : lines) {
			myPosition.getAnchorContainer().getChildren().remove(line);
		}				
	}

}