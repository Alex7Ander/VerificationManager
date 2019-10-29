package GUIpack.StringGridFXPack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import ToleranceParamPack.ParametrsPack.TimeType;
import _tempHelpers.Adapter;

public class NewElementStringGridFX extends StringGridFX {
	
	private static ArrayList<String> tableHeads;	
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
	
	public HashMap<String, HashMap<Double, Double>> values;	
	
	public NewElementStringGridFX(StringGridPosition position) {
		super(7, 10, position, tableHeads);
		values = new HashMap<String, HashMap<Double, Double>>();
		for (int i = 0; i < S_Parametr.values().length; i++) {
			for (int j = 0; j < MeasUnitPart.values().length; j++) {
				for (int k = 0; k < TimeType.values().length; k++) {
					String key = TimeType.values()[k] + "_" +
								 MeasUnitPart.values()[j] + "_" +
								 S_Parametr.values()[i];
					values.put(key, new HashMap<Double, Double>());
				}
			}
		}
	}
	
	public HashMap<Double, Double> getColumnByKey(String key){
		return values.get(key);		
	}

	public void setParams(TimeType type, S_Parametr Sxx) {
			String key = type + "_" + "UP" + "_" + MeasUnitPart.MODULE + "_" + Sxx;
			ArrayList<Double> columnValues = Adapter.HashMapToArrayList(this.values.get(key));
			this.setColumnFromDouble(1, columnValues);
			
			key = type + "_" + MeasUnitPart.MODULE + "_" + Sxx;
			columnValues = Adapter.HashMapToArrayList(this.values.get(key));
			this.setColumnFromDouble(2, columnValues);
			
			key = type + "_" + "DOWN" + "_" + MeasUnitPart.MODULE + "_" + Sxx;
			columnValues = Adapter.HashMapToArrayList(this.values.get(key));
			this.setColumnFromDouble(3, columnValues);
			
			key = type + "_" + "UP" + "_" + MeasUnitPart.PHASE + "_" + Sxx;
			columnValues = Adapter.HashMapToArrayList(this.values.get(key));
			this.setColumnFromDouble(4, columnValues);
			
			key = type + "_" + MeasUnitPart.PHASE + "_" + Sxx;
			columnValues = Adapter.HashMapToArrayList(this.values.get(key));
			this.setColumnFromDouble(5, columnValues);
			
			key = type + "_" + "DOWN" + "_" + MeasUnitPart.PHASE + "_" + Sxx;
			columnValues = Adapter.HashMapToArrayList(this.values.get(key));
			this.setColumnFromDouble(6, columnValues);
			
			columnValues.clear();
			HashSet<Double> fr = (HashSet<Double>)this.values.get(key).keySet();
			for (Double d : fr) {
				columnValues.add(d);
			}
			this.setColumnFromDouble(0, columnValues);
	}

}