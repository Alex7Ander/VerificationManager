package VerificationForm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import AboutMessageForm.AboutMessageWindow;
import DevicePack.Device;
import DevicePack.Element;
import FileManagePack.FileManager;
import GUIpack.InfoRequestable;
import GUIpack.StringGridFX;
import SearchDevicePack.SearchDeviceWindow;
import StartVerificationPack.StartVerificationWindow;
import VerificationPack.Gamma_Result;
import VerificationPack.MeasResult;
import VerificationPack.VSWR_Result;
import _tempHelpers.Adapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
	private ComboBox elementComboBox;
	private ObservableList<String> listOfElements;
	@FXML
	private ComboBox parametrComboBox;
	private ObservableList<String> listOfParametrs;
	
//������� � ������������	
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private AnchorPane tablePane;
	private StringGridFX resultTable;
	private ArrayList<String> tableHeads;
	private ArrayList<String> modulColumn;
	private ArrayList<String> modulErrorColumn;
	private ArrayList<String> modulSolutColumn;
	private ArrayList<String> phaseColumn;
	private ArrayList<String> phaseErrorColumn;
	private ArrayList<String> phaseSolutColumn;
	
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
		
		tableHeads = new ArrayList<String>();
		modulErrorColumn = new ArrayList<String>();
		modulSolutColumn = new ArrayList<String>();
		phaseColumn = new ArrayList<String>();
		phaseErrorColumn = new ArrayList<String>();
		phaseSolutColumn = new ArrayList<String>();
		
		currentElementIndex = 0;
		currentParamIndex = 0;
		
		resultTable = new StringGridFX(7, 10, 1110, 100, scrollPane, tablePane);		 
	}
	
	@FXML
	private void searchDeviceBtnClick(ActionEvent event) throws IOException {
		SearchDeviceWindow.getSearchDeviceWindow(verificatedDevice, this).show();
	}
	
	@FXML
	private void startBtnClick(ActionEvent event) throws IOException {
		if(verificatedDevice != null) {
			String absPath = new File(".").getAbsolutePath();
			String psiFilePath = absPath + "\\measurment\\PSI.ini";
			verificatedDevice.createIniFile(psiFilePath);
			StartVerificationWindow.getVerificationWindow().show();
		}
		else{
			AboutMessageWindow msgWin = new AboutMessageWindow("������","�� �� ������� �������� ��������� ��� �������");
			msgWin.show();
		}
	}
	
//������� �� ������ ���������
	@FXML
	private void saveBtnClick(ActionEvent event) throws IOException {
		if (verificationResult.size() != 0) {
			//
		}
		else {
			AboutMessageWindow msgWin = new AboutMessageWindow("������","��������� ������� ��� �� ���������");
			msgWin.show();
		}
	}
	
//��������� ������ �������� ���������
	@FXML
	private void createProtocolBtnClick(ActionEvent event) throws IOException {
		if (verificationResult.size() != 0) {
			//
		}
		else {
			AboutMessageWindow msgWin = new AboutMessageWindow("������","��������� ������� ��� �� ���������");
			msgWin.show();
		}
	}
	
	@FXML
	private void elementComboBoxChange() {
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
		this.parametrComboBox.getSelectionModel().select(0);
		
		currentParamIndex = 0;
		currentElementIndex = this.elementComboBox.getSelectionModel().getSelectedIndex();
		fillTable();
	}
	
	@FXML
	private void parametrComboBoxChange() {
		currentParamIndex = this.parametrComboBox.getSelectionModel().getSelectedIndex(); 
		fillTable();
	}
	
//---------------------------	
	@FXML
	private Button fileReadBtn;
	@FXML
	private void fileReadBtnClick() {
		int countOfRes = this.verificatedDevice.getCountOfElements();
		try {
			for (int i=0; i<countOfRes; i++) {
				String absPath = new File(".").getAbsolutePath();
				String resFilePath = absPath + "\\measurment\\protocol.ini";

				MeasResult rs = new MeasResult(resFilePath, i+1, this.verificatedDevice.includedElements.get(i));

				this.verificationResult.add(rs);
				fillTable();
			}
		}
		catch(IOException ioExp) {
			try {
				AboutMessageWindow msgWin = new AboutMessageWindow("������", "����������� ��� ��������� ����\n � ������������ ���������");
				msgWin.show();
			}
			catch(Exception exp) {
				//
			}
		}
	}
//---------------------------
	private void fillTable() {
		String keys[] = {"m_S11", "er_m_S11", "p_S11", "er_p_S11", 
				 		 "m_S12", "er_m_S12", "p_S12", "er_p_S12",
				 		 "m_S21", "er_m_S21", "p_S21", "er_p_S21", 
				 		 "m_S22", "er_m_S22", "p_S22", "er_p_S22"};
		
		ArrayList<Double> fr = this.verificationResult.get(currentElementIndex).freqs;
		int countOfFreq = fr.size();
		if (this.resultTable.getRowCount() < countOfFreq) {
			while (this.resultTable.getRowCount() != countOfFreq) this.resultTable.addRow();
		}
		else if (this.resultTable.getRowCount() > countOfFreq) {
			while (this.resultTable.getRowCount() != countOfFreq) this.resultTable.deleteRow(this.resultTable.getRowCount());
		}
		
		this.resultTable.setColumnFromDouble(0, fr);
		
		ArrayList<Double> column1 = Adapter.HashMapToArrayList(fr, this.verificationResult.get(currentElementIndex).values.get(keys[currentParamIndex]));
		this.resultTable.setColumnFromDouble(1, column1);
		
		ArrayList<Double> column2 = Adapter.HashMapToArrayList(fr, this.verificationResult.get(currentElementIndex).values.get(keys[1 + currentParamIndex]));
		this.resultTable.setColumnFromDouble(2, column2);
				
		ArrayList<Double> column4 = Adapter.HashMapToArrayList(fr, this.verificationResult.get(currentElementIndex).values.get(keys[2 + currentParamIndex]));
		this.resultTable.setColumnFromDouble(4, column4);
		
		ArrayList<Double> column5 = Adapter.HashMapToArrayList(fr, this.verificationResult.get(currentElementIndex).values.get(keys[3 + currentParamIndex]));
		this.resultTable.setColumnFromDouble(5, column5);
	}
//---------------------------	
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
				AboutMessageWindow msgWin = new AboutMessageWindow("��������", "�� ������� ����� ��������� �������� ��� ������� �������.\n���������� ������� �� ��������");
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
	
	//�������	
		@FXML
		private void closeBtnClick(ActionEvent event) {
			try {
				Stage stage = (Stage) closeBtn.getScene().getWindow();
				stage.close();
			}
			catch(Exception exp){
				//
			}
		}
	
	
}
