package GUIpack.StringGridFXPack;

import java.util.ArrayList;

import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.TimeType;
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationPack.MeasResult;
import _tempHelpers.Adapter;

public class NewElementStringGridFX extends StringGridFX {

	private static ArrayList<String> tableHeads;
	private MeasResult nominal;
	private ToleranceParametrs primaryModuleTP;
	private ToleranceParametrs primaryPhaseTP;
	private ToleranceParametrs periodicModuleTP;
	private ToleranceParametrs periodicPhaseTP;
	
	public MeasResult getNominal() {
		return this.nominal;
	}
	public ToleranceParametrs getPrimaryModuleTP() {
		return this.primaryModuleTP;
	}
	public ToleranceParametrs getPrimaryPhaseTP() {
		return this.primaryPhaseTP;
	}
	public ToleranceParametrs getPeriodicModuleTP() {
		return this.periodicModuleTP;
	}
	public ToleranceParametrs getPeriodicPhaseTP() {
		return this.periodicPhaseTP;
	}
	
	private String[] keyIndex = new String[]{
		"FREQ", "DOWN_MODULE", "MODULE", "UP_MODULE", "DOWN_PHASE", "PHASE", "UP_PHASE"
	};
	
	static {
		tableHeads = new ArrayList<String>();
		tableHeads.add("Частота, ГГц");
		tableHeads.add("Нижний допуск");
		tableHeads.add("Номинал");
		tableHeads.add("Верхний допуск");
		tableHeads.add("Нижний допуск");
		tableHeads.add("Номинал");
		tableHeads.add("Верхний допуск");
	}
		
	public NewElementStringGridFX(StringGridPosition position) {
		super(7, 10, position, tableHeads);
	}

	private void setAllValues(String sParam, TimeType anyTimeTipe) {
		//this.cells.get("СТОЛБЕЦ").get("СТРОКА")
		int freqCount = this.nominal.freqs.size();
		if (freqCount > this.getRowCount()) {
			while(freqCount != this.getRowCount()) {
				this.addRow();
			}
		} else if (freqCount < this.getRowCount()) {
			while(freqCount != this.getRowCount()) {
				this.deleteRow(this.getRowCount());
			}
		}
		
		ToleranceParametrs currentModuleTP = null;
		ToleranceParametrs currentPhaseTP = null;
		if (anyTimeTipe == TimeType.PRIMARY) {
			currentModuleTP = this.primaryModuleTP;
			currentPhaseTP = this.primaryPhaseTP;
		} else {
			currentModuleTP = this.periodicModuleTP;
			currentPhaseTP = this.periodicPhaseTP;
		}
		
		for (int i = 0; i < freqCount; i++) {
			Double curFreq = this.nominal.freqs.get(i);
			//Из номинала			
			this.cells.get(0).get(i).setText(curFreq.toString()); //частоты
			this.cells.get(2).get(i).setText(this.nominal.values.get(MeasUnitPart.MODULE + "_" + sParam).get(curFreq).toString());
			this.cells.get(5).get(i).setText(this.nominal.values.get(MeasUnitPart.PHASE + "_" + sParam).get(curFreq).toString());
			//			
			this.cells.get(1).get(i).setText(currentModuleTP.values.get("DOWN_" + MeasUnitPart.MODULE + "_" + sParam).get(curFreq).toString());			
			this.cells.get(3).get(i).setText(currentModuleTP.values.get("UP_" + MeasUnitPart.PHASE + "_" + sParam).get(curFreq).toString());
			this.cells.get(4).get(i).setText(currentPhaseTP.values.get("DOWN_" + MeasUnitPart.MODULE + "_" + sParam).get(curFreq).toString());			
			this.cells.get(6).get(i).setText(currentPhaseTP.values.get("UP_" + MeasUnitPart.PHASE + "_" + sParam).get(curFreq).toString());
		}
	}
	
}