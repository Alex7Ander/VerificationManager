package GUIpack.Tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
	protected MenuItem copyMenuItem = new MenuItem("Копировать");
	protected MenuItem pasteMenuItem = new MenuItem("Вставить");
	
	public VisualTable(List<String> headLabels, Pane pane){	
		this.headLabels = headLabels;
		this.table.setEditable(true);
		for (int i = 0; i < headLabels.size(); i++) {
			TableColumn<Line, String> column = new TableColumn<Line, String>();
			final int index = i;
			column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().values.get(index)));

			
			column.setText(headLabels.get(i));
			columns.add(column);
		}
		table.getColumns().addAll(columns);	
		table.setItems(lines);
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        /*
		col1.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().values.get(0)));
		col1.setCellFactory(TextFieldTableCell.<Line>forTableColumn());
		col1.setOnEditCommit(event->{
			if(event.getNewValue() == null) {
				table.refresh();
			}
			else {
				String newValue = event.getNewValue();
				int col = event.getTablePosition().getColumn();
				int row = event.getTablePosition().getRow();
				this.lines.get(row).values.remove(col);
				this.lines.get(row).values.put(col, newValue);
			}
		});
        */
        copyMenuItem.setOnAction(new EventHandler<ActionEvent>() {
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
            	Object content = Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT);
            	StringBuilder text = new StringBuilder(content.toString());
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
            		if(i < lines.size()) {
            			lines.get(i).edit(textLine);
            		}
            		else {
            			Line line = new Line(textLine);
            			lines.add(line);
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
		Set<Integer> keys = lines.get(row).values.keySet();
		String key = keys.toArray()[col].toString();
		return lines.get(row).values.get(key);
	}

	@Override
	public void setCellValue(int col, int row, String text) {
		Set<Integer> keys = lines.get(row).values.keySet();
		int key = (int) keys.toArray()[col];
		lines.get(row).values.remove(key);
		lines.get(row).values.put(key, text);
		table.refresh();
	}

	@Override
	public void getColumn(int index, List<String> columnValues) {
		columnValues.clear();
		if (lines.isEmpty()) {
			return;
		}
		Set keys = (TreeSet) lines.get(0).values.keySet();
		String key = keys.toArray()[index].toString();
		for (Line line : lines) {
			columnValues.add(line.values.get(key));
		}		
	}

	@Override
	public void getColumnToDouble(int index, List<Double> columnValues) {
		columnValues.clear();
		if (lines.isEmpty()) {
			return;
		}
		Set keys = (TreeSet) lines.get(0).values.keySet();
		String key = keys.toArray()[index].toString();
		for (Line line : lines) {
			try {
				columnValues.add(Double.parseDouble(line.values.get(key)));
			}
			catch (NumberFormatException nfExp) {
				//
			}
		}		
	}

	@Override
	public void setColumn(int index, List<String> columnValues) {
		int stopIndex = 0;
		if (columnValues.size() > lines.size()) {
			stopIndex = columnValues.size();
		} 
		else {
			stopIndex = lines.size();
		}
		for (int i = 0; i < stopIndex; i++) {
			String text = columnValues.get(i);
			setCellValue(index, i, text);
		}		
	}

	@Override
	public void setColumnFromDouble(int index, List<Double> columnValues) {
		int stopIndex = setStopIndex(columnValues);
		for (int i = 0; i < stopIndex; i++) {
			String text = columnValues.get(i).toString();
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

	private int setStopIndex(List<Double> columnValues) {
		int stopIndex = 0;
		if (columnValues.size() > this.lines.size()) {
			stopIndex = columnValues.size();
		} 
		else {
			stopIndex = this.lines.size();
		}
		return stopIndex;
	}
	
	private String setFractionalTextPart(String text, int accuracy) {
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
}