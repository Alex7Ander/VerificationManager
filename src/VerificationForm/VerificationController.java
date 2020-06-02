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
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationPack.MeasResult;
import VerificationPack.VerificationProcedure;
import YesNoDialogPack.YesNoWindow;
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
	private boolean resultSaved;
	private Stage myStage;
	
	public void setStage(Stage stage) {
		myStage = stage;
	}
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
		this.listOfElements = FXCollections.observableArrayList();
		this.listOfParametrs = FXCollections.observableArrayList();		
		this.verificationResult = new ArrayList<MeasResult>();
		this.currentElementIndex = 0;
		this.currentParamIndex = 0;		   		
		this.resultTable = new VerificationTable(tablePane);
		this.resultSaved = true;
		this.saveBtn.setDisable(true);
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
				resultSaved = true;
			}
			catch(SQLException sqlExp) {
				AboutMessageWindow.createWindow("Ошибка", "Не удалось сохранить результаты в БД");
				resultSaved = false;
			}
		}
		else {
			AboutMessageWindow.createWindow("Ошибка","Процедура поверки еще не закончена").show();
		}
		if(resultSaved) {
			saveBtn.setDisable(true);
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
		
		List<MeasResult> nominals = new ArrayList<>();
		List<ToleranceParametrs> protocoledModuleToleranceParams = new ArrayList<>(); //Вносимые в протокол параметры пригодности
		List<ToleranceParametrs> protocoledPhaseToleranceParams = new ArrayList<>();
		for (Element elm : this.verificatedDevice.includedElements) {
			nominals.add(elm.getNominal());
			if (this.verification.isPrimary()) {
				protocoledModuleToleranceParams.add(
						this.verificationResult.get(this.currentElementIndex).getMyOwner().getPrimaryModuleToleranceParams());
				protocoledPhaseToleranceParams.add(
						this.verificationResult.get(this.currentElementIndex).getMyOwner().getPrimaryPhaseToleranceParams());	
			}
			else {
				protocoledModuleToleranceParams.add(
						this.verificationResult.get(this.currentElementIndex).getMyOwner().getPeriodicModuleToleranceParams());
				protocoledPhaseToleranceParams.add(
						this.verificationResult.get(this.currentElementIndex).getMyOwner().getPeriodicPhaseToleranceParams());
			}
		}

		ProtocolCreateWindow.getProtocolCreateWindow(docTypes, 
													this.verificationResult, 
													nominals, 
													protocoledModuleToleranceParams, 
													protocoledPhaseToleranceParams,
													this.verification).show();
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
		this.currentParamIndex = this.parametrComboBox.getSelectionModel().getSelectedIndex(); 
		if (this.currentParamIndex != -1) {
			fillTable();			
		}
	}
//---------------------------	
	public void StartVerification() throws IOException {
		String absPath = new File(".").getAbsolutePath();
		//Получение информации об окружающей среде
		this.verification = new VerificationProcedure();
		this.verification.setPrimaryInformation((StartVerificationController)StartVerificationWindow.getStartVerificationWindow().getControllerClass());
		//Создание файла psi.ini
		String psiFilePath = absPath + "\\measurement\\PSI.ini";
		this.verificatedDevice.createIniFile(psiFilePath);
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

		List<Double> fr = this.verificationResult.get(this.currentElementIndex).freqs;
		int countOfFreq = fr.size();
		if (this.resultTable.getRowCount() < countOfFreq) {
			while (this.resultTable.getRowCount() != countOfFreq) 
				this.resultTable.addRow();
		}
		else if (this.resultTable.getRowCount() > countOfFreq) {
			while (this.resultTable.getRowCount() != countOfFreq) 
				this.resultTable.deleteRow(this.resultTable.getRowCount());
		}

		//Параметры годности прибора, который будут указываться в таблице
		MeasResult nominals = this.verificationResult.get(this.currentElementIndex).getMyOwner().getNominal();
		ToleranceParametrs representableModuleToleranceParams = null;
		ToleranceParametrs representablePhaseToleranceParams = null;
		if (this.verification.isPrimary()) {
			representableModuleToleranceParams = this.verificationResult.get(this.currentElementIndex).getMyOwner().getPrimaryModuleToleranceParams();
			representablePhaseToleranceParams = this.verificationResult.get(this.currentElementIndex).getMyOwner().getPrimaryPhaseToleranceParams();	
		}
		else {
			representableModuleToleranceParams = this.verificationResult.get(this.currentElementIndex).getMyOwner().getPeriodicModuleToleranceParams();
			representablePhaseToleranceParams = this.verificationResult.get(this.currentElementIndex).getMyOwner().getPeriodicPhaseToleranceParams();
		}
		
		//Вносим частоты в таблицу
		this.resultTable.setColumnFromDouble(0, fr);
		
		//Измеренный значения модуля
		List<Double> column1 = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).values.get(keys.get(this.currentParamIndex*4)));
		this.resultTable.setColumnFromDouble(1, column1);
		
		//Значения предыдущей поверки
		List<String> column2 = new ArrayList<>();
		for (double currentFreq : this.verificationResult.get(currentElementIndex).freqs) {
			String currentKey = keys.get(this.currentParamIndex*4); 
			String text = Double.toString(nominals.values.get(currentKey).get(currentFreq));
			column2.add(text);
		}		
		this.resultTable.setColumn(2, column2);
		
		//Допуск на КСВН/Г
		List<String> column3 = new ArrayList<>();
		for (double currentFreq : this.verificationResult.get(currentElementIndex).freqs) {
			String currentKey = keys.get(this.currentParamIndex*4); 
			String text = Double.toString(
					representableModuleToleranceParams.values.get("DOWN_" + currentKey).get(currentFreq));
			text += "/";
			text += representableModuleToleranceParams.values.get("UP_" +  currentKey).get(currentFreq);
			column3.add(text);
		}
		this.resultTable.setColumn(3, column3);
		
		//Погрешность измерения модуля
		List<Double> column4 = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).values.get(keys.get(1 + this.currentParamIndex*4)));
		this.resultTable.setColumnFromDouble(4, column4);
		
		//Решение годности по модолю
		List<String> column5 = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).suitabilityDecision.get(keys.get(this.currentParamIndex)));
		this.resultTable.setColumn(5, column5);
						
		//Измеренные значения фазы
		List<Double> column6 = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).values.get(keys.get(2 + this.currentParamIndex*4)));
		this.resultTable.setColumnFromDouble(6, column6);
		
		//Значения предыдущей поверки
		List<String> column7 = new ArrayList<>();
		for (double currentFreq : this.verificationResult.get(currentElementIndex).freqs) {
			String currentKey = keys.get(2 + this.currentParamIndex*4);
			String text = Double.toString(nominals.values.get(currentKey).get(currentFreq));
			column7.add(text);
		}
		this.resultTable.setColumn(7, column7);
		
		//Допуск по фазе
		List<String> column8 = new ArrayList<>();
		for (double currentFreq : this.verificationResult.get(currentElementIndex).freqs) {
			String currentKey = keys.get(2 + this.currentParamIndex*4);
			String text = Double.toString(
					representablePhaseToleranceParams.values.get("DOWN_" + currentKey).get(currentFreq));
			text += "/";
			text += representablePhaseToleranceParams.values.get("UP_" + currentKey).get(currentFreq);
			column8.add(text);
		}
		this.resultTable.setColumn(8, column8);
		
		//Погрешность измерения фазы
		List<Double> column9 = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).values.get(keys.get(3 + this.currentParamIndex*4)));
		this.resultTable.setColumnFromDouble(9, column9);
				
		//Решения годности по фазе
		List<String> column10 = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).suitabilityDecision.get(keys.get(2 + this.currentParamIndex)));
		this.resultTable.setColumn(10, column10);
		
		//Переписываем заголовки у таблицы для столбцов с допуском и погрешностью измерения
		//для модуля
		String newModuleResHeader = parametrComboBox.getSelectionModel().getSelectedItem();
				
		String newModuleToleranceHeader = null;
		String newModuleErrorHeader = null;	
		
		if (this.verificatedDevice.includedElements.get(currentElementIndex).getModuleToleranceType().equals("percent"))
			newModuleToleranceHeader = "Допуск, %";					 
		else 
			newModuleToleranceHeader = "Допуск";			
		
		if(newModuleResHeader.contains("КСВН")) 
			newModuleErrorHeader = "Погрешность, %";
		else 
			newModuleErrorHeader = "Погрешность";
		
		this.resultTable.setHead(1, newModuleResHeader);
		this.resultTable.setHead(2, "Пред. пов.\n" + newModuleResHeader);
		this.resultTable.setHead(3, newModuleToleranceHeader);
		this.resultTable.setHead(4, newModuleErrorHeader);
		//а теперь для фазы		
		String newPhaseToleranceHeader = null;
		if (this.verificatedDevice.includedElements.get(this.currentElementIndex).getPhaseToleranceType().equals("percent"))
			newPhaseToleranceHeader = "Допуск, % (номинал)";
		else
			newPhaseToleranceHeader = "Допуск, \u00B0";
		this.resultTable.setHead(8, newPhaseToleranceHeader);
		this.resultTable.setHead(9, "Погрешность, \u00B0");
	}	
//---------------------------	
	@FXML
	private Button fileReadBtn;
	@FXML
	public void fileReadBtnClick() {
		if (verification == null) {
			return;
		}
		verificationResult.clear();
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
			saveBtn.setDisable(false);
			resultSaved = false;
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
		/*
		if (!resultIsSaved()) {
			int answer = YesNoWindow.createYesNoWindow("Результаты поверки не сохранены", "Результаты поверки не сохранениы в БД.\nВы уверены что хотите завершить поверку\nбез сохранения результатов?").showAndWait();
			if (answer == 1) {
				return;
			}
		}	
		*/
		myStage.close();
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
	
	public boolean resultIsSaved() {
		return resultSaved;
	}
	
}
