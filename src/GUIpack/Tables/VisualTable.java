package GUIpack.Tables;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.Pane;

public class VisualTable implements Table {
		
	protected List<String> headLabels = new ArrayList<String>();
	protected List<String> headKeys = new ArrayList<String>();
	
	protected TableView<Line> table = new TableView<Line>();
	protected ObservableList<Line> lines = FXCollections.observableArrayList();
	protected ArrayList<TableColumn<Line, String>> columns = new ArrayList<TableColumn<Line, String>>();
	
	protected Pane pane;
	
	protected ContextMenu tableMenu = new ContextMenu();
	protected MenuItem copyMenuItem = new MenuItem("����������");
	protected MenuItem pasteMenuItem = new MenuItem("��������");
	
	public VisualTable(List<String> headLabels, Pane pane){	
		this.headLabels = headLabels;
		table.setEditable(true);
		for (int i = 0; i < headLabels.size(); i++) {
			TableColumn<Line, String> column = new TableColumn<Line, String>();
			final int index = i;
			column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().values.get(index)));
			
			column.setText(headLabels.get(i));
			column.setSortable(false);
			columns.add(column);
		}
		table.getColumns().addAll(columns);	
		table.setItems(lines);
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        copyMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @SuppressWarnings("rawtypes")
			@Override
            public void handle(ActionEvent event) {
				ObservableList<TablePosition> posList = table.getSelectionModel().getSelectedCells();               
                int old_r = -1;
                StringBuilder clipboardString = new StringBuilder();
                for (TablePosition p : posList) {
                    int r = p.getRow();
                    int c = p.getColumn();
                    Object cell = table.getColumns().get(c).getCellData(r);
                    if (cell == null)
                        cell = "";
                    if (old_r == r)
                        clipboardString.append('\t');
                    else if (old_r != -1)
                        clipboardString.append('\n');
                    clipboardString.append(cell);
                    old_r = r;
                }
                final ClipboardContent content = new ClipboardContent();
                content.putString(clipboardString.toString());
                Clipboard.getSystemClipboard().setContent(content);
            }
        });
        
        pasteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	TablePosition pos = (TablePosition) table.getSelectionModel().getSelectedCells().get(0);
            	int startColumnIndex = pos.getColumn();
            	int startRowIndex = pos.getRow();
            	Object content = Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT);
            	String stringContent = content.toString().replace("\r", "");
            	StringBuilder text = new StringBuilder(stringContent);
            	text.append("\n");
            	int i = 0;
            	while(text.length() > 0) {
            		List<String> textLine = new ArrayList<>();
            		int index = text.indexOf("\n");
            		StringBuilder currentTextLine = new StringBuilder(text.substring(0, index));
            		currentTextLine.append("\t");
            		while(currentTextLine.length() > 0) {
            			int tabIndex = currentTextLine.indexOf("\t");
            			String value = currentTextLine.substring(0, tabIndex);
            			textLine.add(value);
            			currentTextLine.delete(0, tabIndex + 1);
            		}
            		text.delete(0, index + 1);
            		
            		int editableLineIndex = startRowIndex + i;
            		if(editableLineIndex < lines.size()) {
            			lines.get(editableLineIndex).edit(startColumnIndex, textLine);
            		}
            		++i;
            	}
            	table.refresh();
            }
        });
        
        tableMenu.getItems().addAll(copyMenuItem, pasteMenuItem);
        table.setOnContextMenuRequested(event -> {
        	double x = event.getScreenX();
        	double y = event.getScreenY();
        	tableMenu.show(table, x, y);
        });
        
		pane.getChildren().add(table);
	}
	
	@Override
	public int getColCount() {
		return table.getColumns().size();
	}

	@Override
	public int getRowCount() {
		if (table.getColumns().isEmpty()) {
			return 0;
		}
		return lines.size();
	}

	@Override
	public void addRow() {
		Line line = new Line(headLabels.size());
		lines.add(line);		
		table.refresh();
	}

	@Override
	public void deleteRow(int index) {
		lines.remove(index - 1);		
		table.refresh();		
	}

	@Override
	public void clear() {
		lines.clear();
		Line line = new Line(headLabels.size());
		lines.add(line);
		table.refresh();
	}

	@Override
	public void clearColumn(int columnIndex) {
		for (Line line : lines) {
			line.deleteValue(columnIndex);
		}
		table.refresh();
	}

	@Override
	public String getHead(int index) {
		return headLabels.get(index);
	}

	@Override
	public void setHead(int index, String text) {
		columns.get(index).setText(text);		
	}

	@Override
	public String getCellValue(int col, int row) {
		return lines.get(row).values.get(col);
	}

	@Override
	public void setCellValue(int col, int row, String text) {
		try {
			lines.get(row).values.remove(col);
			lines.get(row).values.put(col, text);
		}
		catch(IndexOutOfBoundsException iExp) {
			System.out.println("����������� ������� ���������� �������� � ������� " + this.getClass().getName() + " � ������ [col, row]= [" + col + ", " + row + "]");
		}
		finally {
			table.refresh();
		}
		
	}

	@Override
	public void getColumn(int index, List<String> columnValues) {
		columnValues.clear();
		if (lines.isEmpty()) {
			return;
		}		
		for (Line line : lines) {
			columnValues.add(line.values.get(index));
		}		
	}

	@Override
	public void getColumnToDouble(int index, List<Double> columnValues) {
		columnValues.clear();
		if (lines.isEmpty()) {
			return;
		}
		for (Line line : lines) {
			try {
				columnValues.add(Double.parseDouble(line.values.get(index)));
			}
			catch (NumberFormatException nfExp) {
				//
			}
		}		
	}

	@Override
	public void setColumn(int index, List<String> columnValues) {
		int stopIndex = setStopIndex(columnValues);
		for (int i = 0; i < stopIndex; i++) {
			String text = null;
			if (columnValues.size() > i) {
				text = columnValues.get(i);
			}
			else {
				text = "-";
			}
			setCellValue(index, i, text);
		}		
	}
	
	@Override
	public void setColumnWithCondition(int index, List<String> columnValues, List<Double> conditionValues, Predicate<Double> std) {
		int stopIndex = setStopIndex(columnValues);
		for (int i = 0; i < stopIndex; i++) {
			String text = null;
			boolean isStandardized = std.test(conditionValues.get(i));
			if ( columnValues.size() > i) {
				if(isStandardized) {
					text = columnValues.get(i);
				}
				else {
					text = "�� �����������";
				}
			}
			else {
				text = "-";
			}
			setCellValue(index, i, text);
		}		
	}

	@Override
	public void setColumnFromDouble(int index, List<Double> columnValues) {
		int stopIndex = setStopIndex(columnValues);
		for (int i = 0; i < stopIndex; i++) {
			String text = null;
			if (columnValues.size() > i) {
				text = columnValues.get(i).toString();
			}
			else {
				text = "-";
			}
			setCellValue(index, i, text);
		}		
	}
	
	@Override
	public void setColumnFromDouble(int index, List<Double> columnValues, int accuracy) {
		int stopIndex = setStopIndex(columnValues);		
		for (int i = 0; i < stopIndex; i++) {
			String text = setFractionalTextPart(columnValues.get(i).toString(), accuracy);			
			setCellValue(index, i, text);
		}		
	}
	
	@Override
	public void setColumnFromDoubleWithCondition(int index, List<Double> columnValues, List<Double> conditionValues, int accuracy, Predicate<Double> std) {
		int stopIndex = setStopIndex(columnValues);
		for (int i = 0; i < stopIndex; i++) {
			String text = null;
			boolean isStandardized = std.test(conditionValues.get(i));
			if (columnValues.size() > i) {
				if(isStandardized) {
					text = columnValues.get(i).toString();
				}
				else {
					text = "�� �����������";	
				}
			}
			else {
				text = "-";
			}
			setCellValue(index, i, text);
		}		
	}

	protected int setStopIndex(List<?> columnValues) {
		int stopIndex = 0;
		if (columnValues.size() > this.lines.size()) {
			stopIndex = columnValues.size();
		} 
		else {
			stopIndex = this.lines.size();
		}
		return stopIndex;
	}
	
	protected String setFractionalTextPart(String text, int accuracy) {
		String fractional = text.substring(text.lastIndexOf('.') + 1);
		int fractionalLength = fractional.length();
		if(accuracy > fractionalLength) {
			while (fractionalLength < accuracy) {
				text = text.concat("0");
				fractionalLength ++;
			}
		}
		return text;
	}
	
	@Override
	public void delete() {
		for (int i = 0; i < pane.getChildren().size(); i++) {
			pane.getChildren().remove(i);
		}		
	}

	@Override
	public void setVisible(boolean visibleStatus) {
		table.setVisible(visibleStatus);		
	}
	
	public void setEditable(boolean editble) {
		this.table.setEditable(editble);
	}
}