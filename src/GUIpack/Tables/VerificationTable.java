package GUIpack.Tables;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class VerificationTable extends VisualTable {

	private static ArrayList<String> localTableHeads;
	private final int colCount = 7;
	
	public VerificationTable(Pane pane) {
		super(localTableHeads, pane);
		table.setPlaceholder(new Label("После завершения поверки нажмите на кнопку \"Получить данные\""));
		table.setPrefWidth(pane.getPrefWidth());
		table.setPrefHeight(pane.getPrefHeight());
		double colWidth = table.getPrefWidth() / colCount;
		for (int i = 0; i < table.getColumns().size(); i++) {
			table.getColumns().get(i).setPrefWidth(colWidth);
		}
	}
	
	static {
		localTableHeads = new ArrayList<String>();
		localTableHeads.add("Частота, ГГц");	
		localTableHeads.add("Измер. знач. модуля");	
		localTableHeads.add("Погрешность");	
		localTableHeads.add("Годен/не годен");	
		localTableHeads.add("Измер. знач. фазы, \u00B0");	
		localTableHeads.add("Погрешность");	
		localTableHeads.add("Годен/не годен");
	}
	
	@Override
	public int getColCount() {
		return colCount;
	}

}