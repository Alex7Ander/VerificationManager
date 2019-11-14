package GUIpack.StringGridFXPack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import DevicePack.Element;
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

	public Map<String, ArrayList<String>> values;
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
		values = new HashMap<String, ArrayList<String>>();
		values.put("FREQS", new ArrayList<String>());
		for (int i = 0; i < S_Parametr.values().length; i++) {
			for (int j = 0; j < MeasUnitPart.values().length; j++) {
				String key = "DOWN_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				values.put(key, new ArrayList<String>());
				key = MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				values.put(key, new ArrayList<String>());
				key = "UP_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				values.put(key, new ArrayList<String>());
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
	
	public void saveInputedValues() {
		this.getColumn(0, values.get("FREQS"));
		this.getColumn(1, values.get("DOWN_" + MeasUnitPart.MODULE + "_" + currentS));
		this.getColumn(2, values.get(MeasUnitPart.MODULE + "_" + currentS));
		this.getColumn(3, values.get("UP_" + MeasUnitPart.MODULE + "_" + currentS));		
		this.getColumn(4, values.get("DOWN_" + MeasUnitPart.PHASE + "_" + currentS));
		this.getColumn(5, values.get(MeasUnitPart.PHASE + "_" + currentS));
		this.getColumn(6, values.get("UP_" + MeasUnitPart.PHASE + "_" + currentS));
	}
	public void showParametr(S_Parametr parametr) {
		this.setColumn(0, values.get("FREQS"));
		this.setColumn(1, values.get("DOWN_" + MeasUnitPart.MODULE + "_" + parametr));
		this.setColumn(2, values.get(MeasUnitPart.MODULE + "_" + parametr));
		this.setColumn(3, values.get("UP_" + MeasUnitPart.MODULE + "_" + parametr));		
		this.setColumn(4, values.get("DOWN_" + MeasUnitPart.PHASE + "_" + parametr));
		this.setColumn(5, values.get(MeasUnitPart.PHASE + "_" + parametr));
		this.setColumn(6, values.get("UP_" + MeasUnitPart.PHASE + "_" + parametr));	
	}
	public void changeSParametr(S_Parametr parametr) {		
		saveInputedValues(); 	//Get values		
		this.clear(); 			//Clear table		
		showParametr(parametr); //show other values
		currentS = parametr;
	}
	
	public void setValuesFromElement(Element element) {		
		for (Double freq : element.getNominal().freqs) {
			values.get("FREQS").add(freq.toString());
		}		
		for (int i = 0; i < element.getSParamsCout(); i++) {
			for (int j = 0; j < MeasUnitPart.values().length; j++) {
				String currentKeys[] = new String[]{"DOWN_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i], MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i], "UP_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i]};
				for (String key: currentKeys){
					values.get(key).clear();
					for (Double freq : element.getNominal().freqs) {
						Double val = null;
						if (key.contains("DOWN") || key.contains("UP"))
							val = element.getToleranceParametrs(this.myTimeType, MeasUnitPart.values()[j]).values.get(key).get(freq);
						else
							val = element.getNominal().values.get(key).get(freq);
						values.get(key).add(val.toString());
					}
				}
			}
		}		
	}
	
	
	public ArrayList<Double> getFreqs(){
		ArrayList<Double> freqs = new ArrayList<Double>();
		for (String strFreq : this.values.get("FREQS")) {
			try {
				double freq = Double.parseDouble(strFreq);
				freqs.add(freq);
			} catch (NumberFormatException nfExp) {
				
			}
		}
		return freqs;
	}
	public Map<Double, Double> getParametr(String paramName){
		LinkedHashMap<Double, Double> parametr = new LinkedHashMap<Double, Double>();
		for (int i=0; i < values.get("FREQS").size(); i++) {
			try {
				double freq = Double.parseDouble(values.get("FREQS").get(i));
				double currentValue = Double.parseDouble(values.get(paramName).get(i));
				parametr.put(freq, currentValue);
			} catch (NumberFormatException nfExp) {
				//
			}			
		}
		return parametr;
	}
	
	public boolean isFull(int countOfControlledParams) {
		int expectedCount = getRowCount();
		String prefix[] = new String[] {"DOWN_", "", "UP_"};
		for (int i = 0; i < countOfControlledParams; i++) {
			for (int j = 0; j < MeasUnitPart.values().length; j++) {
				for (String pref: prefix) {
					String key = pref + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
					int currentCount = values.get(key).size();
					if (expectedCount != currentCount) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void setRandomValues() {
		Random rand = new Random();
		for (String key : values.keySet()) {
			if (!key.equals("FREQS")) {
				for (int i = 0; i < getRowCount(); i++) {
					Double value = rand.nextDouble();
					this.values.get(key).add(value.toString());
				}
			}
		}
	}
	
	
}