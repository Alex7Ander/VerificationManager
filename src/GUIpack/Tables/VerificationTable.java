package GUIpack.Tables;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class VerificationTable extends VisualTable {

	private static ArrayList<String> localTableHeads;
	private final int colCount = 13;
	
	public VerificationTable(Pane pane) {
		super(localTableHeads, pane);
		table.setPlaceholder(new Label("����� ���������� ������� ������� �� ������ \"�������� ������\""));
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
		localTableHeads.add("F, ���");	//0
		localTableHeads.add("�����. ����. ������");	//1
		localTableHeads.add("�����������");
		localTableHeads.add("����. ���. ������");
		localTableHeads.add("\u03B4, %");
		localTableHeads.add("������");				
		localTableHeads.add("�����������\n\t���");	
		
		localTableHeads.add("�����. ����.\n����, \u00B0"); //7
		localTableHeads.add("�����������");
		localTableHeads.add("����. ���.\n����, \u00B0");
		localTableHeads.add("\u0394");
		localTableHeads.add("������");			
		localTableHeads.add("�����������\n\t���");
	}
	
	@Override
	public int getColCount() {
		return colCount;
	}

}