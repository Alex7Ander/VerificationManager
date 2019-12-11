package VerificationForm;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import AboutMessageForm.AboutMessageWindow;
import DevicePack.Device;
import DevicePack.Element;
import FileManagePack.FileManager;
import GUIpack.InfoRequestable;
import GUIpack.Tables.Table;
import GUIpack.Tables.VerificationTable;
import ProtocolCreatePack.ProtocolCreateWindow;
import SearchDevicePack.SearchDeviceWindow;
import StartVerificationPack.StartVerificationController;
import StartVerificationPack.StartVerificationWindow;
import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import VerificationPack.MeasResult;
import VerificationPack.VerificationProcedure;
import _tempHelpers.Adapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class VerificationController implements InfoRequestable {
	@FXML
	private Button searchDeviceBtn;
	@FXML
	private Button startBtn;
	@FXML
	private Button saveBtn;
	@FXML
	private Button closeBtn;
	@FXML
	private Button createProtocolBtn;
	@FXML
	private Label nameLabel;
	@FXML
	private Label typeLabel;
	@FXML
	private Label serNumLabel;
	@FXML
	private ComboBox<String> elementComboBox;
	private ObservableList<String> listOfElements;
	@FXML
	private ComboBox<String> parametrComboBox;
	private ObservableList<String> listOfParametrs;
	
//Таблица с результатами	
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private AnchorPane tablePane;
	private Table resultTable;

//Процедура поверки
	VerificationProcedure verification;
//Результат поверки
	private ArrayList<MeasResult> verificationResult;	
//Ссылка на поверяемое СИ
	public Device verificatedDevice;		
	private int currentElementIndex;
	private int currentParamIndex;
	
	@FXML
	private void initialize(){			
		listOfElements = FXCollections.observableArrayList();
		listOfParametrs = FXCollections.observableArrayList();		
		verificationResult = new ArrayList<MeasResult>();
		currentElementIndex = 0;
		currentParamIndex = 0;		   		
		//StringGridPosition position = new StringGridPosition(1110, 100, scrollPane, tablePane);
		//resultTable = new VerificationStringGridFX(position);	
		resultTable = new VerificationTable(tablePane);
	}
	
	@FXML
	private void searchDeviceBtnClick(ActionEvent event) throws IOException {
		SearchDeviceWindow.getSearchDeviceWindow(verificatedDevice, this).show();
	}

//Нажатие на кнопку Start
	@FXML
	private void startBtnClick(ActionEvent event) throws IOException, InterruptedException {
		if(verificatedDevice != null) {
			StartVerificationWindow.getStartVerificationWindow().show();						
		}
		else{
			AboutMessageWindow.createWindow("Ошибка","Вы не выбрали средство измерения для поверки").show();
		}
	}
	
//Нажатие на кнопку сохранить
	@FXML
	private void saveBtnClick(ActionEvent event) throws IOException {
		if (verificationResult.size() != 0) {
			try {
				for (int i = 0; i < verificationResult.size(); i++) {
					verificationResult.get(i).saveInDB();
				}
				AboutMessageWindow.createWindow("Успешно", "Результаты поверки сохранены в БД").show();
			}
			catch(SQLException sqlExp) {
				AboutMessageWindow.createWindow("Ошибка", "Не удалось сохранить результаты в БД");
			}
		}
		else {
			AboutMessageWindow.createWindow("Ошибка","Процедура поверки еще не закончена").show();
		}
	}
	
//Нажатие на кнопку создания протокола
	@FXML
	private void createProtocolBtnClick(ActionEvent event) throws IOException {
		if (verificationResult.size() != 0) {
			String[] docTypes = {"Cвидетельство о поверке", "Извещение о непригодности"};	
			createProtocol(docTypes);
		}
		else {
			AboutMessageWindow.createWindow("Ошибка","Процедура поверки еще не закончена").show();
		}
	}
	public void createProtocol(String[] docTypes) throws IOException {
		this.verification.setDeviceInformation(this.verificatedDevice);
		ProtocolCreateWindow.getProtocolCreateWindow(docTypes, verificationResult, this.verification).show();
	}
//Изменить занчение в комбобоксе со списком элементов	
	@FXML
	private void elementComboBoxChange() {
		
		//Получим индекс отображаемого элемента
		currentElementIndex = this.elementComboBox.getSelectionModel().getSelectedIndex();
		
		//Установим список параметрав в соответствии количеству полюсов у элемента
		int elPoleCount = this.verificatedDevice.includedElements.get(currentElementIndex).getPoleCount();		
		int countOfParams = 0;
		if (elPoleCount == 2) countOfParams = 1;
		else countOfParams = 4;
		
		String curMeasUnit = this.verificatedDevice.includedElements.get(currentElementIndex).getMeasUnit();		
		try {
			String path = new File(".").getAbsolutePath();
			if (curMeasUnit.equals("vswr")) path += "\\files\\vswr.txt";
			else if (curMeasUnit.equals("gamma")) path += "\\files\\gamma.txt";
			FileManager.LinesToItems(path, countOfParams, listOfParametrs);			
		}
		catch(Exception exp) {
			listOfParametrs.clear();
			listOfParametrs.add("S11");
			listOfParametrs.add("S12");
			listOfParametrs.add("S21");
			listOfParametrs.add("S22");
		}
		parametrComboBox.setItems(listOfParametrs);
		//Установим индекс отображаемого параметра в 0 
		currentParamIndex = 0;
		//И программно выберем параметром под индексом 0
		parametrComboBox.getSelectionModel().select(currentParamIndex);
		//Это приведет к вызову функции parametrComboBoxChange()  //fillTable();
	}
//Изменить значение в комбобоксе с параметрами	
	@FXML
	private void parametrComboBoxChange() {
		currentParamIndex = parametrComboBox.getSelectionModel().getSelectedIndex(); 
		if (currentParamIndex != -1) {
			fillTable();			
		}
	}
//---------------------------	
	public void StartVerification() throws IOException {
		String absPath = new File(".").getAbsolutePath();
		//Получение информации об окружающей среде
		verification = new VerificationProcedure();
		verification.setPrimaryInformation((StartVerificationController)StartVerificationWindow.getStartVerificationWindow().getControllerClass());
		//Создание файла psi.ini
		String psiFilePath = absPath + "\\measurement\\PSI.ini";
		verificatedDevice.createIniFile(psiFilePath);
		//Запуск программы measurement
		File file = new File(absPath + "\\measurement\\Project1.exe");
		Desktop.getDesktop().open(file);				
	}
		
	private void fillTable() {
		ArrayList<String> keys = new ArrayList<String>();
		for (int i = 0; i < S_Parametr.values().length; i++){
			for (int j=0; j < MeasUnitPart.values().length; j++){
				String key = MeasUnitPart.values()[j] + "_" + S_Parametr.values()[i];
				keys.add(key);
				keys.add("ERROR_" + key);
			}
		}

		ArrayList<Double> fr = verificationResult.get(currentElementIndex).freqs;
		int countOfFreq = fr.size();
		if (resultTable.getRowCount() < countOfFreq) {
			while (resultTable.getRowCount() != countOfFreq) 
				resultTable.addRow();
		}
		else if (resultTable.getRowCount() > countOfFreq) {
			while (resultTable.getRowCount() != countOfFreq) 
				resultTable.deleteRow(this.resultTable.getRowCount());
		}

		resultTable.setColumnFromDouble(0, fr);
		List<Double> column1 = Adapter.MapToArrayList(this.verificationResult.get(currentElementIndex).values.get(keys.get(currentParamIndex*4)));
		resultTable.setColumnFromDouble(1, column1);
		List<Double> column2 = Adapter.MapToArrayList(this.verificationResult.get(currentElementIndex).values.get(keys.get(1 + currentParamIndex*4)));
		resultTable.setColumnFromDouble(2, column2);
		List<Double> column4 = Adapter.MapToArrayList(this.verificationResult.get(currentElementIndex).values.get(keys.get(2 + currentParamIndex*4)));
		resultTable.setColumnFromDouble(4, column4);
		List<Double> column5 = Adapter.MapToArrayList(this.verificationResult.get(currentElementIndex).values.get(keys.get(3 + currentParamIndex*4)));
		resultTable.setColumnFromDouble(5, column5);
		List<String> column3 = Adapter.MapToArrayList(this.verificationResult.get(currentElementIndex).suitabilityDecision.get(keys.get(currentParamIndex)));
		resultTable.setColumn(3, column3);
		List<String> column6 = Adapter.MapToArrayList(this.verificationResult.get(currentElementIndex).suitabilityDecision.get(keys.get(2 + currentParamIndex)));
		resultTable.setColumn(6, column6);
		
		//set table column headers
		String newPhaseErrorHeader = null;
		if (verificatedDevice.includedElements.get(currentElementIndex).getPhaseToleranceType().equals("percent")) {
			newPhaseErrorHeader = "Погрешность, %";
		} 
		else {
			newPhaseErrorHeader = "Погрешность, \u00B0";
		}
		resultTable.setHead(5, newPhaseErrorHeader);
		
		String newModuleErrorHeader = null;
		if (verificatedDevice.includedElements.get(currentElementIndex).getModuleToleranceType().equals("percent")) {
			newModuleErrorHeader = "Погрешность, %";
		} 
		else {
			newModuleErrorHeader = "Погрешность";
		}
		resultTable.setHead(2, newModuleErrorHeader);
		
		String newModuleResHeader = parametrComboBox.getSelectionModel().getSelectedItem();
		resultTable.setHead(1, newModuleResHeader);
	}	
//---------------------------	
	@FXML
	private Button fileReadBtn;
	@FXML
	public void fileReadBtnClick() {
		if (verification == null) {
			return;
		}
		this.verificationResult.clear();
		try {
			for (int i=0; i < verificatedDevice.getCountOfElements(); i++) {
				String absPath = new File(".").getAbsolutePath();
				String resFilePath = absPath + "\\measurement\\protokol.ini";
				MeasResult rs = new MeasResult(resFilePath, i+1, this.verificatedDevice.includedElements.get(i));				
				verificationResult.add(rs);
				//Verificating results
				if (verification.isPrimary()) {
					verificatedDevice.includedElements.get(i).getPrimaryModuleToleranceParams().checkResult(rs);
					verificatedDevice.includedElements.get(i).getPrimaryPhaseToleranceParams().checkResult(rs);
				}
				else {
					verificatedDevice.includedElements.get(i).getPeriodicModuleToleranceParams().checkResult(rs);
					verificatedDevice.includedElements.get(i).getPeriodicPhaseToleranceParams().checkResult(rs);
				}
				
				elementComboBox.setValue(listOfElements.get(0));
				parametrComboBox.setValue(listOfParametrs.get(0));
			}
		}
		catch(IOException ioExp) {
			AboutMessageWindow.createWindow("Ошибка", "Отсутствует или поврежден файл\n с результатами измерений").show();
		}
	}
//----------------------------------------	
	@Override
	public void representRequestedInfo() {
		nameLabel.setText(verificatedDevice.getName());
		typeLabel.setText(verificatedDevice.getType());
		serNumLabel.setText(verificatedDevice.getSerialNumber());
		int countOfElements = verificatedDevice.getCountOfElements();	
		try {
			if (countOfElements > 0) {
				listOfElements.clear();
				for(int i = 0; i < verificatedDevice.getCountOfElements(); i++) {
					Element currentEl = verificatedDevice.includedElements.get(i);
					String elementItem = currentEl.getType() + " " + currentEl.getSerialNumber();
					listOfElements.add(elementItem);
				}
				elementComboBox.setItems(listOfElements);
			}
			else {
				AboutMessageWindow.createWindow("Внимание", "Не удалось найти составные элементы для данного прибора.\nПроведение поверки не возможно").show();
			}
		}
		catch(Exception exp) {
			
		}
		
	}
	
	@Override
	public void setDevice(Device device) {
		verificatedDevice = device;
	}
		
	//Закрыть	
	@FXML
	private void closeBtnClick(ActionEvent event) {
		Stage stage = (Stage) closeBtn.getScene().getWindow();
		stage.close();
	}
		
	public void waitResults(){		
		try {
			StopStatus status = new StopStatus();		
			ServerService wService = new ServerService(status);
			wService.start();
			status.waitMonitor();
			if (status.getStopStatus() == true) {
				fileReadBtnClick();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}				
	}	
	
}
