package NewDevicePack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import AboutMessageForm.AboutMessageWindow;
import AddNewDeviceNameForm.AddNewDeviceNameWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Device;
import DevicePack.Element;
import DevicePack.SavingException;
import FileManagePack.FileManager;
import NewElementPack.NewElementController;
import NewElementPack.NewElementWindow;
import SearchDevicePack.SearchDeviceWindow;
import VerificationPack.MeasResult;
import VerificationPack.VSWR_Result;
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
import javafx.stage.Stage;


public class NewDeviceController  {

	@FXML
	private Button createElmentsBtn;
	@FXML
	private Button saveBtn;
	@FXML
	private Button addNameBtn;
	
	@FXML
	private ComboBox  namesComboBox;
	@FXML
	private TextField typeTextField;
	@FXML
	private TextField serialNumberTextField;
	@FXML
	private TextField ownerTextField;
	@FXML
	private TextField gosNumberTextField;
	@FXML
	private TextField countOfElementsTextField;
	
	@FXML
	private VBox elementsButtonBox;
	
	public ArrayList<NewElementWindow> elementsWindow;
	public ArrayList<Button> elementsButton;
	
	private ObservableList<String> listOfNames;
	int countOfElements;

	@FXML
	private void initialize(){
		listOfNames = FXCollections.observableArrayList();
		elementsButton = new ArrayList<Button>();
		elementsWindow = new ArrayList<NewElementWindow>();
		setItemsOfNames();
	}
	
	@FXML
	public void createElementsBtnClick(ActionEvent event) {
		for (int i=0; i<elementsButton.size(); i++) {
			elementsButtonBox.getChildren().remove(elementsButton.get(i));
		}
		elementsButton.clear();
		elementsWindow.clear();
		try {			
			countOfElements = Integer.parseInt(countOfElementsTextField.getText());
			for (int i=0; i<countOfElements; i++) {
				final int index = i;
				String item = Integer.toString(i+1);		
				
				NewElementWindow elementWin = new NewElementWindow();
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
	public void saveBtnClick(ActionEvent event) throws IOException {
		
		String tName = null;
		try{
			tName = this.namesComboBox.getValue().toString();		
		}
		catch(Exception exp) {
			AboutMessageWindow errorMessage = new AboutMessageWindow("Ошибка", "Не указано наименование типа создаваемого СИ!");
			errorMessage.show();
		}
		
		if (tName != null) {
			String tType = this.typeTextField.getText();
			String tSerN = this.serialNumberTextField.getText();
			String tOwner = this.ownerTextField.getText();
			String tGosN = this.gosNumberTextField.getText();
			Device newDevice = null;
			
			if (tName.length()==0 || tType.length()==0 || tSerN.length()==0){
				AboutMessageWindow errorMessage = new AboutMessageWindow("Ошибка", "Заполнены не все обходимые поля");
				errorMessage.show();
			}
			else{
				if (tOwner.length() == 0) tOwner = "-";
				if (tGosN.length() == 0) tGosN = "-";
				newDevice = new Device(tName, tType, tSerN, tOwner, tGosN);
				if (!(newDevice.isExist())) {
					//Создаем элементы
					for (int i=0; i<elementsWindow.size(); i++) {
						NewElementWindow elementWin = elementsWindow.get(i);
						NewElementController ctrl = (NewElementController) elementWin.getControllerClass();						
						Element el = new Element(ctrl, newDevice);
						newDevice.addElement(el);
					}
					try{
						DataBaseManager.getDB().BeginTransaction();
						newDevice.saveInDB();
						DataBaseManager.getDB().Commit();
						AboutMessageWindow sucsessMessage = new AboutMessageWindow("Успешно", "Успешное сохранение");
						sucsessMessage.show();
						Stage stage = (Stage) saveBtn.getScene().getWindow();
						stage.close();
					}
					catch(SQLException sqlExp){
						DataBaseManager.getDB().RollBack();
						AboutMessageWindow errorMessage = new AboutMessageWindow("Ошибка", "Ошибка: " + sqlExp.getMessage());
						errorMessage.show();
					}
				}
				else {
					AboutMessageWindow errorMessage = new AboutMessageWindow("Ошибка", "Прибор данного типа с таким серийным номером уже существует!");
					errorMessage.show();
				}
			}
		}
	}
	
	@FXML
	public void addNameBtnClick(ActionEvent event) {
		
		try {
			//SearchDeviceWindow.getSearchDeviceWindow(verificatedDevice, this).show();
			AddNewDeviceNameWindow.getNewDeviceWindow(this).show();
		}
		catch(IOException ioExp) {
			//
		}
		
	}
	
	public void setItemsOfNames() {
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

}
