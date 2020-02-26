package DBEditForm;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import AboutMessageForm.AboutMessageWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Device;
import DevicePack.Element;
import Exceptions.SavingException;
import FileManagePack.FileManager;
import GUIpack.InfoRequestable;
import GUIpack.Tables.ResultsTable;
import NewElementPack.NewElementController;
import NewElementPack.NewElementWindow;
import SearchDevicePack.SearchDeviceWindow;
import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import VerificationPack.MeasResult;
import YesNoDialogPack.YesNoWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class DBEditController implements InfoRequestable {
	
//Левая часть окна
	@FXML
	private Button searchDeviceBtn;
	@FXML
	private ComboBox<String> nameComboBox;
	private ObservableList<String> devNamesList;
	@FXML
	private TextField typeTextField;
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
	private ContextMenu verificationsDatesListViewContextMenu;
	private ObservableList<String> elementsList;
	
	//Правая часть окна
	//Просмотр результатов измерений
	@FXML
	private Label measInfoLabel;
	@FXML
	private Label measInfoLabel2;
	@FXML
	private ListView<String> verificationDateListView;
	private ObservableList<String> verificationDateList;	
	@FXML
	private ListView<String> currentMeasUnitListView;
	private ObservableList<String> measUnitsList;	
	//таблица для результатов
	@FXML
	private ScrollPane resultsScrollPane;
	@FXML
	private AnchorPane resultsTablePane;
	private ResultsTable resultsTable;
	@FXML
	private Label measUnitLabel;
	@FXML
	private Label dateLabel;
	//Графики
	@FXML
	private HBox chartBox;
	NumberAxis moduleX = new NumberAxis();
	NumberAxis moduleY = new NumberAxis();
	NumberAxis phaseX = new NumberAxis();
	NumberAxis phaseY = new NumberAxis();
	private LineChart<Number, Number> moduleChart = new LineChart<Number, Number>(moduleX, moduleY);
	private LineChart<Number, Number> phaseChart = new LineChart<Number, Number>(phaseX, phaseY);	
	private XYChart.Series<Number, Number> moduleSeries = new XYChart.Series<Number, Number>();
	private XYChart.Series<Number, Number> phaseSeries = new XYChart.Series<Number, Number>();

//---------------------------------------------	
	private Device modDevice;  //Редактируемое средство измерения
	private MeasResult currentResult;  //Отображаемый результат измерения
	private Integer currentElementIndex = null;
	private Integer currentDateIndex = null;
	private Integer currentMeasUnitIndex = null;	
	private Element currentElement = null;
	private List<List<String>> verifications;
	
//---------------------------------------------------------
	@FXML
	private void initialize() {						
		resultsTable = new ResultsTable(resultsTablePane);		
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
		
		chartBox.getChildren().add(moduleChart);
		chartBox.getChildren().add(phaseChart);
		verificationDateListView.setItems(this.verificationDateList);				
		currentMeasUnitListView.setItems(measUnitsList);
		moduleX.setAutoRanging(false);
		phaseX.setAutoRanging(false);
		
		moduleChart.setTitle("|Sxx|(f)");
		phaseChart.setTitle("фаза Sxx(f)");
		moduleSeries.setName("Значения модуля");
		phaseSeries.setName("Значение фазы");
		
		createElemtnsListViewContextMenu();		
		createVerificationsDatesContextMenu();
	}
	
	private void createElemtnsListViewContextMenu() {
		this.elemtnsListViewContextMenu = new ContextMenu();
		MenuItem deleteElementItem = new MenuItem("Удалить");
		MenuItem editElementItem = new MenuItem("Редактировать");
		MenuItem addElementItem = new MenuItem("Добавить");
		this.elemtnsListViewContextMenu.getItems().addAll(deleteElementItem, editElementItem, addElementItem);
		
		//deleting element
		deleteElementItem.setOnAction(event->{
			int answer = YesNoWindow.createYesNoWindow("Удалить?", "Удалить выбранный элемент:\n" + 
					this.elementsListView.getSelectionModel().getSelectedItem().toString()).showAndWait();
			if (answer == 0) {
				int deletedElementIndex = this.elementsListView.getSelectionModel().getSelectedIndex();
				if (deletedElementIndex < 0) return;
				try {
					DataBaseManager.getDB().BeginTransaction();
					this.modDevice.includedElements.get(deletedElementIndex).deleteFromDB();
					int currentModdeviceId = this.modDevice.getId();
					this.modDevice = new Device(currentModdeviceId);
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					
					DataBaseManager.getDB().Commit();
					this.modDevice.removeElement(deletedElementIndex);
					this.elementsList.remove(deletedElementIndex);
				}
				catch(SQLException sqlExp) {
					DataBaseManager.getDB().RollBack();
					AboutMessageWindow.createWindow("Ошибка", "Ошибка доступа к БД\nпри попытке удаления").show();
				}				
			}	
			
		});
		
		//editing element
		editElementItem.setOnAction(event -> {
			int index = this.elementsListView.getSelectionModel().getSelectedIndex();
			Element elm = this.modDevice.includedElements.get(index);
			try {
				NewElementWindow elementWin = new NewElementWindow(elm);
				elementWin.showAndWait();
				int currentModdeviceId = this.modDevice.getId();
				this.modDevice = new Device(currentModdeviceId);
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!							
				this.verificationDateList.clear();
				this.verifications = currentElement.getListOfVerifications();	
				int nominalIndex = this.currentElement.getNominalId();
				for (int i = 0; i < this.verifications.size(); i++) {
					String item = null;
					int resIndex = Integer.parseInt(this.verifications.get(i).get(0));
					if (resIndex != nominalIndex)
						item = "Поверка, проведенная " + this.verifications.get(i).get(1);
					else 
						item = "Номиналыные занчение, полученные " + this.verifications.get(i).get(1);
					verificationDateList.add(item);
				}				
			} catch (IOException ioExp) {
				ioExp.printStackTrace();
			} catch (SQLException sqlExp) {
				sqlExp.printStackTrace();
			}
		});
		
		//adding new element
		addElementItem.setOnAction(event -> {
			if (modDevice != null) {
				try {
					NewElementWindow elementWin = new NewElementWindow();
					elementWin.showAndWait();	
					NewElementController ctrl = (NewElementController)elementWin.getControllerClass();
					Element newElement = new Element(ctrl);
					modDevice.addElement(newElement);
					newElement.saveInDB();
					String item = newElement.getType() + " №" + newElement.getSerialNumber();
					elementsList.add(item);
					AboutMessageWindow.createWindow("Успешное сохранение", "Новый элемент успешно добавлен").show();
				} 
				catch (IOException ioExp) {
					AboutMessageWindow.createWindow("Ошибка открытия окна", "Ошибка открытия окна создания нового элемента.").show();
					ioExp.printStackTrace();
				} 
				catch (SavingException sExp) {
					AboutMessageWindow.createWindow("Ошибка сохранения", sExp.getMessage()).show();
					sExp.printStackTrace();
				} 
				catch (SQLException sqlExp) {
					AboutMessageWindow.createWindow("Ошибка сохранения", "База данных отсутсвует или повреждена").show();
					sqlExp.printStackTrace();
				}
			}
		});
		
		elementsListView.setOnContextMenuRequested(event -> {
			double x = event.getScreenX();
			double y = event.getScreenY();
			elemtnsListViewContextMenu.show(elementsListView, x, y);
		});
	}
	
	private void createVerificationsDatesContextMenu() {
		verificationsDatesListViewContextMenu = new ContextMenu();
		MenuItem deleteResultItem = new MenuItem("Удалить результат");
		verificationsDatesListViewContextMenu.getItems().addAll(deleteResultItem);
		
		deleteResultItem.setOnAction(event -> {						
			int resIndex = Integer.parseInt(verifications.get(currentDateIndex).get(0));
			if (resIndex == currentElement.getNominalId()) {
				AboutMessageWindow.createWindow("Ошибка", "Значения, использующиеся в качестве\nноминальных не могут быть удалены из БД").show();
				return;
			}
			int answer = YesNoWindow.createYesNoWindow("Удалить?", "Вы уверены, что хотите удалить результаты\nизмерений от " + verifications.get(currentDateIndex).get(1)).showAndWait();
			if (answer == 1) {
				return;
			}
			MeasResult deletedResult = null;
			try {
				deletedResult = new MeasResult(modDevice.includedElements.get(currentElementIndex), resIndex);
				deletedResult.deleteFromDB();
				AboutMessageWindow.createWindow("Успешно", "Результат измерения удален").show();
				verifications.remove((int)currentDateIndex);
				verificationDateList.remove((int)currentDateIndex);
			} catch (SQLException sqlExp) {
				AboutMessageWindow.createWindow("Ошибка", "Ошибка доступа к БД\nпри попытке удаления").show();
			}
		});
		
		verificationDateListView.setOnContextMenuRequested(event->{
			double x = event.getScreenX();
			double y = event.getScreenY();
			verificationsDatesListViewContextMenu.show(verificationDateListView, x, y);
		});
	}
//InfoRequestable
	@Override
	public void setDevice(Device device) {
		modDevice = device;	
	}
	public void removeDevice() {
		modDevice = null;
	}

	@Override
	public void representRequestedInfo() {
		nameComboBox.setValue(modDevice.getName());
		typeTextField.setText(modDevice.getType());
		ownerTextField.setText(modDevice.getOwner());
		serNumTextField.setText(modDevice.getSerialNumber());
		gosNumTextField.setText(modDevice.getGosNumber());				
		elementsList.clear();
		for (Element elm : this.modDevice.includedElements) {
			String item = elm.getType() + " №" + elm.getSerialNumber();
			elementsList.add(item);
		}
		elementsListView.setItems(elementsList);		
	}
//---------------------------------------------------------
	//Очистка окна
	private void clearGUI() {
		nameComboBox.setValue("");
		typeTextField.setText("");
		ownerTextField.setText("");
		serNumTextField.setText("");
		gosNumTextField.setText("");
		
		elementsList.clear();
		verificationDateList.clear();
		measUnitsList.clear();
		resultsTable.clear();
	}
	
//---Работа с устройством---
//Сохранение изменений в устройство
	@FXML
	private void saveDeviceModificationBtnClick() {
		HashMap<String, String> editingValues = new HashMap<String, String>();
		editingValues.put("NameOfDevice", nameComboBox.getSelectionModel().getSelectedItem().toString()); 
		editingValues.put("TypeOfDevice", typeTextField.getText());
		editingValues.put("SerialNumber", serNumTextField.getText());
		editingValues.put("Owner", ownerTextField.getText());
		editingValues.put("GosNumber", gosNumTextField.getText());
		try {
			DataBaseManager.getDB().BeginTransaction();
			modDevice.editInfoInDB(editingValues);
			DataBaseManager.getDB().Commit();				
			AboutMessageWindow.createWindow("Успешно", "Изменения сохранены").show();
		}
		catch(SQLException sqlExp) {
			DataBaseManager.getDB().RollBack();
			AboutMessageWindow.createWindow("Ошибка", "Не удалось сохранить изменения").show();
		}
	}	
//---------------------------------------------------------
	@FXML
	private void deleteDeviceBtnClick() {
		int answer = YesNoWindow.createYesNoWindow("Удалить?", "Вы уверены, что хотите удалить этот прибор?").showAndWait();
		if (answer == 0) {
			try {
				DataBaseManager.getDB().BeginTransaction();
				modDevice.deleteFromDB();
				DataBaseManager.getDB().Commit();
				clearGUI(); 
				modDevice = null;
				AboutMessageWindow.createWindow("Успешно", "Прибор удален").show();
			}
			catch(SQLException sqlExp) {
				DataBaseManager.getDB().RollBack();
				AboutMessageWindow.createWindow("Ошибка", "Ошибка доступа к БД\nпри попытке удаления").show();
			}
		}		
	}	
//--- Работа с результатами измерений ---

//Поиск
	@FXML
	private void searchDeviceBtnClick() {
		SearchDeviceWindow.getSearchDeviceWindow(modDevice, this).show();
	}
			
	@FXML
	private void elementsListViewClick() {
		if (this.modDevice == null) {
			return;
		}		
		this.resultsTable.clear();
		this.verificationDateList.clear();
		this.measUnitsList.clear();
		
		this.currentDateIndex = null;
		this.currentMeasUnitIndex = null;		
		this.currentElementIndex = this.elementsListView.getSelectionModel().getSelectedIndex();
		this.currentElement = this.modDevice.includedElements.get(currentElementIndex);
		
		try {
			this.verifications = this.currentElement.getListOfVerifications();	
			int nominalIndex = this.currentElement.getNominalId();
			for (int i = 0; i < this.verifications.size(); i++) {
				String item = null;
				int resIndex = Integer.parseInt(this.verifications.get(i).get(0));
				if (resIndex != nominalIndex)
					item = "Поверка, проведенная " + this.verifications.get(i).get(1);
				else 
					item = "Номиналыные занчение, полученные " + this.verifications.get(i).get(1);
				this.verificationDateList.add(item);
			}						
			this.verificationDateListView.setItems(this.verificationDateList);			
		}
		catch(SQLException exp) {
			AboutMessageWindow.createWindow("Ошибка", "Ошибка доступа к БД\nпри получении списка проведенных поверок").show();
		}		
	}	
		
	@FXML
	private void verificationDateListViewClick() {
		if (currentElement == null) {
			return;
		}
		currentDateIndex = verificationDateListView.getSelectionModel().getSelectedIndex();
		try {
			ArrayList<String> items = new ArrayList<String>();
			String path = new File(".").getAbsolutePath();
			path += "\\files\\" + currentElement.getMeasUnit() + ".txt";
			FileManager.LinesToItems(path, items);			
			measUnitsList.clear();
			for (int i = 0; i < currentElement.getSParamsCout(); i++) {
				measUnitsList.add(items.get(i));
			}
		}
		catch(Exception Exp) {
			measUnitsList.add("S11");			
			if (currentElement.getPoleCount() == 4) {
				measUnitsList.add("S12");
				measUnitsList.add("S21");
				measUnitsList.add("S22");
			}	
			this.currentMeasUnitListView.setItems(measUnitsList);
		}
		
		if (currentMeasUnitIndex != null) {
			showResult();
		}
	}		
	
	@FXML
	private void currentMeasUnitListViewClick() throws IOException {
		if (currentElement == null || currentDateIndex == null) {
			return;
		}
		currentMeasUnitIndex = currentMeasUnitListView.getSelectionModel().getSelectedIndex();
		showResult();
	}
	
	private void showResult() {
		try {
			int resIndex = Integer.parseInt(verifications.get(currentDateIndex).get(0));
			currentResult = new MeasResult(modDevice.includedElements.get(currentElementIndex), resIndex);
			resultsTable.showResult(currentResult, S_Parametr.values()[currentMeasUnitIndex]);
			//set table column headers
			String newPhaseErrorHeader = null;
			if (modDevice.includedElements.get(currentElementIndex).getPhaseToleranceType().equals("percent")) {
				newPhaseErrorHeader = "Погрешность, %";
			} 
			else {
				newPhaseErrorHeader = "Погрешность, \u00B0";
			}
			resultsTable.setHead(4, newPhaseErrorHeader);
			
			String newModuleErrorHeader = null;
			if (modDevice.includedElements.get(currentElementIndex).getModuleToleranceType().equals("percent")) {
				newModuleErrorHeader = "Погрешность, %";
			} 
			else {
				newModuleErrorHeader = "Погрешность";
			}
			resultsTable.setHead(2, newModuleErrorHeader);
			
			String date = currentResult.getDateOfMeasByString();
			date = date.split(" ")[0];
			String unit = currentMeasUnitListView.getSelectionModel().getSelectedItem();
			
			ObservableList<Data<Number, Number>> dataModule = FXCollections.observableArrayList();
			ObservableList<Data<Number, Number>> dataPhase = FXCollections.observableArrayList();
			String key = MeasUnitPart.MODULE + "_" + S_Parametr.values()[currentMeasUnitIndex];
			for (int i=0; i < currentResult.values.get(key).size(); i++) {
				//size of Collection with module should has same size as with phase 
				double cFreq = currentResult.freqs.get(i);
				double cModule = currentResult.values.get(key).get(cFreq);
				double cPhase = currentResult.values.get(MeasUnitPart.PHASE + "_" + S_Parametr.values()[currentMeasUnitIndex]).get(cFreq);
				dataModule.add(new XYChart.Data<Number, Number>(cFreq, cModule));
				dataPhase.add(new XYChart.Data<Number, Number>(cFreq, cPhase));
			}
						
			moduleSeries.setData(dataModule);
			phaseSeries.setData(dataPhase);
			moduleChart.getData().add(moduleSeries);
			phaseChart.getData().add(phaseSeries);
			moduleChart.setTitle("Модуль " + currentMeasUnitListView.getSelectionModel().getSelectedItem() + "(f)");
			phaseChart.setTitle("Фаза " + currentMeasUnitListView.getSelectionModel().getSelectedItem() + "(f)");
			
			double upFreq = currentResult.freqs.get(currentResult.freqs.size() - 1);
			double downFreq = currentResult.freqs.get(0);
			moduleX.setUpperBound(upFreq);
			phaseX.setUpperBound(upFreq);
			moduleX.setLowerBound(downFreq);
			phaseX.setLowerBound(downFreq);
			
			measInfoLabel.setText("Результаты измерения " + unit + " проведенного " + date + " для \"" + currentResult.getMyOwner().getType() + " " +
					currentResult.getMyOwner().getSerialNumber() + "\" ");
			measInfoLabel2.setText("из состава \"" + currentResult.getMyOwner().getMyOwner().getName() + " " +
					currentResult.getMyOwner().getMyOwner().getType() + " №" + currentResult.getMyOwner().getMyOwner().getSerialNumber() + "\".");
		}
		catch(SQLException sqlExp) {
			AboutMessageWindow.createWindow("Ошибка", "Ошибка доступа к БД\nпри получении результатов измерения").show();
		}
	}
}