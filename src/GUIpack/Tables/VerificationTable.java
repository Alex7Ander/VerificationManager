package GUIpack.Tables;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class VerificationTable extends VisualTable {

	private static ArrayList<String> localTableHeads;
	private final int colCount = 11;
	
	public VerificationTable(Pane pane) {
		super(localTableHeads, pane);
		table.setPlaceholder(new Label("После завершения поверки нажмите на кнопку \"Получить данные\""));
		table.setPrefWidth(pane.getPrefWidth());
		table.setPrefHeight(pane.getPrefHeight());
		
		table.getColumns().get(0).setPrefWidth(57);
		double colWidth = (table.getPrefWidth() - 57)/(colCount - 1);
		for (int i = 1; i < table.getColumns().size(); i++) {
			table.getColumns().get(i).setPrefWidth(colWidth);
		}
	}
	
	static {
		localTableHeads = new ArrayList<String>();
		localTableHeads.add("F, ГГц");	
		localTableHeads.add("Измер. знач. модуля");	
		localTableHeads.add("Пред. пов. модуля");
		localTableHeads.add("Допуск");		
		localTableHeads.add("Погрешность");			
		localTableHeads.add("Соответсвие\n\tНТД");	
		
		localTableHeads.add("Измер. знач.\nфазы, \u00B0");	
		localTableHeads.add("Пред. пов.\nфазы, \u00B0");
		localTableHeads.add("Допуск");
		localTableHeads.add("Погрешность");	
		localTableHeads.add("Соответсвие\n\tНТД");
	}
	
	@Override
	public int getColCount() {
		return colCount;
	}

}