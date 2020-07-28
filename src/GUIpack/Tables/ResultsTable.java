package GUIpack.Tables;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import VerificationPack.MeasResult;
import _tempHelpers.Adapter;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class ResultsTable extends VisualTable {
	
	private final int colCount = 5;
	
	private static ArrayList<String> localTableHeads;
	private static int vswrAccuracy;
	private static int vswrErrorAccuracy;
	private static int phaseAccuracy;
	private static int phaseErrorAccuracy;
	
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
		
		FileInputStream fis;
        Properties property = new Properties();
        String propPath = new File(".").getAbsolutePath() + "/files/aksol.properties";
        try {       	
            fis = new FileInputStream(propPath);
            property.load(fis);

            vswrAccuracy = Integer.parseInt(property.getProperty("verification.vswrAccuracy"));
            vswrErrorAccuracy = Integer.parseInt(property.getProperty("varification.vswrErrorAccuracy"));
            phaseAccuracy = Integer.parseInt(property.getProperty("verification.phaseAccuracy"));
            phaseErrorAccuracy = Integer.parseInt(property.getProperty("verification.phaseErrorAccuracy"));
            
            System.out.println("vswrAccuracy: " + vswrAccuracy
                            + ", vswrAccuracy: " + vswrAccuracy
                            + ", phaseAccuracy: " + phaseAccuracy
                            + ", phaseErrorAccuracy: " + phaseErrorAccuracy);
            fis.close();

        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств по адресу " + propPath + " отсуствует!");
            vswrAccuracy = 3;
            vswrErrorAccuracy = 3;
            phaseAccuracy = 2;
            phaseErrorAccuracy = 2;
        } catch (NumberFormatException nfExp) {
        	System.err.println("ОШИБКА: неправильный формат данных для преобразования");
            vswrAccuracy = 3;
            vswrErrorAccuracy = 3;
            phaseAccuracy = 2;
            phaseErrorAccuracy = 2;
        }
	}
	
	@Override
	public int getColCount() {
		return colCount;
	}
	
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
		setColumnFromDouble(1,  Adapter.MapToArrayList(result.values.get(MeasUnitPart.MODULE + "_" + sParam)), vswrAccuracy);
		setColumnFromDouble(2,  Adapter.MapToArrayList(result.values.get("ERROR_" + MeasUnitPart.MODULE + "_" + sParam)), vswrErrorAccuracy);
		setColumnFromDouble(3,  Adapter.MapToArrayList(result.values.get(MeasUnitPart.PHASE + "_" + sParam)), phaseAccuracy);
		setColumnFromDouble(4,  Adapter.MapToArrayList(result.values.get("ERROR_" + MeasUnitPart.PHASE + "_" + sParam)), phaseErrorAccuracy);
	}
	
}