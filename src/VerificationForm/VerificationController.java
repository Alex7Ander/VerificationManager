package VerificationForm;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;

import AboutMessageForm.AboutMessageWindow;
import DevicePack.Device;
import DevicePack.Element;
import Exceptions.SavingException;
import FileManagePack.FileManager;
import GUIpack.InfoRequestable;
import GUIpack.Tables.Table;
import GUIpack.Tables.VerificationTable;
import ProtocolCreatePack.ProtocolCreateWindow;
import SearchDevicePack.SearchDeviceWindow;
import StartVerificationPack.StartVerificationController;
import StartVerificationPack.StartVerificationWindow;
import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.PhaseStandardizableCondition;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
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
	private VerificationProcedure verification = new VerificationProcedure();
//��������� �������
	private List<MeasResult> verificationResult;	
//������ �� ���������� ��
	public Device verificatedDevice;		
	private int currentElementIndex;
	private int currentParamIndex;
	
	private static int vswrAccuracy;
	private static int vswrErrorAccuracy;
	private static int phaseAccuracy;
	private static int phaseErrorAccuracy;
	
	static {
		FileInputStream fis;
        Properties property = new Properties();
        String propPath = new File(".").getAbsolutePath() + "/files/aksol.properties";
        try {       	
            fis = new FileInputStream(propPath);
            property.load(fis);

            vswrAccuracy = Integer.parseInt(property.getProperty("verification.vswrAccuracy"));
            vswrErrorAccuracy = Integer.parseInt(property.getProperty("varification.vswrErrorAccuracy"));
            phaseAccuracy = Integer.parseInt(property.getProperty("verification.phaseAccuracy"));
            phaseErrorAccuracy = Integer.parseInt(property.getProperty("verification.phaseErrorAccuracy"));
            
            System.out.println("vswrAccuracy: " + vswrAccuracy
                            + ", vswrAccuracy: " + vswrAccuracy
                            + ", phaseAccuracy: " + phaseAccuracy
                            + ", phaseErrorAccuracy: " + phaseErrorAccuracy);
            fis.close();

        } catch (IOException e) {
            System.err.println("������: ���� ������� �� ������ " + propPath + " ����������!");
            vswrAccuracy = 3;
            vswrErrorAccuracy = 3;
            phaseAccuracy = 2;
            phaseErrorAccuracy = 2;
        } catch (NumberFormatException nfExp) {
        	System.err.println("������: ������������ ������ ������ ��� ��������������");
            vswrAccuracy = 3;
            vswrErrorAccuracy = 3;
            phaseAccuracy = 2;
            phaseErrorAccuracy = 2;
        }
		
	}
	
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
	private void saveBtnClick(ActionEvent event) {
		try {
			saveResultsOfVerification();
		}
		catch(SavingException exp) {
			AboutMessageWindow.createWindow("������", exp.getMessage()).show();
		}		
	}
	
	//������� �� ������ �������� ���������
	@FXML
	private void createProtocolBtnClick(ActionEvent event) throws IOException {
		if (verificationResult.size() != 0) {
			if(!resultSaved) {
				try {
					saveResultsOfVerification();
					AboutMessageWindow.createWindow("�������", "���������� ������� ��������� � ��").show();
				}
				catch(SavingException exp) {
					AboutMessageWindow.createWindow("������", exp.getMessage()).show();
				}			
			}
			String[] docTypes = {"C������������ � �������", "��������� � �������������"};	
			createProtocol(docTypes);
		}
		else {
			AboutMessageWindow.createWindow("������", "��������� ������� ��� �� ���������").show();
		}
	}
	
	//��������� ���������� �����������
	private void saveResultsOfVerification() throws SavingException {
		if (verificationResult.size() != 0) {
			try {
				for (int i = 0; i < verificationResult.size(); i++) {
					verificationResult.get(i).saveInDB();
				}				
				resultSaved = true;
			}
			catch(SQLException sqlExp) {
				resultSaved = false;
				throw new SavingException("�� ������� ��������� ���������� � ��:\n" + sqlExp.getMessage());				
			}
		}
		else {
			AboutMessageWindow.createWindow("������","��������� ������� ��� �� ���������").show();
		}
		if(resultSaved) {
			saveBtn.setDisable(true);
		}
	}
	
	public void createProtocol(String[] docTypes) throws IOException {
		this.verification.setDeviceInformation(this.verificatedDevice);
		
		List<MeasResult> nominals = new ArrayList<>();
		List<ToleranceParametrs> protocoledModuleToleranceParams = new ArrayList<>(); //�������� � �������� ��������� �����������
		List<ToleranceParametrs> protocoledPhaseToleranceParams = new ArrayList<>();
		for (Element elm : this.verificatedDevice.includedElements) {			
			if (this.verification.isPrimary()) {
				nominals.add(elm.getNominal());
				protocoledModuleToleranceParams.add(elm.getPrimaryModuleToleranceParams());
				protocoledPhaseToleranceParams.add(elm.getPrimaryPhaseToleranceParams());
			}
			else {
				nominals.add(elm.getLastVerificationResult());
				protocoledModuleToleranceParams.add(elm.getPeriodicModuleToleranceParams());
				protocoledPhaseToleranceParams.add(elm.getPeriodicPhaseToleranceParams());
			}
		}
		
		this.verification.setShouldBeSavedInDBStatus(true);
		ProtocolCreateWindow.getProtocolCreateWindow(this.verificatedDevice,
													docTypes, 
													this.verificationResult, 
													nominals, 
													protocoledModuleToleranceParams, 
													protocoledPhaseToleranceParams,
													this.verification).show();
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
		this.currentParamIndex = this.parametrComboBox.getSelectionModel().getSelectedIndex(); 
		if (this.currentParamIndex != -1) {
			fillTable();			
		}
	}
//---------------------------	
	public void StartVerification() throws IOException {
		this.fileReadBtn.setDisable(false);
		this.startBtn.setDisable(true);
		this.searchDeviceBtn.setDisable(true);
		this.createProtocolBtn.setDisable(true);
		
		String absPath = new File(".").getAbsolutePath();
		//��������� ���������� �� ���������� �����
		//this.verification = new VerificationProcedure();
		
		StartVerificationController strtVerCtrl = (StartVerificationController)StartVerificationWindow.getStartVerificationWindow().getControllerClass();
		this.verification.setVerificationTimeType(strtVerCtrl.getVerificationiTimeType());
		this.verification.setStrTemperature(strtVerCtrl.getStrTemperatur());
		this.verification.setStrAirHumidity(strtVerCtrl.getStrAirHumidity());
		this.verification.setStrAtmPreasure(strtVerCtrl.getStrAtmPreasure());
		
		//�������� ����� psi.ini
		String psiFilePath = absPath + "\\measurement\\PSI.ini";
		this.verificatedDevice.createIniFile(psiFilePath);
		//������ ��������� measurement
		File file = new File(absPath + "\\measurement\\Project1.exe");
		Desktop.getDesktop().open(file);				
	}
	
	public void FinishVerificationBeforeMeasurment() throws IOException {
		StartVerificationController strtVerCtrl = (StartVerificationController)StartVerificationWindow.getStartVerificationWindow().getControllerClass();
		this.verification.setVerificationTimeType(strtVerCtrl.getVerificationiTimeType());
		this.verification.setStrTemperature(strtVerCtrl.getStrTemperatur());
		this.verification.setStrAirHumidity(strtVerCtrl.getStrAirHumidity());
		this.verification.setStrAtmPreasure(strtVerCtrl.getStrAtmPreasure());
		
		this.verification.setDeviceInformation(this.verificatedDevice);
		
		this.verification.setShouldBeSavedInDBStatus(true);
		String docTypes[] = {"��������� � �������������"};
		ProtocolCreateWindow.getProtocolCreateWindow(this.verificatedDevice,
													docTypes, 
													null, 
													null, 
													null, 
													null,
													this.verification).show();	
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

		//��������� �������� �������, ������� ����� ����������� � �������
		MeasResult nominals = null;
		if(verification.isPrimary()) {
			nominals = this.verificationResult.get(this.currentElementIndex).getMyOwner().getNominal();
		}
		else {
			nominals = this.verificationResult.get(this.currentElementIndex).getMyOwner().getLastVerificationResult();
		}
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
		
		//������ ������� � �������
		this.resultTable.setColumnFromDouble(0, fr);
							
		//���������� �������� ����/������ �
		String key1 = keys.get(this.currentParamIndex*4);
		List<Double> vswrColumn = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).values.get(key1));
		this.resultTable.setColumnFromDouble(1, vswrColumn, vswrAccuracy);
		
		//����������� ��������� ����/������ �
		String key2 = keys.get(1 + this.currentParamIndex*4);
		List<Double> column2 = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).values.get(key2));
		this.resultTable.setColumnFromDouble(2, column2, vswrErrorAccuracy);
		
		//�������� ���������� ������� ��� ����/������ �
		List<Double> column3 = Adapter.MapToArrayList(nominals.values.get(key1));	
		this.resultTable.setColumnFromDouble(3, column3, vswrAccuracy);
		
		//������ ������� ����������� ���� ������� � ����������
		List<Double> column4 = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).differenceBetweenNominal.get(key1));		
		this.resultTable.setColumnFromDouble(4, column4, vswrAccuracy);
		
		//������ �� ����/������ �
		List<String> column5 = new ArrayList<>();
		for (double currentFreq : this.verificationResult.get(currentElementIndex).freqs) {
			String currentKey = keys.get(this.currentParamIndex*4); 
			String text = Double.toString(
					representableModuleToleranceParams.values.get("DOWN_" + currentKey).get(currentFreq));
			text += "/";
			text += representableModuleToleranceParams.values.get("UP_" +  currentKey).get(currentFreq);
			column5.add(text);
		}
		this.resultTable.setColumn(5, column5);
				
		//������� �������� �� ������
		List<String> column6 = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).suitabilityDecision.get(key1));
		this.resultTable.setColumn(6, column6);
				
		//������� �������, ��� ������� ����������� ��������� ���� ����������� ���������
		Predicate<Double> std = PhaseStandardizableCondition.getStandardizableCondition(this.verificationResult.get(this.currentElementIndex).getMyOwner());		
		//���������� �������� ����
		String key7 = keys.get(2 + this.currentParamIndex*4);
		List<Double> phaseColumn = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).values.get(key7));
		this.resultTable.setColumnFromDoubleWithCondition(7, phaseColumn, vswrColumn, phaseAccuracy, std); //setColumnFromDouble(7, phaseColumn, phaseAccuracy);
		
		//����������� ��������� ����
		String key8 = keys.get(3 + this.currentParamIndex*4);
		List<Double> column8 = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).values.get(key8));
		this.resultTable.setColumnFromDoubleWithCondition(8, column8, vswrColumn, phaseErrorAccuracy, std);
		
		//�������� ���������� �������		
		List<Double> column9 = Adapter.MapToArrayList(nominals.values.get(key7));
		this.resultTable.setColumnFromDoubleWithCondition(9, column9, vswrColumn, phaseAccuracy, std);
		
		//������ ������� ����������� ���� ������� � ���������� ��� ����
		List<Double> column10 = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).differenceBetweenNominal.get(key7));
		this.resultTable.setColumnFromDoubleWithCondition(10, column10, vswrColumn, phaseAccuracy, std);
		
		//������ �� ����
		List<String> column11 = new ArrayList<>();
		for (double currentFreq : this.verificationResult.get(currentElementIndex).freqs) {
			String currentKey = keys.get(2 + this.currentParamIndex*4);
			String text = Double.toString(
					representablePhaseToleranceParams.values.get("DOWN_" + currentKey).get(currentFreq));
			text += "/";
			text += representablePhaseToleranceParams.values.get("UP_" + currentKey).get(currentFreq);
			column11.add(text);
		}
		this.resultTable.setColumnWithCondition(11, column11, vswrColumn, std);
				
		//������� �������� �� ����
		List<String> column12 = Adapter.MapToArrayList(
				this.verificationResult.get(this.currentElementIndex).suitabilityDecision.get(key7));
		this.resultTable.setColumnWithCondition(12, column12, vswrColumn, std);
		
		//������������ ��������� � ������� ��� �������� � �������� � ������������ ���������
		//��� ������
		String newModuleResHeader = parametrComboBox.getSelectionModel().getSelectedItem();				
		String newModuleToleranceHeader = null;
		String newModuleErrorHeader = null;		
		String differenceModuleHeader = null;
		
		//���� ���������� S12 ��� S21
		if(this.currentParamIndex == 1 || this.currentParamIndex == 2) {
			newModuleToleranceHeader = "������, ��";
			differenceModuleHeader = "\u0394, ��";
		}
		else{
			//��� S11 � S22 ����� ��������, � ��� ��������� ������ 
			if (this.verificatedDevice.includedElements.get(currentElementIndex).getModuleToleranceType().equals("percent")) {
				newModuleToleranceHeader = "������, %";	
				differenceModuleHeader = "\u03B4, %";			
			}
			else {
				newModuleToleranceHeader = "������";
				differenceModuleHeader = "\u0394";
			}	
		}
	
		if(newModuleResHeader.contains("����")) 
			newModuleErrorHeader = "�����������, %";
		else 
			newModuleErrorHeader = "�����������";		
		this.resultTable.setHead(1, newModuleResHeader);
		this.resultTable.setHead(2, newModuleErrorHeader);
		this.resultTable.setHead(3, "����. ���.\n" + newModuleResHeader);
		this.resultTable.setHead(4, differenceModuleHeader);
		this.resultTable.setHead(5, newModuleToleranceHeader);
		
		
		//� ������ ��� ����		
		String newPhaseToleranceHeader = null;
		String differencePhaseHeader = null;
		if (this.verificatedDevice.includedElements.get(this.currentElementIndex).getPhaseToleranceType().equals("percent")) {
			newPhaseToleranceHeader = "������, % (�������)";
			differencePhaseHeader = "\u03B4, %";			
		}
		else {
			newPhaseToleranceHeader = "������, \u00B0";
			differencePhaseHeader = "\u0394, \u00B0";			
		}
		this.resultTable.setHead(8, "�����������, \u00B0");
		this.resultTable.setHead(10, differencePhaseHeader);
		this.resultTable.setHead(11, newPhaseToleranceHeader);		
	}	
	
	/*
	private Standardizable getStandardizableCondition() {
		Standardizable std = null;
		if(this.verificationResult.get(this.currentElementIndex).getMyOwner().getMeasUnit().equals("vswr")) {
			std = (double currentModuleValue) -> {
				if(currentModuleValue >= 1.05) {
					return true;
				}
				else {
					return false;
				}
			};
			
		}
		else {
			std = (double currentModuleValue) -> {
				if(currentModuleValue >= 1.05) {
					return true;
				}
				else {
					return false;
				}
			};
		}
		return std;
	}
	*/
//---------------------------
	@SuppressWarnings("unused")
	private int getAccuracy(List<Double> values) {
		int accuracy = 0;
		for (Double value: values) {
			String textValue = Double.toString(value);
			int fractionalLength = textValue.substring(textValue.lastIndexOf('.')).length();
			if (fractionalLength > accuracy)
				accuracy = fractionalLength;
		}
		return accuracy;
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
				Element currentElement = this.verificatedDevice.includedElements.get(i);
				String absPath = new File(".").getAbsolutePath();
				String resFilePath = absPath + "\\measurement\\protokol.ini";
				MeasResult rs = new MeasResult(resFilePath, i+1, currentElement);				
				verificationResult.add(rs);
				//Verificating results
				if (verification.isPrimary()) {
					verificatedDevice.includedElements.get(i).getPrimaryModuleToleranceParams().checkResult(currentElement.getNominal(), rs);
					verificatedDevice.includedElements.get(i).getPrimaryPhaseToleranceParams().checkResult(currentElement.getNominal(), rs);
				}
				else {
					MeasResult lastVerRes = currentElement.getLastVerificationResult();
					verificatedDevice.includedElements.get(i).getPeriodicModuleToleranceParams().checkResult(lastVerRes, rs);
					verificatedDevice.includedElements.get(i).getPeriodicPhaseToleranceParams().checkResult(lastVerRes, rs);
				}				
				elementComboBox.setValue(listOfElements.get(0));
				parametrComboBox.setValue(listOfParametrs.get(0));
			}
			saveBtn.setDisable(false);
			resultSaved = false;
			
			this.createProtocolBtn.setDisable(false);
			this.startBtn.setDisable(false);
			this.startBtn.setText("��������� ���������");
			this.fileReadBtn.setDisable(true);
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
		this.startBtn.setDisable(false);
		this.fileReadBtn.setDisable(true);
		verificatedDevice = device;
	}
		
	//�������	
	@FXML
	private void closeBtnClick(ActionEvent event) {		
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
	
	public void zeroingLastVerificationsId() {
		if(this.verificatedDevice == null) {
			return;
		}
		System.out.println("�� ���������� ������� ��������� ��������� ������� ������ ��������� �������:");
		for (Element elm : this.verificatedDevice.includedElements) {
			try {
				elm.setLastVerificationId(0);
				System.out.println("- " + elm.getType() + " " + elm.getSerialNumber() + " - �������");
			} catch (SQLException sqlExp) {
				System.err.println("- " + elm.getType() + " " + elm.getSerialNumber() + " - ������: " + sqlExp.getMessage());
			}
		}
	}
}
