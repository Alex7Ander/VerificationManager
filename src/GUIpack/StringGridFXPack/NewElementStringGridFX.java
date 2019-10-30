package GUIpack.StringGridFXPack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import ToleranceParamPack.ParametrsPack.TimeType;
import _tempHelpers.Adapter;
import javafx.collections.ObservableList;

public class NewElementStringGridFX extends StringGridFX {
	
	//public HashMap<String, HashMap<Double, Double>> values;
	private TimeType currentlyIndicatedTimeType;	
	public HashMap<String, ArrayList<Double>> values;
	
	private String[] columnNames = new String[]{"FREQ", "PRIMARY_DOWN_MODULE", "MODULE", "PRIMARY_UP_MODULE",   "PRIMARY_DOWN_PHASE", "PHASE", "PRIMARY_UP_PHASE",		   
			"PERIODIC_DOWN_MODULE", "PERIODIC_UP_MODULE", "PERIODIC_DOWN_PHASE", "PERIODIC_UP_PHASE"};
	
	public NewElementStringGridFX(StringGridPosition position) {
		super(7, 10, position, tableHeads);
		values = new HashMap<String, ArrayList<Double>>();
		for (int i = 0; i < S_Parametr.values().length; i++) {
			for (int j = 0; j < MeasUnitPart.values().length; j++) {
				for (int k = 0; k < TimeType.values().length; k++) {
					String key = TimeType.values()[k] + "_DOWN_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
					values.put(key, new ArrayList<Double>());
					key = MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
					values.put(key, new ArrayList<Double>());
					key = TimeType.values()[k] + "_UP_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
					values.put(key, new ArrayList<Double>());
				}
			}
		}
		
		for (ObservableList<CellTextField> line: this.cells) {
			for (CellTextField currentCell : line) {
				String numberMatcher = "";  //"^-?\\d+$"; 		// ^[+-]?\\d+\\.\\d+
				currentCell.textProperty().addListener((observableValue, oldText, newText) -> {
				     if (!newText.isEmpty()) {
				         if (!newText.matches(numberMatcher)) {
				        	 currentCell.setText(oldText);
				         } else {
				             try {
				                 //тут можете парсить строку как захотите
				                 Double value = Double.parseDouble(newText);
				                 int row = currentCell.getRowIndex();
				                 int col = currentCell.getColIndex();
				                 //values.get(this.currentlyIndicatedTimeType + "_" + columnNames[col]).remove(key);
				                 currentCell.setText(newText);
				             } catch (NumberFormatException nfExp) {
				            	 currentCell.setText(oldText);
				             }
				         }
				     }
				 });
			}//end for (TextField currentCell : line)
		} //end for (ObservableList<TextField> line: this.cells)
	}
	
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
	
	public HashMap<Double, Double> getColumnByKey(String key){
		return null; //values.get(key);		
	}

	public void setParams(TimeType type, S_Parametr Sxx) {/*
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