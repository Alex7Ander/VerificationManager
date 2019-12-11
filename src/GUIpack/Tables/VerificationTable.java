package GUIpack.Tables;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class VerificationTable extends VisualTable {

	private static ArrayList<String> localTableHeads;
	private final int colCount = 7;
	
	public VerificationTable(Pane pane) {
		super(localTableHeads, pane);
		table.setPlaceholder(new Label("����� ���������� ������� ������� �� ������ \"�������� ������\""));
		table.setPrefWidth(pane.getPrefWidth());
		table.setPrefHeight(pane.getPrefHeight());
		double colWidth = table.getPrefWidth() / colCount;
		for (int i = 0; i < table.getColumns().size(); i++) {
			table.getColumns().get(i).setPrefWidth(colWidth);
		}
	}
	
	static {
		localTableHeads = new ArrayList<String>();
		localTableHeads.add("�������, ���");	
		localTableHeads.add("�����. ����. ������");	
		localTableHeads.add("�����������");	
		localTableHeads.add("�����/�� �����");	
		localTableHeads.add("�����. ����. ����, \u00B0");	
		localTableHeads.add("�����������");	
		localTableHeads.add("�����/�� �����");
	}
	
	@Override
	public int getColCount() {
		return colCount;
	}

}