package DBEditForm;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import AboutMessageForm.AboutMessageWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Device;
import DevicePack.Element;
import ErrorParamsPack.ErrorParamsWindow;
import FileManagePack.FileManager;
import GUIpack.InfoRequestable;
import GUIpack.StringGridFX;
import NewElementPack.NewElementWindow;
import SearchDevicePack.SearchDeviceWindow;
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationPack.MeasResult;
import YesNoDialogPack.YesNoWindow;
import _tempHelpers.Adapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class DBEditController implements InfoRequestable {
	
//Левая часть окна
	@FXML
	private Button errorParamsBtn;	
	@FXML
	private Button searchDeviceBtn;
	@FXML
	private ComboBox<String> nameComboBox;
	private ObservableList<String> devNamesList;
	@FXML
	private TextField typeTextFiel;
	@FXML
	private TextField serNumTextField;
	@FXML
	private TextField ownerTextField;
	@FXML
	private TextField gosNumTextField;
	@FXML
	private Button saveDeviceModificationBtn;
	@FXML
	private Button deleteDeviceBtn;
	@FXML
	private ListView<String> elementsListView;
	private ContextMenu elemtnsListViewContextMenu;
	private ObservableList<String> elementsList;
//---------------------------------------------

//Правая часть окна
	//Шапка
	//---------------------------------
/*	//Редактирование параметров годности
	@FXML
	private VBox paramsBox;
	//Таблица для параметров
	@FXML
	private ScrollPane paramsScrollPane;
	@FXML
	private AnchorPane paramsTablePane;
	private StringGridFX paramsTable;
	@FXML
	private Pane paramsBtnPane;
	@FXML
	private Button deleteParamsBtn;
	@FXML
	private Button saveParamsModBtn;*/
	//------------------------------
	//Просмотр результатов измерений
	@FXML
	private VBox resultBox;
	@FXML
	private ComboBox<String> verificationDateComboBox;
	private ObservableList<String> verificationDateList;	
	@FXML
	private ComboBox<String> currentMeasUnitComboBox;
	private ObservableList<String> measUnitsList;	
	//таблица для результатов
	@FXML
	private ScrollPane resultsScrollPane;
	@FXML
	private AnchorPane resultsTablePane;
	private StringGridFX resultsTable;
	@FXML
	private Button deletResBtn;
	@FXML
	private Label measUnitLabel;
	@FXML
	private Label dateLabel;
	
//---------------------------------------------	
	private Device modDevice;  //Редактируемое средство измерения
	private MeasResult currentResult;  //Отображаемый результат измерения
	ToleranceParametrs currentParams;
	private int currentElementIndex;
	private ArrayList<ArrayList<String>> verifications;
	
//---------------------------------------------------------
	@FXML
	private void initialize() {		
		resultsTable = this.createResultsTable();		
		elementsList = FXCollections.observableArrayList();
		verificationDateList = FXCollections.observableArrayList();
		measUnitsList = FXCollections.observableArrayList();
		
		devNamesList = FXCollections.observableArrayList();
		try {
			FileManager.LinesToItems(new File(".").getAbsolutePath() + "//files//sitypes.txt", devNamesList);
		} catch (Exception exp) {
			devNamesList.add("Рабочий эталон ККПиО");
			devNamesList.add("Набор нагрузок волноводных");
			devNamesList.add("Нагрузки волноводные согласованные");
			devNamesList.add("Комплект поверочный");
			devNamesList.add("Калибровочный и поверочный комплекты мер");
			devNamesList.add("Нагрузки волноводные КЗ подвижные");
		} 
		nameComboBox.setItems(devNamesList);
		
		//propertiesList = FXCollections.observableArrayList();
		//propertiesList.add("Критерий годности");
		//propertiesList.add("Результат измерения");
		
		this.verificationDateComboBox.setItems(this.verificationDateList);
		//this.currentMeasUnitComboBox.setItems(this.measUnitsList);				
		this.currentMeasUnitComboBox.setItems(measUnitsList);
		//this.editedPropertyComboBox.setItems(propertiesList);
		
		elemtnsListViewContextMenu = new ContextMenu();
		MenuItem deleteItem = new MenuItem("Удалить");
		MenuItem editItem = new MenuItem("Редактировать");
		elemtnsListViewContextMenu.getItems().addAll(deleteItem, editItem);
		
		deleteItem.setOnAction(event->{
			try {
				YesNoWindow qWin = new YesNoWindow("Удалить?", "Удалить выбранный элемент:\n" + 
								this.elementsListView.getSelectionModel().getSelectedItem().toString());
				int answer = qWin.showAndWait();
				if (answer == 0) {
					deleteElement(this.elementsListView.getSelectionModel().getSelectedIndex());
				}	
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		editItem.setOnAction(event->{
			int index = this.elementsListView.getSelectionModel().getSelectedIndex();
			Element elm = this.modDevice.includedElements.get(index);
			try {
				NewElementWindow elementWin = new NewElementWindow(elm);
				elementWin.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		this.elementsListView.setOnContextMenuRequested(event->{
			double x = event.getScreenX();
			double y = event.getScreenY();
			elemtnsListViewContextMenu.show(this.elementsListView, x, y);
		});
	}
//InfoRequestable
	@Override
	public void setDevice(Device device) {
		modDevice = device;	
	}

	@Override
	public void representRequestedInfo() {
		this.nameComboBox.setValue(modDevice.getName());
		this.typeTextFiel.setText(modDevice.getType());
		this.ownerTextField.setText(modDevice.getOwner());
		this.serNumTextField.setText(modDevice.getSerialNumber());
		this.gosNumTextField.setText(modDevice.getGosNumber());	
			
		this.elementsList.clear();
		for (Element elm : this.modDevice.includedElements) {
			String item = elm.getType() + " №" + elm.getSerialNumber();
			this.elementsList.add(item);
		}
		this.elementsListView.setItems(elementsList);		
	}
//---------------------------------------------------------	
/*	//Создание таблиц
	private StringGridFX createParamsTable() {
		ArrayList<String> tableHeads = new ArrayList<String>();
		tableHeads.add("Частота, ГГц");
		tableHeads.add("Модуль - нижний допуск");
		tableHeads.add("Модуль - верхний допуск");
		tableHeads.add("Фаза - нижний допуск");
		tableHeads.add("Фаза - верхний допуск");
		return new StringGridFX(5, 10, 800, 100, paramsScrollPane, paramsTablePane, tableHeads);
	}
*/	
	private StringGridFX createResultsTable() {
		ArrayList<String> tableHeads = new ArrayList<String>();
		tableHeads.add("Частота, ГГц");
		tableHeads.add("Изм. знач. модуля");
		tableHeads.add("Погрешность");
		tableHeads.add("Изм. знач. фазы");
		tableHeads.add("Погрешность");
		return new StringGridFX(5, 10, 800, 100, resultsScrollPane, resultsTablePane, tableHeads);
	}
//---------------------------------------------------------
	//Очистка окна
	private void clearGUI() {
		this.nameComboBox.setValue("");
		this.typeTextFiel.setText("");
		this.ownerTextField.setText("");
		this.serNumTextField.setText("");
		this.gosNumTextField.setText("");
		
		this.elementsList.clear();
		this.elementsListView.setItems(elementsList);
		this.verificationDateList.clear();
		this.verificationDateComboBox.setItems(verificationDateList);
	}
//---------------------------------------------------------	
//---------------------------------------------------------		
	//Отображение результатов
	private void showResult() throws SQLException {	
		this.resultsTable.clear();
		int necessaryRowCount = this.currentResult.freqs.size();
		if (this.resultsTable.getRowCount() < necessaryRowCount) {
			while(this.resultsTable.getRowCount() < necessaryRowCount) {
				this.resultsTable.addRow();
			}
		}
		else if (this.resultsTable.getRowCount() > necessaryRowCount) {
			while(this.resultsTable.getRowCount() > necessaryRowCount) {
				this.resultsTable.deleteRow(this.resultsTable.getRowCount());
			}				
		}
		this.resultsTable.setColumnFromDouble(0, this.currentResult.freqs);
		String keys[] = {"m_S11", "err_m_S11", "p_S11", "err_p_S11", 
				 "m_S12", "err_m_S12", "p_S12", "err_p_S12",
				 "m_S21", "err_m_S21", "p_S21", "err_p_S21", 
				 "m_S22", "err_m_S22", "p_S22", "err_p_S22"};
		for (int i = 1; i < 5; i++) {
			int currentMeasUnitIndex = this.currentMeasUnitComboBox.getSelectionModel().getSelectedIndex();
			String key = keys[i-1 + currentMeasUnitIndex * 4];
			this.resultsTable.setColumnFromDouble(i, Adapter.HashMapToArrayList(this.currentResult.freqs, this.currentResult.values.get(key)));
		}		
	}
//---------------------------------------------------------
	@FXML
	private void errorParamsBtnClick() throws IOException {
		ErrorParamsWindow.getErrorParamsWindow().show();
	}	
	
//---Работа с устройством---
//Сохранение изменений в устройство
	@FXML
	private void saveDeviceModificationBtnClick(){
		HashMap<String, String> editingValues = new HashMap<String, String>();
		editingValues.put("NameOfDevice", this.nameComboBox.getSelectionModel().getSelectedItem().toString()); 
		editingValues.put("TypeOfDevice", this.typeTextFiel.getText());
		editingValues.put("SerialNumber", this.serNumTextField.getText());
		editingValues.put("Owner", this.ownerTextField.getText());
		editingValues.put("GosNumber", this.gosNumTextField.getText());
		try {
			this.modDevice.editInfoInDB(editingValues);
			try {
				AboutMessageWindow aboutWin = new AboutMessageWindow("Успешно", "Изменения сохранены");
				aboutWin.show();
			}
			catch(IOException ioExp) {
				ioExp.getStackTrace();
			}
		}
		catch(SQLException sqlExp) {
			try {
				AboutMessageWindow aboutWin = new AboutMessageWindow("Ошибка", "Не удалось сохранить изменения");
				aboutWin.show();
			}
			catch(IOException ioExp) {
				ioExp.getStackTrace();
			}
		}
	}
//----------------------УДАЛЕНИЯ---------------------------
//-----------------Удаление устройства---------------------	
	private void deleteDevice() throws IOException {
		try {
			DataBaseManager.getDB().BeginTransaction();
			modDevice.deleteFromDB();
			DataBaseManager.getDB().Commit();
			clearGUI(); 
			modDevice = null;
		}
		catch(SQLException sqlExp) {
			DataBaseManager.getDB().RollBack();
			AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Ошибка доступа к БД\nпри попытке удаления");
			msgWin.show();
		}
	}
//---------------------------------------------------------
//--------------------Удаление элемента--------------------
	private void deleteElement(int index) throws IOException {
		if (index > 0) return;
		try {
			DataBaseManager.getDB().BeginTransaction();
			this.modDevice.includedElements.get(index).deleteFromDB();
			DataBaseManager.getDB().Commit();
			this.elementsList.remove(index);
		}
		catch(SQLException sqlExp) {
			DataBaseManager.getDB().RollBack();
			AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Ошибка доступа к БД\nпри попытке удаления");
			msgWin.show();
		}
	}	
//---------------------------------------------------------
//---------------------------------------------------------
	@FXML
	private void deleteDeviceBtnClick() throws IOException {
		deleteDevice();
	}	
//--- Работа с результатами измерений ---

//Поиск
	@FXML
	private void searchDeviceBtnClick() {
		try {
			SearchDeviceWindow.getSearchDeviceWindow(modDevice, this).show();
		}
		catch(IOException exp) {
			//
		}
	}
		
	@FXML
	private void deletResBtnClick() throws IOException {
		try {
			currentResult.deleteFromDB();
		}
		catch(SQLException sqlExp) {
			AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Ошибка доступа к БД\nпри удалении результатов измерения");
			msgWin.show();	
		}
	}	
	
	@FXML
	private void elementsListViewClick() throws IOException {
		currentElementIndex = elementsListView.getSelectionModel().getSelectedIndex();
		Element cElm = this.modDevice.includedElements.get(currentElementIndex);
		try {
			verifications = cElm.getListOfVerifications();
			this.verificationDateList.clear();
			for (int i = 0; i < verifications.size(); i++) {
				verificationDateList.add(verifications.get(i).get(1));
			}						
			this.verificationDateComboBox.setItems(verificationDateList);			
		}
		catch(SQLException exp) {
			AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Ошибка доступа к БД\nпри получении списка проведенных поверок");
			msgWin.show();
		}		
	}	
		
	@FXML
	private void verificationDateComboBoxClick() throws IOException {
		Element cElm = this.modDevice.includedElements.get(currentElementIndex);
		try {
			ArrayList<String> items = new ArrayList<String>();
			String path = new File(".").getAbsolutePath();
			if (cElm.getMeasUnit().equals("vswr")) {
				path += "\\files\\vswr.txt";			
			}
			else {
				path += "\\files\\gamma.txt";
			}
			FileManager.LinesToItems(path, items);
			
			measUnitsList.clear();
			measUnitsList.add(items.get(0));			
			if (cElm.getPoleCount() == 4) {
				measUnitsList.add(items.get(1));
				measUnitsList.add(items.get(2));
				measUnitsList.add(items.get(3));
			}	
		}
		catch(Exception Exp) {
			measUnitsList.add("S11");			
			if (cElm.getPoleCount() == 4) {
				measUnitsList.add("S12");
				measUnitsList.add("S21");
				measUnitsList.add("S22");
			}	
			this.currentMeasUnitComboBox.setItems(measUnitsList);
		}
	}		
	
	@FXML
	private void currentMeasUnitComboBoxClick() throws IOException {
		try {
			int resIndex = Integer.parseInt(this.verifications.get(
					this.verificationDateComboBox.getSelectionModel().getSelectedIndex()).get(0));
			this.currentResult = new MeasResult(this.modDevice.includedElements.get(currentElementIndex), resIndex);
			showResult();
		}
		catch(SQLException sqlExp) {
			AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Ошибка доступа к БД\nпри получении результатов измерения");
			msgWin.show();
		}
	}
}