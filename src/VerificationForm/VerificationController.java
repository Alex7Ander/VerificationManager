package VerificationForm;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import AboutMessageForm.AboutMessageWindow;
import DevicePack.Device;
import DevicePack.Element;
import FileManagePack.FileManager;
import GUIpack.InfoRequestable;
import GUIpack.StringGridFXPack.StringGridPosition;
import GUIpack.StringGridFXPack.VerificationStringGridFX;
import ProtocolCreatePack.ProtocolCreateWindow;
import SearchDevicePack.SearchDeviceWindow;
import StartVerificationPack.StartVerificationController;
import StartVerificationPack.StartVerificationWindow;
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
	private VerificationStringGridFX resultTable;

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
		StringGridPosition position = new StringGridPosition(1110, 100, scrollPane, tablePane);
		resultTable = new VerificationStringGridFX( position);		 
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
			AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка","Вы не выбрали средство измерения для поверки");
			msgWin.show();
		}
	}
	
//Нажатие на кнопку сохранить
	@FXML
	private void saveBtnClick(ActionEvent event) throws IOException {
		if (verificationResult.size() != 0) {
			for (int i=0; i<verificationResult.size(); i++) {
				try {
					verificationResult.get(i).saveInDB();
					AboutMessageWindow msgWin = new AboutMessageWindow("Успешно", "Результаты поверки сохранены в БД");
					msgWin.show();
				}
				catch(SQLException sqlExp) {
					AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Не удалось сохранить результаты в БД");
					msgWin.show();
				}
			}
		}
		else {
			AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка","Процедура поверки еще не закончена");
			msgWin.show();
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
			AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка","Процедура поверки еще не закончена");
			msgWin.show();
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
		this.parametrComboBox.setItems(listOfParametrs);
		//Установим индекс отображаемого параметра в 0 
		currentParamIndex = 0;
		//И программно выберем параметром под индексом 0
		this.parametrComboBox.getSelectionModel().select(currentParamIndex);
		//Это приведет к вызову функции parametrComboBoxChange()  //fillTable();
	}
//Изменить значение в комбобоксе с параметрами	
	@FXML
	private void parametrComboBoxChange() {
		currentParamIndex = this.parametrComboBox.getSelectionModel().getSelectedIndex(); 
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
		String psiFilePath = absPath + "\\measurment\\PSI.ini";
		verificatedDevice.createIniFile(psiFilePath);
			
		//Запуск программы measurment
		File file =new File(absPath + "\\measurment\\Project1.exe");
		Desktop.getDesktop().open(file);				
	}
		
	private void fillTable() {
		String keys[] = {"m_S11", "err_m_S11", "p_S11", "err_p_S11", 
					 	 "m_S12", "err_m_S12", "p_S12", "err_p_S12",
					 	 "m_S21", "err_m_S21", "p_S21", "err_p_S21", 
					 	 "m_S22", "err_m_S22", "p_S22", "err_p_S22"};
			
		ArrayList<Double> fr = this.verificationResult.get(currentElementIndex).freqs;
		int countOfFreq = fr.size();
		if (this.resultTable.getRowCount() < countOfFreq) {
			while (this.resultTable.getRowCount() != countOfFreq) 
				this.resultTable.addRow();
		}
		else if (this.resultTable.getRowCount() > countOfFreq) {
			while (this.resultTable.getRowCount() != countOfFreq) 
				this.resultTable.deleteRow(this.resultTable.getRowCount());
		}
			  
		this.resultTable.setColumnFromDouble(0, fr);
			
		ArrayList<Double> column1 = Adapter.HashMapToArrayList(this.verificationResult.get(currentElementIndex).values.get(keys[currentParamIndex*4]));
		this.resultTable.setColumnFromDouble(1, column1);
			
		ArrayList<Double> column2 = Adapter.HashMapToArrayList(this.verificationResult.get(currentElementIndex).values.get(keys[1 + currentParamIndex*4]));
		this.resultTable.setColumnFromDouble(2, column2);
					
		ArrayList<Double> column4 = Adapter.HashMapToArrayList(this.verificationResult.get(currentElementIndex).values.get(keys[2 + currentParamIndex*4]));
		this.resultTable.setColumnFromDouble(4, column4);
		
		ArrayList<Double> column5 = Adapter.HashMapToArrayList(this.verificationResult.get(currentElementIndex).values.get(keys[3 + currentParamIndex*4]));
		this.resultTable.setColumnFromDouble(5, column5);
			
		ArrayList<String> column3 = Adapter.HashMapToArrayList(this.verificationResult.get(currentElementIndex).suitabilityDecision.get(keys[currentParamIndex])); 
		this.resultTable.setColumn(3, column3);
			
		ArrayList<String> column6 = Adapter.HashMapToArrayList(this.verificationResult.get(currentElementIndex).suitabilityDecision.get(keys[2 + currentParamIndex])); 
		this.resultTable.setColumn(6, column6);
	}	
//---------------------------	
	@FXML
	private Button fileReadBtn;
	@FXML
	public void fileReadBtnClick() {
		if (this.verification == null) {
			return;
		}
		
		this.verificationResult.clear();
		int countOfRes = this.verificatedDevice.getCountOfElements();
		try {		
			for (int i=0; i<countOfRes; i++) {
				String absPath = new File(".").getAbsolutePath();
				String resFilePath = absPath + "\\measurment\\protocol.ini";

				MeasResult rs = new MeasResult(resFilePath, i+1, this.verificatedDevice.includedElements.get(i));				
				this.verificationResult.add(rs);				
				if (this.verification.getTypeByTime().equals("primary")) {
					//Проверка результатво
					//this.verificatedDevice.includedElements.get(i).getPrimaryToleranceParams().checkResult(rs);
				}
				else {
					//Проверка результатов
					//this.verificatedDevice.includedElements.get(i).getPeriodicToleranceParams().checkResult(rs);
				}							
				//Заполним таблицу
				//fillTable();
			}
		}
		catch(IOException ioExp) {
			try {
				AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Отсутствует или поврежден файл\n с результатами измерений");
				msgWin.show();
			}
			catch(Exception exp) {
				//
			}
		}
	}
//----------------------------------------	
	@Override
	public void representRequestedInfo() {
		this.nameLabel.setText(this.verificatedDevice.getName());
		this.typeLabel.setText(this.verificatedDevice.getType());
		this.serNumLabel.setText(this.verificatedDevice.getSerialNumber());
		int countOfElements = this.verificatedDevice.getCountOfElements();	
		try {
			if (countOfElements > 0) {
				listOfElements.clear();
				for(int i = 0; i < this.verificatedDevice.getCountOfElements(); i++) {
					Element currentEl = this.verificatedDevice.includedElements.get(i);
					String elementItem = currentEl.getType() + " " + currentEl.getSerialNumber();
					listOfElements.add(elementItem);
				}
				this.elementComboBox.setItems(listOfElements);
			}
			else {
				AboutMessageWindow msgWin = new AboutMessageWindow("Внимание", "Не удалось найти составные элементы для данного прибора.\nПроведение поверки не возможно");
				msgWin.show();
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
