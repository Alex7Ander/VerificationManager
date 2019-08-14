package NewDeviceForm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import AboutMessageForm.AboutMessageWindow;
import DevicePack.Device;
import DevicePack.SavingException;
import FileManagePack.FileManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class NewDeviceController {

	public Button createElmentsBtn;
	public Button saveBtn;
	public Button addNameBtn;
	
	public ComboBox  namesComboBox;
	public TextField typeTextField;
	public TextField serialNumberTextField;
	public TextField ownerTextField;
	public TextField gosNumberTextField;
	public TextField countOfElementsTextField;
	
	public VBox elementsButtonBox;
	
	public ArrayList<ElementWindow> elementsWindow;
	public ArrayList<Button> elementsButton;
	
	int countOfElements;

	@FXML
	private void initialize(){
		ObservableList<String> listOfNames = FXCollections.observableArrayList();
		try {
			String absPath = new File(".").getAbsolutePath();
			FileManager.LinesToItems(absPath + "\\files\\sitypes.txt", listOfNames);
		}
		catch(Exception exp) {
			System.out.println("Error: "+ exp.getMessage());
			listOfNames.clear();
			listOfNames.add("Рабочий эталон ККПиО");
			listOfNames.add("Набор нагрузок волноводных");
			listOfNames.add("Нагрузки волноводные согласованные");
			listOfNames.add("Комплект поверочный");
			listOfNames.add("Калибровочный и поверочный комплекты мер");
			listOfNames.add("Нагрузки волноводные КЗ подвижные");
		}
		this.namesComboBox.setItems(listOfNames);
	}
	
	@FXML
	public void createElementsBtnClick(ActionEvent event) {
		elementsButton = new ArrayList<Button>();
		elementsWindow = new ArrayList<ElementWindow>();
		elementsButton.clear();
		try {			
			countOfElements = Integer.parseInt(countOfElementsTextField.getText());
			for (int i=0; i<countOfElements; i++) {
				final int index = i;
				String item = Integer.toString(i+1);		
				
				ElementWindow elementWin = new ElementWindow();
				elementWin.setTitle(item);
				elementsWindow.add(elementWin);	
				
				Button btn = new Button(item);
				btn.setPrefWidth(600);
				btn.setPrefHeight(30);				
				btn.setOnAction(new EventHandler<ActionEvent>(){
						@Override
						public void handle(ActionEvent event) {
							elementsWindow.get(index).show();
						}
				});
				elementsButton.add(btn);
			}
			elementsButtonBox.getChildren().addAll(elementsButton);
		}
		catch(Exception exp) {
			System.out.println(exp.getMessage());
		}
	}	
	
	@FXML
	public void saveBtnClick(ActionEvent event) {
		String tName = this.namesComboBox.getValue().toString();		
		String tType = this.typeTextField.getText();
		String tSerN = this.serialNumberTextField.getText();
		String tOwner = this.ownerTextField.getText();
		String tGosN = this.gosNumberTextField.getText();
		
		Device newDevice = new Device(tName, tType, tSerN, tOwner, tGosN);
		if (tName.length()==0 || tType.length()==0 || tSerN.length()==0){
			try {
				AboutMessageWindow errorMessage = new AboutMessageWindow("Ошибка", "Заполнены не все обходимые поля");
				errorMessage.show();
			}
			catch(IOException exp) {
				System.out.println("Ошибка вывода сообщения о незаполнености полей: " + exp.getMessage());
			}
		}
		else{
			try{
				if (tOwner.length() == 0) tOwner = "-";
				if (tGosN.length() == 0) tGosN = "-";
				newDevice.saveInDB();
			}
			catch(SavingException sExp){
				try {
					AboutMessageWindow errorMessage = new AboutMessageWindow("Ошибка", sExp.getDeviceSatausMsg());
					errorMessage.show();
				}
				catch(IOException exp) {
					System.out.println("Ошибка вывода сообщения о наличии такого СИ: " + exp.getMessage());
				}
			}
		}
	}
	
	@FXML
	public void addNameBtnClick(ActionEvent event) {
		
	}
	
}
