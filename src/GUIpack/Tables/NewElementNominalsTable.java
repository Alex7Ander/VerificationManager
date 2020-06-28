package GUIpack.Tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import DevicePack.Element;
import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import ToleranceParamPack.ParametrsPack.TimeType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;


public class NewElementNominalsTable extends VisualTable {

	private static ArrayList<String> localTableHeads;
	private final int colCount = 3;
	
	private TimeType myTimeType;
	private S_Parametr currentS;
	public TimeType getMyTimeType() {
		return myTimeType;
	}
	public S_Parametr getCurrentS() {
		return currentS;
	}

	public Map<String, ArrayList<String>> values;
	
	public NewElementNominalsTable(Pane pane) {
		super(localTableHeads, pane);
		currentS = S_Parametr.S11;
		
		values = new HashMap<String, ArrayList<String>>();
		values.put("FREQS", new ArrayList<String>());
		for (int i = 0; i < S_Parametr.values().length; i++) {
			for (int j = 0; j < MeasUnitPart.values().length; j++) {
				String key = MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				values.put(key, new ArrayList<String>());
			}
		}	
		
		table.setPlaceholder(new Label("Таблица пуста"));
		table.setPrefWidth(pane.getPrefWidth());
		table.setPrefHeight(pane.getPrefHeight());	
		table.getColumns().get(0).setPrefWidth(57);
		double colWidth = (table.getPrefWidth() - 57)/(colCount - 1);
		for (int i = 1; i < table.getColumns().size(); i++) {
			table.getColumns().get(i).setPrefWidth(colWidth);
		}
		
		//Редактирование ячеек
		for (int i = 0; i < table.getColumns().size(); i++) {
			@SuppressWarnings("unchecked")
			TableColumn<Line, String> column = (TableColumn<Line, String>) table.getColumns().get(i);
			column.setCellFactory(TextFieldTableCell.<Line>forTableColumn());
			column.setOnEditCommit(event->{
				if(event.getNewValue() != null) {
					String newValue = event.getNewValue();
					newValue = newValue.replace(",", ".");
					try {
			    		 @SuppressWarnings("unused")
						 Double value = Double.parseDouble(newValue);
			         } 
			    	 catch (NumberFormatException nfExp) {
			    		 newValue = "-";
			         }
					int colIndex = event.getTablePosition().getColumn();
					int rowIndex = event.getTablePosition().getRow();
					this.lines.get(rowIndex).values.remove(colIndex);
					this.lines.get(rowIndex).values.put(colIndex, newValue);
					
				}
				table.refresh();
			});
		}

	}
	
	static {
		localTableHeads = new ArrayList<String>();
		localTableHeads.add("F, ГГц");	//0
		localTableHeads.add("Модуль\nКСВН (1 порт)");
		localTableHeads.add("Фаза\nКСВН (1 порт)");
	}	
	
	public void setValuesFromElement(Element element) {
		for (Double freq : element.getNominal().freqs) {
			values.get("FREQS").add(freq.toString());
		}		
		for (int i = 0; i < element.getSParamsCout(); i++) {
			for (int j = 0; j < MeasUnitPart.values().length; j++) {
				String currentKey = MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				values.get(currentKey).clear();
				for (Double freq : element.getNominal().freqs) {
					Double val = element.getNominal().values.get(currentKey).get(freq);
					values.get(currentKey).add(val.toString());
				}
			}
		}	
	}
	
	public List<Double> getFreqs() {
		List<Double> freqs = new ArrayList<>();
		for (String strFreq : this.values.get("FREQS")) {
			try {
				double freq = Double.parseDouble(strFreq);
				freqs.add(freq);
			} catch (NumberFormatException nfExp) {
				
			}
		}
		return freqs;
	}
	public Map<Double, Double> getParametr(String key) {
		Map<Double, Double> parametr = new LinkedHashMap<Double, Double>();
		for (int i=0; i < values.get("FREQS").size(); i++) {
			try {
				double freq = Double.parseDouble(values.get("FREQS").get(i));
				double currentValue = Double.parseDouble(values.get(key).get(i));
				parametr.put(freq, currentValue);
			} catch (NumberFormatException nfExp) {
				//
			}			
		}
		return parametr;
	}
	public boolean isFull(int countOfControlledParams) {
		for (int i = 0; i < countOfControlledParams; i++) {
			for (int j = 0; j < MeasUnitPart.values().length; j++) {
				String key = MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				int currentCount = values.get(key).size();
				for (int k = 0; k < currentCount; k++) {
					if (values.get(key).get(k).equals("")) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	
	public void changeSParametr(S_Parametr parametr) {
		saveInputedValues(); 	//Get values			
		showParametr(parametr); //show other values
		currentS = parametr;		
	}
	
	public void saveInputedValues() {
		this.getColumn(0, values.get("FREQS"));
		this.getColumn(1, values.get(MeasUnitPart.MODULE + "_" + currentS));
		this.getColumn(2, values.get(MeasUnitPart.PHASE + "_" + currentS));
	}
	public void showParametr(S_Parametr parametr) {
		this.setColumn(0, values.get("FREQS"));
		this.setColumn(1, values.get(MeasUnitPart.MODULE + "_" + parametr));
		this.setColumn(2, values.get(MeasUnitPart.PHASE + "_" + parametr));	
	}	
}
