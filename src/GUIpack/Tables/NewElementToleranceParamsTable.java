package GUIpack.Tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import DevicePack.Element;
import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import ToleranceParamPack.ParametrsPack.TimeType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;

public class NewElementToleranceParamsTable extends VisualTable {

	private static ArrayList<String> localTableHeads;
	private final int colCount = 5;
	
	private TimeType myTimeType;
	private S_Parametr currentS;
	
	//проверить, используется ли этот метод вообще где-либо
	public TimeType getMyTimeType() {
		return myTimeType;
	}
	
	public S_Parametr getCurrentS() {
		return currentS;
	}

	public Map<String, ArrayList<String>> values;
	
	public NewElementToleranceParamsTable(Pane pane, TimeType timeType) {
		super(localTableHeads, pane);
		myTimeType = timeType;
		currentS = S_Parametr.S11;
		
		
		values = new HashMap<String, ArrayList<String>>();
		values.put("FREQS", new ArrayList<String>());
		for (int i = 0; i < S_Parametr.values().length; i++) {
			for (int j = 0; j < MeasUnitPart.values().length; j++) {
				String key = "DOWN_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				values.put(key, new ArrayList<String>());
				key = "UP_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				values.put(key, new ArrayList<String>());
			}
		}
		
		table.setPlaceholder(new Label("Начниет заполнение создав частотную сетку для измерений"));
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
					newValue = newValue.replace(",",".");
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
		localTableHeads.add("F, ГГц");
		localTableHeads.add("Нижний допуск модуля\nКСВН (1 порт)");
		localTableHeads.add("Верхний допуск модуля\nКСВН (1 порт)");
		localTableHeads.add("Нижний допуск фазы\nКСВН (1 порт)");
		localTableHeads.add("Верхний допуск фазы\nКСВН (1 порт)");
	}
	
		
	
	public void setValuesFromElement(Element element) {
		for (Double freq : element.getNominal().freqs) {
			values.get("FREQS").add(freq.toString());
		}		
		for (int i = 0; i < element.getSParamsCout(); i++) {
			for (int j = 0; j < MeasUnitPart.values().length; j++) {
				String currentKeys[] = new String[]{"DOWN_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i], "UP_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i]};
				for (String key: currentKeys){
					values.get(key).clear();
					for (Double freq : element.getNominal().freqs) {
						Double val = element.getToleranceParametrs(this.myTimeType, MeasUnitPart.values()[j]).values.get(key).get(freq);
						values.get(key).add(val.toString());
					}
				}
			}
		}	
	}
	
	public void changeSParametr(S_Parametr parametr) {
		saveInputedValues(); 	//Get values			
		showParametr(parametr); //show other values
		currentS = parametr;				
	}
	public void saveInputedValues() {
		this.getColumn(0, values.get("FREQS"));
		this.getColumn(1, values.get("DOWN_" + MeasUnitPart.MODULE + "_" + currentS));
		this.getColumn(2, values.get("UP_" + MeasUnitPart.MODULE + "_" + currentS));		
		this.getColumn(3, values.get("DOWN_" + MeasUnitPart.PHASE + "_" + currentS));
		this.getColumn(4, values.get("UP_" + MeasUnitPart.PHASE + "_" + currentS));
	}
	public void showParametr(S_Parametr parametr) {
		this.setColumn(0, values.get("FREQS"));
		this.setColumn(1, values.get("DOWN_" + MeasUnitPart.MODULE + "_" + parametr));
		this.setColumn(2, values.get("UP_" + MeasUnitPart.MODULE + "_" + parametr));		
		this.setColumn(3, values.get("DOWN_" + MeasUnitPart.PHASE + "_" + parametr));
		this.setColumn(4, values.get("UP_" + MeasUnitPart.PHASE + "_" + parametr));	
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
		String prefix[] = new String[] {"DOWN_", "UP_"};
		for (int i = 0; i < countOfControlledParams; i++) {
			for (int j = 0; j < MeasUnitPart.values().length; j++) {
				for (String pref: prefix) {
					String key = pref + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
					int currentCount = values.get(key).size();
					for (int k = 0; k < currentCount; k++) {
						if (values.get(key).get(k).equals("")) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
}
