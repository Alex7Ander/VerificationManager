package GUIpack.Tables;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

public class VisualTable implements Table {
		
	protected List<String> heads = new ArrayList<String>();
	
	protected TableView<Line> table = new TableView<Line>();
	protected ObservableList<Line> lines = FXCollections.observableArrayList();
	protected ArrayList<TableColumn<Line, String>> columns = new ArrayList<>();
	
	protected Pane pane;
	
	public VisualTable(List<String> heads, Pane pane){	
		this.heads = heads;
		for (int i = 0; i < heads.size(); i++) {
			TableColumn<Line, String> column = new TableColumn<Line, String>();
			final int index = i;
			column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().values.get(index)));
			columns.add(column);
		}
		table.getColumns().addAll(columns);	
		table.setItems(lines);
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
		Line line = new Line(heads);
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
		Line line = new Line(heads);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHead(int index, String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCellValue(int col, int row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCellValue(int col, int row, String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getColumn(int index, ArrayList<String> columnValues) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getColumnToDouble(int index, ArrayList<Double> columnValues) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColumn(int index, List<String> columnValues) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColumnFromDouble(int index, List<Double> columnValues) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVisible(boolean visibleStatus) {
		// TODO Auto-generated method stub
		
	}

	
}
