package GUIpack.StringGridFXPack;

import java.util.ArrayList;

import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import VerificationPack.MeasResult;

public class ResultsStringGridFX extends StringGridFX {

	private static ArrayList<String> tableHeads;
	public ResultsStringGridFX(StringGridPosition position) {
		super(5, 10, position, tableHeads);
	}

	static {
		tableHeads = new ArrayList<String>();
		tableHeads.add("Частота, ГГц");
		tableHeads.add("Изм. знач. модуля");
		tableHeads.add("Погрешность");
		tableHeads.add("Изм. знач. фазы");
		tableHeads.add("Погрешность");
	}
	
	public void showResult(MeasResult result, S_Parametr sParam) {	
		this.clear();
		if (result.freqs.size() > this.getRowCount()) {
			while (result.freqs.size() != this.getRowCount()) {
				this.addRow();
			}
		} else if (result.freqs.size() < this.getRowCount()) {
			while (result.freqs.size() != this.getRowCount()) {
				this.deleteRow(this.getRowCount());
			}
		}
		this.setColumnFromDouble(0, result.freqs);		
		int currentRow = 0;
		for (double freq : result.freqs) {
			//Column 1 with module S
			String val = result.values.get(MeasUnitPart.MODULE + "_" + sParam).get(freq).toString();
			this.setCellValue(1, currentRow, val);
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
			this.setCellValue(3, currentRow, val);
			//Column 4 with error of phase S (it is also could not exist)
			try {
				val = result.values.get("ERROR_" + MeasUnitPart.PHASE + "_" + sParam).get(freq).toString();
			}
			catch (NullPointerException npExp) {
				val = "-";
			}
			finally {
				this.setCellValue(4, currentRow, val);
			}
						
			++currentRow;
		}
	}
	
}
