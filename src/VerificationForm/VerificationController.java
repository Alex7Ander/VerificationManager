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
//������� � ������������	
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private AnchorPane tablePane;
	private Table resultTable;

//��������� �������
	VerificationProcedure verification;
//��������� �������
	private ArrayList<MeasResult> verificationResult;	
//������ �� ���������� ��
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
		resultTable = new VerificationTable(tablePane);
		resultSaved = false;
		saveBtn.setDisable(true);
	}
	
	@FXML
	private void searchDeviceBtnClick(ActionEvent event) throws IOException {
		SearchDeviceWindow.getSearchDeviceWindow(verificatedDevice, this).show();
	}

//������� �� ������ Start
	@FXML
	private void startBtnClick(ActionEvent event) throws IOException, InterruptedException {
		if(verificatedDevice != null) {
			StartVerificationWindow.getStartVerificationWindow().show();						
		}
		else{
			AboutMessageWindow.createWindow("������","�� �� ������� �������� ��������� ��� �������").show();
		}
	}
	
//������� �� ������ ���������
	@FXML
	private void saveBtnClick(ActionEvent event) throws IOException {
		if (verificationResult.size() != 0) {
			try {
				for (int i = 0; i < verificationResult.size(); i++) {
					verificationResult.get(i).saveInDB();
				}
				AboutMessageWindow.createWindow("�������", "���������� ������� ��������� � ��").show();
				resultSaved = true;
			}
			catch(SQLException sqlExp) {
				AboutMessageWindow.createWindow("������", "�� ������� ��������� ���������� � ��");
				resultSaved = false;
			}
		}
		else {
			AboutMessageWindow.createWindow("������","��������� ������� ��� �� ���������").show();
		}
		if(resultSaved) {
			saveBtn.setDisable(true);
		}
	}
	
//������� �� ������ �������� ���������
	@FXML
	private void createProtocolBtnClick(ActionEvent event) throws IOException {
		if (verificationResult.size() != 0) {
			String[] docTypes = {"C������������ � �������", "��������� � �������������"};	
			createProtocol(docTypes);
		}
		else {
			AboutMessageWindow.createWindow("������","��������� ������� ��� �� ���������").show();
		}
	}
	public void createProtocol(String[] docTypes) throws IOException {
		this.verification.setDeviceInformation(this.verificatedDevice);
		ProtocolCreateWindow.getProtocolCreateWindow(docTypes, verificationResult, this.verification).show();
	}
//�������� �������� � ���������� �� ������� ���������	
	@FXML
	private void elementComboBoxChange() {
		
		//������� ������ ������������� ��������
		currentElementIndex = this.elementComboBox.getSelectionModel().getSelectedIndex();
		
		//��������� ������ ���������� � ������������ ���������� ������� � ��������
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
		//��������� ������ ������������� ��������� � 0 
		currentParamIndex = 0;
		//� ���������� ������� ���������� ��� �������� 0
		parametrComboBox.getSelectionModel().select(currentParamIndex);
		//��� �������� � ������ ������� parametrComboBoxChange()  //fillTable();
	}
//�������� �������� � ���������� � �����������	
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
		//��������� ���������� �� ���������� �����
		verification = new VerificationProcedure();
		verification.setPrimaryInformation((StartVerificationController)StartVerificationWindow.getStartVerificationWindow().getControllerClass());
		//�������� ����� psi.ini
		String psiFilePath = absPath + "\\measurement\\PSI.ini";
		verificatedDevice.createIniFile(psiFilePath);
		//������ ��������� measurement
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
			newPhaseErrorHeader = "�����������, %";
		} 
		else {
			newPhaseErrorHeader = "�����������, \u00B0";
		}
		resultTable.setHead(5, newPhaseErrorHeader);
		
		String newModuleErrorHeader = null;
		if (verificatedDevice.includedElements.get(currentElementIndex).getModuleToleranceType().equals("percent")) {
			newModuleErrorHeader = "�����������, %";
		} 
		else {
			newModuleErrorHeader = "�����������";
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
			saveBtn.setDisable(false);
		}
		catch(IOException ioExp) {
			AboutMessageWindow.createWindow("������", "����������� ��� ��������� ����\n � ������������ ���������").show();
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
				AboutMessageWindow.createWindow("��������", "�� ������� ����� ��������� �������� ��� ������� �������.\n���������� ������� �� ��������").show();
			}
		}
		catch(Exception exp) {
			
		}
		
	}
	
	@Override
	public void setDevice(Device device) {
		verificatedDevice = device;
	}
		
	//�������	
	@FXML
	private void closeBtnClick(ActionEvent event) {			
		if (!resultIsSaved()) {
			int answer = YesNoWindow.createYesNoWindow("���������� ������� �� ���������", "���������� ������� �� ���������� � ��.\n�� ������� ��� ������ ��������� �������\n��� ���������� �����������?").showAndWait();
			if (answer == 1) {
				return;
			}
		}	
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
		return this.resultSaved;
	}
	
}
