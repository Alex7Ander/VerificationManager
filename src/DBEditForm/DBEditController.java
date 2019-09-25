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
import SearchDevicePack.SearchDeviceWindow;
import ToleranceParamPack.ToleranceParametrs;
import VerificationPack.MeasResult;
import _tempHelpers.Adapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DBEditController implements InfoRequestable {

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
			String item = elm.getType() + " �" + elm.getSerialNumber();
			this.elementsList.add(item);
		}
		this.elementsListView.setItems(elementsList);		
	}
	
//����� ����� ����
	@FXML
	private Button passBtn;	
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
	private ObservableList<String> elementsList;
//---------------------------------------------

//������ ����� ����
	//�����
	@FXML
	private ComboBox<String> editedPropertyComboBox;
	private ObservableList<String> propertiesList;
	@FXML
	private StackPane propertiesStack;
	//---------------------------------
	//�������������� ���������� ��������
	@FXML
	private VBox paramsBox;
	//������� ��� ����������
	@FXML
	private ScrollPane paramsScrollPane;
	@FXML
	private AnchorPane paramsTablePane;
	private StringGridFX paramsTable;
	@FXML
	private Pane paramsBtnPane;
	//------------------------------
	//�������� ����������� ���������
	@FXML
	private VBox resultBox;
	@FXML
	private ComboBox<String> verificationSecondParametrComboBox;
	private ObservableList<String> verificationSecondParametrList;
	@FXML
	private ComboBox<String> currentMeasUnitComboBox;
	private ObservableList<String> measUnitsList;
	@FXML
	private Button addFreqBtn;
	@FXML
	private Button deleteFreqBtn;
	//������� ��� �����������
	@FXML
	private ScrollPane resultsScrollPane;
	@FXML
	private AnchorPane resultsTablePane;
	private StringGridFX resultsTable;
	@FXML
	private Button deletResBtn;
	@FXML
	private Button saveResModificationBtn;
	@FXML
	private Label measUnitLabel;
	@FXML
	private Label dateLabel;
	@FXML
	private Pane resBtnPane;
	
//---------------------------------------------	
	private Device modDevice;  //������������� �������� ���������
	private MeasResult currentResult;  //������������ ��������� ���������
	ToleranceParametrs currentParams;
	private int currentElementIndex;
	private ArrayList<ArrayList<String>> verifications;
	
//---------------------------------------------------------
	@FXML
	private void initialize() {		
		resultsTable = this.createResultsTable();
		paramsTable = this.createParamsTable();
		
		elementsList = FXCollections.observableArrayList();
		verificationSecondParametrList = FXCollections.observableArrayList();
		measUnitsList = FXCollections.observableArrayList();
		
		devNamesList = FXCollections.observableArrayList();
		try {
			FileManager.LinesToItems(new File(".").getAbsolutePath() + "//files//sitypes.txt", devNamesList);
		} catch (Exception exp) {
			devNamesList.add("������� ������ �����");
			devNamesList.add("����� �������� �����������");
			devNamesList.add("�������� ����������� �������������");
			devNamesList.add("�������� ����������");
			devNamesList.add("������������� � ���������� ��������� ���");
			devNamesList.add("�������� ����������� �� ���������");
		} 
		nameComboBox.setItems(devNamesList);
		
		propertiesList = FXCollections.observableArrayList();
		propertiesList.add("�������� ��������");
		propertiesList.add("��������� ���������");
		
		this.verificationSecondParametrComboBox.setItems(this.verificationSecondParametrList);
		this.currentMeasUnitComboBox.setItems(this.measUnitsList);				
		this.currentMeasUnitComboBox.setItems(measUnitsList);
		this.editedPropertyComboBox.setItems(propertiesList);
	}
//---------------------------------------------------------	
	//�������� ������
	private StringGridFX createParamsTable() {
		ArrayList<String> tableHeads = new ArrayList<String>();
		tableHeads.add("�������, ���");
		tableHeads.add("������ - ������ ������");
		tableHeads.add("������ - ������� ������");
		tableHeads.add("���� - ������ ������");
		tableHeads.add("���� - ������� ������");
		return new StringGridFX(5, 10, 800, 100, paramsScrollPane, paramsTablePane, tableHeads);
	}
	
	private StringGridFX createResultsTable() {
		ArrayList<String> tableHeads = new ArrayList<String>();
		tableHeads.add("�������, ���");
		tableHeads.add("���. ����. ������");
		tableHeads.add("�����������");
		tableHeads.add("���. ����. ����");
		tableHeads.add("�����������");
		return new StringGridFX(5, 10, 800, 100, resultsScrollPane, resultsTablePane, tableHeads);
	}
//---------------------------------------------------------
	//������� ����
	private void clearGUI() {
		this.nameComboBox.setValue("");
		this.typeTextFiel.setText("");
		this.ownerTextField.setText("");
		this.serNumTextField.setText("");
		this.gosNumTextField.setText("");
		
		this.elementsList.clear();
		this.elementsListView.setItems(elementsList);
		this.verificationSecondParametrList.clear();
		this.verificationSecondParametrComboBox.setItems(verificationSecondParametrList);
	}
//---------------------------------------------------------
	
	//��������� ����������� �������������� ������� � ��������� ���������
	private void setMeasUnits() {	
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
//---------------------------------------------------------		
	//����������� �����������
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

	//����������� ���������� �����������
	private void showParams() throws SQLException {
		if (this.modDevice == null) {
			return;
		}
				
		String keys[] = {"d_m_S11", "u_m_S11", "d_p_S11", "u_p_S11",  
				   "d_m_S12", "u_m_S12", "d_p_S12", "u_p_S12",
				   "d_m_S21", "u_m_S21", "d_p_S21", "u_p_S21",
				   "d_m_S22", "u_m_S22", "d_p_S22", "u_p_S22"};
		
		this.paramsTable.clear();
		if (this.verificationSecondParametrComboBox.getSelectionModel().getSelectedIndex()==0) {
			currentParams = this.modDevice.includedElements.get(currentElementIndex).getPrimaryToleranceParams();
		}
		else {
			currentParams = this.modDevice.includedElements.get(currentElementIndex).getPeriodicToleranceParams();
		}	
			
		int necessaryRowCount = currentParams.getCountOfFreq();
		if (this.paramsTable.getRowCount() < necessaryRowCount) {
			while(this.paramsTable.getRowCount() < necessaryRowCount) {
				this.paramsTable.addRow();
			}
		}
		else if (this.paramsTable.getRowCount() > necessaryRowCount) {
			while(this.paramsTable.getRowCount() > necessaryRowCount) {
				this.paramsTable.deleteRow(this.paramsTable.getRowCount());
			}				
		}
		this.paramsTable.setColumnFromDouble(0, currentParams.freqs);
		int dParam = this.currentMeasUnitComboBox.getSelectionModel().getSelectedIndex();
		for (int i=0; i<4; i++) {
			String key = keys[i  +dParam];
			HashMap<Double, Double> hMap = currentParams.values.get(key);
			this.paramsTable.setColumnFromDouble(i+1, Adapter.HashMapToArrayList(currentParams.freqs, hMap));
		}
		
	}
//---------------------------------------------------------
	private void setParamsTypes() {
		measUnitsList.clear();
		measUnitsList.add("��������� �������");
		measUnitsList.add("������������� �������");
	}
	
	@FXML
	private void passBtnClick() {
		
	}
	
	@FXML
	private void errorParamsBtnClick() throws IOException {
		ErrorParamsWindow.getErrorParamsWindow().show();
	}
	
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
				AboutMessageWindow aboutWin = new AboutMessageWindow("�������", "��������� ���������");
				aboutWin.show();
			}
			catch(IOException ioExp) {
				ioExp.getStackTrace();
			}
		}
		catch(SQLException sqlExp) {
			try {
				AboutMessageWindow aboutWin = new AboutMessageWindow("������", "�� ������� ��������� ���������");
				aboutWin.show();
			}
			catch(IOException ioExp) {
				ioExp.getStackTrace();
			}
		}
	}
	
	@FXML
	private void saveResModificationBtn() {
		
	}
	
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
	private void deleteDeviceBtnClick() throws IOException, SQLException {
		modDevice.deleteFromDB();
		try {
			DataBaseManager.getDB().BeginTransaction();
			modDevice.deleteFromDB();
			DataBaseManager.getDB().Commit();
			clearGUI(); 
			modDevice = null;
		}
		catch(SQLException sqlExp) {
			DataBaseManager.getDB().RollBack();
			AboutMessageWindow msgWin = new AboutMessageWindow("������", "������ ������� � ��\n��� ������� ��������");
			msgWin.show();
		}
	}
		
	@FXML
	private void deletResBtnClick() throws IOException {
		try {
			currentResult.deleteFromDB();
		}
		catch(SQLException sqlExp) {
			AboutMessageWindow msgWin = new AboutMessageWindow("������", "������ ������� � ��\n��� �������� ����������� ���������");
			msgWin.show();	
		}
	}
			
	@FXML
	private void elementsListViewClick() throws IOException {
		currentElementIndex = elementsListView.getSelectionModel().getSelectedIndex();
		Element cElm = this.modDevice.includedElements.get(currentElementIndex);
		try {
			verifications = cElm.getListOfVerifications();
			this.verificationSecondParametrList.clear();
			for (int i = 0; i < verifications.size(); i++) {
				verificationSecondParametrList.add(verifications.get(i).get(1));
			}						
			this.verificationSecondParametrComboBox.setItems(verificationSecondParametrList);			
			//��������� ���������� ��������
			setMeasUnits();
		}
		catch(SQLException exp) {
			AboutMessageWindow msgWin = new AboutMessageWindow("������", "������ ������� � ��\n��� ��������� ������ ����������� �������");
			msgWin.show();
		}		
	}
		
	@FXML
	private void currentMeasUnitComboBoxClick() throws SQLException {
		int index = this.editedPropertyComboBox.getSelectionModel().getSelectedIndex();
		if(index == 0) {
			showParams();			
		}
		else if (index == 1) {
			if (this.currentResult != null) showResult();
		}
	}
	
	@FXML
	private void verificationSecondParametrComboBoxClick() throws IOException {
		try {
			int index = this.editedPropertyComboBox.getSelectionModel().getSelectedIndex();
			int resIndex = Integer.parseInt(this.verifications.get(
					this.verificationSecondParametrComboBox.getSelectionModel().getSelectedIndex()).get(0));
			this.currentMeasUnitComboBox.setValue(measUnitsList.get(0));
			this.currentResult = new MeasResult(this.modDevice.includedElements.get(currentElementIndex), resIndex);			
			if(index == 0) {
				showParams();
			}	
			else if (index == 1) {
				showResult();
			}
		}
		catch(SQLException sqlExp) {
			AboutMessageWindow msgWin = new AboutMessageWindow("������", "������ ������� � ��\n��� ��������� ����������� ���������");
			msgWin.show();
		}
	}
		
	@FXML
	private void editedPropertyComboBoxChange() {
		int index = this.editedPropertyComboBox.getSelectionModel().getSelectedIndex();
		switch(index) {
			case 0:
				dateLabel.setText("��� �������");
				this.verificationSecondParametrList.clear();
				this.verificationSecondParametrList.add("���������");
				this.verificationSecondParametrList.add("�������������");
				this.paramsBox.toFront();
				break;
			case 1:
				dateLabel.setText("���� ���������� �������:");
				this.verificationSecondParametrList.clear();
				this.resultBox.toFront();
				break;
		}
	}

	@FXML
	private void addFreqBtnClick() {
		this.paramsTable.addRow();
	}
	@FXML
	private void deleteFreqBtnClick() {
		this.paramsTable.deleteRow(this.paramsTable.getRowCount());
	}
}
