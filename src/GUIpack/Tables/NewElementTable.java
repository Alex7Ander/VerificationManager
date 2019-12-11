package GUIpack.Tables;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;

public class NewElementTable extends VisualTable {

	private static ArrayList<String> localTableHeads;
	private final int colCount = 8;
	
	public NewElementTable(List<String> headLabels, Pane pane) {
		super(headLabels, pane);
		table.setEditable(true);
		for (TableColumn<Line, String> col : columns) {
			//col.setCellFactory(TextFieldTableCell<String, String>.forTableColumn());
		}
	}
	
	static {
		localTableHeads = new ArrayList<String>();
		localTableHeads.add("№");
		localTableHeads.add("Частота, ГГц");
		localTableHeads.add("Нижний допуск");
		localTableHeads.add("Номинал");
		localTableHeads.add("Верхний допуск");
		localTableHeads.add("Нижний допуск");
		localTableHeads.add("Номинал");
		localTableHeads.add("Верхний допуск");
	}
	
	@Override
	public int getColCount() {
		return colCount;
	}

}
