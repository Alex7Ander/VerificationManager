package GUIpack.Tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import ToleranceParamPack.ParametrsPack.TimeType;
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
				String key = "DOWN_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				values.put(key, new ArrayList<String>());
				key = MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				values.put(key, new ArrayList<String>());
				key = "UP_" + MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				values.put(key, new ArrayList<String>());
			}
		}		
		
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
		localTableHeads.add("F, ГГц");	//0
		localTableHeads.add("Модуль\nКСВН (1 порт)");
		localTableHeads.add("Фаза\nКСВН (1 порт)");
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
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isFull(int countOfControlledParams) {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	public void changeSParametr(S_Parametr currentS) {
		
		
		
		this.setHead(1, "");
	}
	
}
