package GUIpack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class StringGridFX {

	private int colCount;
	private int rowCount;
	private ScrollPane scrollContainer;
	private Pane ownerContainer;
	private VBox vBox;
	
	private ObservableList<HBox> lines;	
	private ObservableList<Label> heads;
	private ObservableList<ObservableList<TextField>> cells;
	
	private ArrayList<ArrayList<String>> cellsValues;
	
	private double height;
	private double celHeight;
	private double width;
	private double celWidth;
	
	public StringGridFX(int ColCount, int RowCount, double Width, double Height, ScrollPane ScrollContainer, Pane container) {
		
		celWidth = Width/ColCount - 1;
		celHeight = 27;
		height = 27 * RowCount + 5;
		width = Width;
		
		System.out.println("Container width is " + width);
		
		scrollContainer = ScrollContainer;
		scrollContainer.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		
		ownerContainer = container;
		ownerContainer.setPrefHeight(height);
		vBox = new VBox();
		ScrollContainer.setContent(ownerContainer);
		lines = FXCollections.observableArrayList();
		heads = FXCollections.observableArrayList();
		cells = FXCollections.observableArrayList();
		
		HBox headLine = new HBox();
		for (int h=0; h<ColCount; h++) {
			Label hLabel = new Label("-");
			hLabel.setPrefWidth(celWidth-1);
			hLabel.setStyle("-fx-text-fill: black;" + 
						    "-fx-font-family: Arial Narrow;" + 
							"-fx-font-size: 14;");		//-fx-font-weight: bold;	
			heads.add(hLabel);
		}
		headLine.getChildren().addAll(heads);
		lines.add(headLine);
		
		for (int i=0; i<RowCount; i++) {
			HBox line = new HBox();
			ObservableList<TextField> cellsOfCurrentLine = FXCollections.observableArrayList();
			for (int j=0; j<ColCount; j++) {				
				TextField cell = new TextField();
				cell.setPrefWidth(celWidth-1);
				cell.setStyle("-fx-background-radius:0; "
							+ "-fx-border-color:black; "
							+ "-fx-border-width:1;"
							+ "-fx-font-family: Arial Narrow;"
							+ "-fx-font-size: 12;");
				cellsOfCurrentLine.add(cell);
			}
			if (i == 0) this.colCount = cellsOfCurrentLine.size();
			cells.add(cellsOfCurrentLine);
			line.getChildren().addAll(cellsOfCurrentLine);
			lines.add(line);
		}
		vBox.getChildren().addAll(lines);
		ownerContainer.getChildren().addAll(vBox);		
		this.rowCount = lines.size() - 1;
	}
	
	public StringGridFX(int ColCount, int RowCount, double Width, double Height, ScrollPane ScrollContainer, AnchorPane container, ArrayList<String> values) {
		this(ColCount, RowCount, Width, Height, ScrollContainer, container);
		int count = heads.size();
		int i = 0;
		while(i != count) {
			this.heads.get(i).setText(values.get(i));
			i++;
		}
	}
	
	public StringGridFX(int ColCount, int RowCount, double Width, double Height, ScrollPane ScrollContainer, AnchorPane container, ArrayList<String> values, boolean VisibleStatus) {
		this(ColCount, RowCount, Width, Height, ScrollContainer, container, values);
		this.setVisible(VisibleStatus);
	}
	public StringGridFX(int ColCount, int RowCount, double Width, double Height, ScrollPane ScrollContainer, AnchorPane container, boolean VisibleStatus) {
		this(ColCount, RowCount, Width, Height, ScrollContainer, container);
		this.setVisible(VisibleStatus);
	}
		
	public int getColCount() {
		return this.colCount;
	}
	
	public int getRowCount() {
		return this.rowCount;
	}
	
	public void addRow() {
		HBox line = new HBox();
		ObservableList<TextField> cellsOfCurrentLine = FXCollections.observableArrayList();
		for (int j=0; j<this.colCount; j++) {				
			TextField cell = new TextField();
			cell.setPrefWidth(celWidth-1);
			cell.setStyle("-fx-background-radius:0; "
						+ "-fx-border-color:black; "
						+ "-fx-border-width:1;"
						+ "-fx-font-family: Arial Narrow;"
						+ "-fx-font-size: 12;");
			cellsOfCurrentLine.add(cell);
		}
		cells.add(cellsOfCurrentLine);
		line.getChildren().addAll(cellsOfCurrentLine);
		lines.add(line);
		vBox.getChildren().add(line);
		this.rowCount++;
		
		height = 27 * this.rowCount + 20;
		ownerContainer.setPrefHeight(height);
	}
	
	public void deleteRow(int index) {
		if (index > 0) {	
			vBox.getChildren().remove(index);
			Iterator<ObservableList<TextField>> cellsIt = cells.iterator();
			int i = 0;
			while(i < index) {
				if (cellsIt.hasNext()) {
					cellsIt.next();						
					i++;
				}
			}
			cellsIt.remove();				
			this.rowCount = cells.size();
			
			height = 27 * this.rowCount + 20;
			ownerContainer.setPrefHeight(height);
		}				
	}

	public void clear() {
		for (int i=0; i<this.colCount; i++) {
			for (int j=0; j<this.rowCount; j++) {
				this.cells.get(j).get(i).setText("");
			}
		}
	}
	
	public String getHead(int index) {
		try {			
			return this.heads.get(index).getText();
		}
		catch(Exception exp) {
			return null;
		}
	}
	
	public void setHead(int index, String text) {
		try {
			this.heads.get(index).setText(text);
		}
		catch(Exception exp) {
			//
		}
	}
	
	public String getCellValue(int col, int row) {
		try {
			return this.cells.get(row).get(col).getText();
		}
		catch(Exception exp) {
			return null;
		}
	}
	
	public void setCellValue(int col, int row, String text) {
		try {
			this.cells.get(row).get(col).setText(text);
		}
		catch(Exception exp) {
			//
		}
	}
	
	public void getColumn(int index, ArrayList<String> columnValues) {
		columnValues.clear();
		for (int i = 0; i < this.rowCount; i++) {
			columnValues.add(this.cells.get(i).get(index).getText());
		}
	}
	
	public void getColumnToDouble(int index, ArrayList<Double> columnValues) throws NumberFormatException {
		columnValues.clear();
		for (int i = 0; i < this.rowCount; i++) {
			columnValues.add(Double.parseDouble(this.cells.get(i).get(index).getText()));
		}		
	}
	
	public void setColumn(int index, ArrayList<String> columnValues) {
		for (int i=0; i<columnValues.size(); i++) { // было for (int i=0; i<this.rowCount; i++)
			this.cells.get(i).get(index).setText(columnValues.get(i));
		}
	}
	
	public void setColumnFromDouble(int index, ArrayList<Double> columnValues) {
		for (int i=0; i<this.rowCount; i++) {
			this.cells.get(i).get(index).setText(columnValues.get(i).toString());
		}
	}
	
	public void setVisible(boolean visibleStatus) {
		this.scrollContainer.setVisible(visibleStatus);
	}


}
