package GUIpack.Tables;

import java.util.ArrayList;
import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import VerificationPack.MeasResult;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class ResultsTable extends VisualTable implements ResultsRepresentable {

	private static ArrayList<String> localTableHeads;
	private final int colCount = 5;
	
	public ResultsTable(Pane pane) {
		super(localTableHeads, pane);
		table.setPlaceholder(new Label("Устройство не выбрано"));
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
		localTableHeads.add("Изм. знач. модуля");
		localTableHeads.add("Погрешность");
		localTableHeads.add("Изм. знач. фазы, \u00B0");
		localTableHeads.add("Погрешность");
	}
	
	@Override
	public int getColCount() {
		return colCount;
	}
	
	@Override
	public void showResult(MeasResult result, S_Parametr sParam) {
		this.clear();
		if (result.freqs.size() > getRowCount()) {
			while (result.freqs.size() != getRowCount()) {
				addRow();
			}
		} else if (result.freqs.size() < getRowCount()) {
			while (result.freqs.size() != getRowCount()) {
				deleteRow(getRowCount());
			}
		}
		setColumnFromDouble(0, result.freqs);		
		int currentRow = 0;
		for (double freq : result.freqs) {
			//Column 1 with module S
			String val = result.values.get(MeasUnitPart.MODULE + "_" + sParam).get(freq).toString();
			setCellValue(1, currentRow, val);
			//Column 2 with error of module S (it could not exist)
			try {
				val = result.values.get("ERROR_" + MeasUnitPart.MODULE + "_" + sParam).get(freq).toString();				
			} 
			catch(NullPointerException npExp) {
				val = "-";
			}
			finally {
				this.setCellValue(2, currentRow, val);
			}
			//Column 3 with phase S
			val = result.values.get(MeasUnitPart.PHASE + "_" + sParam).get(freq).toString();
			setCellValue(3, currentRow, val);
			//Column 4 with error of phase S (it is also could not exist)
			try {
				val = result.values.get("ERROR_" + MeasUnitPart.PHASE + "_" + sParam).get(freq).toString();
			}
			catch (NullPointerException npExp) {
				val = "-";
			}
			finally {
				setCellValue(4, currentRow, val);
			}						
			++currentRow;
		}
	}

}
