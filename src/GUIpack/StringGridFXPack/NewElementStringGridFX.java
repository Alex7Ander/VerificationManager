package GUIpack.StringGridFXPack;

import java.util.ArrayList;
import java.util.HashMap;

import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import ToleranceParamPack.ParametrsPack.TimeType;
import javafx.collections.ObservableList;

public class NewElementStringGridFX extends StringGridFX {

	private TimeType myTimeType;
	private S_Parametr currentS;
	public TimeType getMyTimeType() {
		return myTimeType;
	}
	public S_Parametr getCurrentS() {
		return currentS;
	}

	public HashMap<String, ArrayList<Double>> values;
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
	public NewElementStringGridFX(StringGridPosition position, TimeType timeType) {
		super(7, 10, position, tableHeads);
		myTimeType = timeType;
		currentS = S_Parametr.S11;
		values = new HashMap<String, ArrayList<Double>>();
		for (int i = 0; i < S_Parametr.values().length; i++) {
			for (int j = 0; j < MeasUnitPart.values().length; j++) {
				String key = "DOWN_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				values.put(key, new ArrayList<Double>());
				key = MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				values.put(key, new ArrayList<Double>());
				key = "UP_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				values.put(key, new ArrayList<Double>());
			}
		}		
		for (ObservableList<CellTextField> line: this.cells) {
			for (CellTextField currentCell : line) {
				currentCell.textProperty().addListener((observableValue, oldText, newText) -> {
				     if (!newText.isEmpty()) {
				    	 try {
				    		 @SuppressWarnings("unused")
							 Double value = Double.parseDouble(newText);
				             currentCell.setText(newText);
				         } 
				    	 catch (NumberFormatException nfExp) {
				        	 currentCell.setText(oldText);
				         }
				     }
				 });
			}//end for (TextField currentCell : line)
		} //end for (ObservableList<TextField> line: this.cells)
	}
	
	public void changeSParametr(S_Parametr parametr) {
		//Teke data
		this.getColumnToDouble(1, values.get("DOWN_" + MeasUnitPart.MODULE + "_" + currentS));
		this.getColumnToDouble(2, values.get(MeasUnitPart.MODULE + "_" + currentS));
		this.getColumnToDouble(3, values.get("UP_" + MeasUnitPart.MODULE + "_" + currentS));		
		this.getColumnToDouble(4, values.get("DOWN_" + MeasUnitPart.PHASE + "_" + currentS));
		this.getColumnToDouble(5, values.get(MeasUnitPart.PHASE + "_" + currentS));
		this.getColumnToDouble(6, values.get("UP_" + MeasUnitPart.PHASE + "_" + currentS));
		//Set new values
		this.setColumnFromDouble(1, values.get("DOWN_" + MeasUnitPart.MODULE + "_" + parametr));
		this.setColumnFromDouble(2, values.get(MeasUnitPart.MODULE + "_" + parametr));
		this.setColumnFromDouble(3, values.get("UP_" + MeasUnitPart.MODULE + "_" + parametr));		
		this.setColumnFromDouble(4, values.get("DOWN_" + MeasUnitPart.PHASE + "_" + parametr));
		this.setColumnFromDouble(5, values.get(MeasUnitPart.PHASE + "_" + parametr));
		this.setColumnFromDouble(6, values.get("UP_" + MeasUnitPart.PHASE + "_" + parametr));		
		currentS = parametr;
	}
	
	public HashMap<Double, Double> getColumnByKey(String key){		
		return null; //values.get(key);		
	}

	public void setParams(TimeType type, S_Parametr Sxx) {
		// 1. Сохранить текущее состояние

		// 2. Записать новое состояние
		
		/*
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
			this.setColumnFromDouble(0, columnValues);*/
	}
	
}