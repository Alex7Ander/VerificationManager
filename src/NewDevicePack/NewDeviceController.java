package NewDevicePack;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import AboutMessageForm.AboutMessageWindow;
import AddNewDeviceNameForm.AddNewDeviceNameWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Device;
import DevicePack.Element;
import Exceptions.SavingException;
import FileManagePack.FileManager;
import NewElementPack.NewElementController;
import NewElementPack.NewElementWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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
	private ComboBox<String> namesComboBox;
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
	
	//Сохраняемыый прибор
	private Device newDevice;

	public List<NewElementWindow> elementsWindow;
	public List<Button> elementsButton;
	
	private ObservableList<String> listOfNames;
	int countOfElements;

	@FXML
	private void initialize(){
		listOfNames = FXCollections.observableArrayList();
		elementsButton = new ArrayList<Button>();
		elementsWindow = new ArrayList<NewElementWindow>();
		countOfElements = 0;
		setItemsOfNames();
	}
	
	@FXML
	public void createElementsBtnClick(ActionEvent event) throws IOException {
		int newCountOfElements = 0;
		try {			
			newCountOfElements = Integer.parseInt(countOfElementsTextField.getText());
		}
		catch(NumberFormatException nfExp) {
			AboutMessageWindow.createWindow("Ошибка", "Введенное Вами число элементов\nне является действительным числом!").show();
			return;
		}
		
		if(newCountOfElements == this.countOfElements) {
			return;
		}
		else if(newCountOfElements > this.countOfElements) {
			while(this.countOfElements != newCountOfElements) {
				final int index = (countOfElements);
				String item = Integer.toString(countOfElements + 1);		
				//Cоздаем окно, которе будет принимать информацию для элемента
				NewElementWindow elementWin;
				elementWin = new NewElementWindow();
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
				elementsButtonBox.getChildren().add(btn);
				++countOfElements;
			}
		}
		else if(newCountOfElements < this.countOfElements) {
			while(newCountOfElements != this.countOfElements) {
				Button btn = elementsButton.get(countOfElements - 1);
				elementsButton.remove(btn);
				elementsButtonBox.getChildren().remove(btn);
				elementsWindow.remove(countOfElements - 1);
				--countOfElements;
			}

		}
	}	
	
	@FXML
	public void saveBtnClick(ActionEvent event) {		
		String tName = null;
		try{
			tName = this.namesComboBox.getValue().toString();		
		}
		catch(NullPointerException npExp) {
			AboutMessageWindow.createWindow("Ошибка", "Не указано наименование типа создаваемого СИ!").show();
			return;
		}
		
		String tType = this.typeTextField.getText();
		String tSerN = this.serialNumberTextField.getText();
		String tOwner = this.ownerTextField.getText();
		String tGosN = this.gosNumberTextField.getText();
			
		//Если критически важные для сохранения поля пусты, то прерываем процедуру
		if (tName.length()==0 || tType.length()==0 || tSerN.length()==0){
			AboutMessageWindow.createWindow("Ошибка", "Заполнены не все обходимые поля").show();
			return;
		}
		if (tOwner.length() == 0) tOwner = "-"; //Не критически важные заполняем условно прочерком
		if (tGosN.length() == 0) tGosN = "-";
			
		//Создали новое устройство
		newDevice = new Device(tName, tType, tSerN, tOwner, tGosN);			
		//Проверим, не существует ли аналогичное в БД
		try {
			if (newDevice.isExist()) {
				AboutMessageWindow.createWindow("Ошибка", "Прибор данного типа с аналогичным\n"
						+ "серийным номером зарегистрированн\nв базе данных!").show();
				return;
			}
		}
		catch (SQLException sqlExp) {
			AboutMessageWindow.createWindow("Ошибка", "Не удалось проверить наличие Си в БД.\nВозможно, база данных отсутствует\nили повреждена").show();
			return;
		}
		
		//Создаем элементы (нагрузки) для данного прибора		
		for (int i = 0; i < this.elementsWindow.size(); i++) {			
			NewElementController ctrl = (NewElementController)this.elementsWindow.get(i).getControllerClass();
			Element elm = new Element(ctrl); 
			newDevice.addElement(elm);	
		}			
		//проверяем серийный номера у нагрузок
		for (int i=0; i<newDevice.includedElements.size(); i++) {
			Element elm = newDevice.includedElements.get(i);
			for(int j=i+1; j<newDevice.includedElements.size(); j++) {
				Element otherElm = newDevice.includedElements.get(j);
				if(elm.getSerialNumber().equals(otherElm.getSerialNumber())) {
					AboutMessageWindow.createWindow("Ошибка", "У 2-х или более составных элементов\n"
																+ "одинаковые серийный номера.\n"
																+ "Проверьте указанные Вами данные\n"
																+ "и повторите попытку.").show();
					return;
				}
			}
		}
		
		//и наконец, пробуем сохранить все в БД
		try{
			DataBaseManager.getDB().BeginTransaction();
			newDevice.saveInDB();
			//------------------
			DataBaseManager.getDB().Commit();
			AboutMessageWindow.createWindow("Успешно", "Успешное сохранение").show();
			Stage stage = (Stage) saveBtn.getScene().getWindow();
			stage.close();
			NewDeviceWindow.deleteNewDeviceWindow();
		}
		catch(SQLException sqlExp){
			System.err.println(sqlExp.getCause());
			DataBaseManager.getDB().RollBack();
			AboutMessageWindow.createWindow("Ошибка", "Ошибка: " + sqlExp.getMessage()).show();
		}
		catch(NumberFormatException nfExp) {
			DataBaseManager.getDB().RollBack();
			AboutMessageWindow.createWindow("Ошибка", "Ошибка при преобразовании чисел\nПроверьте корректность введенных Вами данных.").show();
		}
		catch(SavingException noExp) {
			DataBaseManager.getDB().RollBack();
			AboutMessageWindow.createWindow("Ошибка", "Ошибка: " + noExp.getMessage()).show();
		}
	}
	
	@FXML
	public void addNameBtnClick(ActionEvent event) {		
		try {
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