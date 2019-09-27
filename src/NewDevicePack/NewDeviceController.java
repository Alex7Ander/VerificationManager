package NewDevicePack;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import AboutMessageForm.AboutMessageWindow;
import AddNewDeviceNameForm.AddNewDeviceNameWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Device;
import DevicePack.Element;
import Exceptions.NoOwnerException;
import Exceptions.SavingException;
import FileManagePack.FileManager;
import NewElementPack.NewElementWindow;
import YesNoDialogPack.YesNoWindow;
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
	private ComboBox<String>  namesComboBox;
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
	//Составные элементы данного прибора
	private ArrayList<Element> elements;
	
	public ArrayList<NewElementWindow> elementsWindow;
	public ArrayList<Button> elementsButton;
	
	private ObservableList<String> listOfNames;
	int countOfElements;

	@FXML
	private void initialize(){
		listOfNames = FXCollections.observableArrayList();
		elementsButton = new ArrayList<Button>();
		elementsWindow = new ArrayList<NewElementWindow>();
		elements = new ArrayList<Element>();
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
			for (int i = 0; i < countOfElements; i++) {
				//Создаем элемент и инициализируем его нулем
				Element elm = null;
				elements.add(elm);
								
				final int index = i;
				String item = Integer.toString(i+1);		
				//Cоздаем окно, которе будет принимать информацию для элемента
				//Передаем в это окно этот самый элемент
				NewElementWindow elementWin = new NewElementWindow(elm);
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
		catch(NullPointerException npExp) {
			AboutMessageWindow errorMessage = new AboutMessageWindow("Ошибка", "Не указано наименование типа создаваемого СИ!");
			errorMessage.show();
			return;
		}
		
		String tType = this.typeTextField.getText();
		String tSerN = this.serialNumberTextField.getText();
		String tOwner = this.ownerTextField.getText();
		String tGosN = this.gosNumberTextField.getText();
			
		//Если критически важные для сохранения поля пусты, то прерываем процедуру
		if (tName.length()==0 || tType.length()==0 || tSerN.length()==0){
			AboutMessageWindow errorMessage = new AboutMessageWindow("Ошибка", "Заполнены не все обходимые поля");
			errorMessage.show();
			return;
		}
		if (tOwner.length() == 0) tOwner = "-"; //Не критически важные заполняем условно прочерком
		if (tGosN.length() == 0) tGosN = "-";
			
		//Создали новое устройство
		newDevice = new Device(tName, tType, tSerN, tOwner, tGosN);			
		//Проверим, не существует ли аналогичное в БД
		if (newDevice.isExist()) {
			AboutMessageWindow errorMessage = new AboutMessageWindow("Ошибка", "Прибор данного типа с таким серийным номером уже существует!");
			errorMessage.show();
			return;
		}
		
		//Проверим, все ли элементы проинициализированны
		int notInitializedElementsCount = 0;
		for (Element cElm : this.elements) {
			if (cElm == null) {
				notInitializedElementsCount++;
			}
		}
		if (notInitializedElementsCount != 0) {
			int answer = 0;
			try {
				YesNoWindow qWin = new YesNoWindow("","Вы проиницилизировали не все компоненты создаваемого СИ.\nЕсли Вы желаете сохранить только проинициализированные, то нажмите - \"Да\"."
						+ "Если желаее продолжить редактирование, то нажмите - \"Нет\".");
				answer = qWin.showAndWait();
			}
			catch(IOException ioExp) {
				ioExp.getStackTrace();
			}
			if (answer != 0) {
				return;
			}
		}

		for (Element cElm : this.elements) {
			if (cElm != null) {
				newDevice.addElement(cElm);
			}
		}
		
		//и наконец, пробуем сохранить все в БД
		try{
			DataBaseManager.getDB().BeginTransaction();
			newDevice.saveInDB();
			DataBaseManager.getDB().Commit();
			AboutMessageWindow sucsessMessage = new AboutMessageWindow("Успешно", "Успешное сохранение");
			sucsessMessage.show();
			Stage stage = (Stage) saveBtn.getScene().getWindow();
			stage.close();
			NewDeviceWindow.deleteNewDeviceWindow();
		}
		catch(SQLException sqlExp){
			DataBaseManager.getDB().RollBack();
			AboutMessageWindow errorMessage = new AboutMessageWindow("Ошибка", "Ошибка: " + sqlExp.getMessage());
			errorMessage.show();
		}
		catch(SavingException noExp) {
			DataBaseManager.getDB().RollBack();
			AboutMessageWindow errorMessage = new AboutMessageWindow("Ошибка", "Ошибка: " + noExp.getMessage());
			errorMessage.show();
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
