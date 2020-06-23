package GUIpack.Tables;

import java.util.ArrayList;
import java.util.Map;

import DevicePack.Element;
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
	
	public NewElementToleranceParamsTable(Pane pane) {
		super(localTableHeads, pane);
		currentS = S_Parametr.S11;
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
	
	
	
	
	
	
	
	public void setValuesFromElement(Element currentElement) {
		
	}

	public void showParametr(S_Parametr currentS) {
		// TODO Auto-generated method stub
		
	}
	
	public void changeSParametr(S_Parametr currentS) {
		
		
		
		
	}

	public Map<Double, Double> getParametr(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isFull(int countOfControlledParams) {
		// TODO Auto-generated method stub
		return true;
	}
	
}
