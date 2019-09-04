package DBEditForm;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import AboutMessageForm.AboutMessageWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Device;
import DevicePack.Element;
import ErrorParamsPack.ErrorParamsWindow;
import FileManagePack.FileManager;
import GUIpack.InfoRequestable;
import GUIpack.StringGridFX;
import SearchDevicePack.SearchDeviceWindow;
import VerificationPack.MeasResult;
import _tempHelpers.Adapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class DBEditController implements InfoRequestable {
	
	@FXML
	private Button passBtn;	
	@FXML
	private Button errorParamsBtn;	
	@FXML
	private Button searchDeviceBtn;
	@FXML
	private Button deleteDeviceBtn;
	@FXML
	private Button deletResBtn;
	@FXML
	private Button saveDeviceModificationBtn;
	@FXML
	private Button saveResModificationBtn;
	
	@FXML
	private TextField nameTextField;
	@FXML
	private TextField typeTextFiel;
	@FXML
	private TextField serNumTextField;
	@FXML
	private TextField ownerTextField;
	@FXML
	private TextField gosNumTextField;
	
	@FXML
	private ListView<String> elementsListView;
	private ObservableList<String> elementsList;
	
	@FXML
	private ComboBox<String> verificationDateComboBox;
	private ObservableList<String> verificationDateList;
	
	@FXML
	private RadioButton resultsRB;
	@FXML
	private RadioButton paramsRB;
	private ToggleGroup indicationGroup;
	
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private AnchorPane tablePane;
	private StringGridFX resultTable;
	
	@FXML
	private ComboBox<String> currentMeasUnitComboBox;
	private ObservableList<String> measUnitsList;
	
	private Device modDevice;
	private int currentElementIndex;
	private ArrayList<ArrayList<String>> verifications;
	private MeasResult currentResult;
//---------------------------------------------------------
	@FXML
	private void initialize() {
		ArrayList<String> tableHeads = new ArrayList<String>();
		tableHeads.add("Частота, ГГц");
		tableHeads.add("Изм. знач. модуля");
		tableHeads.add("СКО");
		tableHeads.add("Погрешность");
		tableHeads.add("Изм. знач. фазы");
		tableHeads.add("СКО");
		tableHeads.add("Погрешность");
		resultTable = new StringGridFX(7, 10, 800, 100, scrollPane, tablePane, tableHeads);
		
		indicationGroup = new ToggleGroup();		
		this.resultsRB.setToggleGroup(indicationGroup);
		this.paramsRB.setToggleGroup(indicationGroup);
		this.resultsRB.setSelected(true);
		
		elementsList = FXCollections.observableArrayList();
		verificationDateList = FXCollections.observableArrayList();
		measUnitsList = FXCollections.observableArrayList();
	}
//---------------------------------------------------------	
	@FXML
	private void passBtnClick() {
		
	}
	
	@FXML
	private void errorParamsBtnClick() throws IOException {
		ErrorParamsWindow.getErrorParamsWindow().show();
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
			AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Ошибка доступа к БД\nпри попытке удаления");
			msgWin.show();
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
	private void saveDeviceModificationBtnClick() {
		
	}
	
	@FXML
	private void saveResModificationBtn() {
		
	}
	
	@FXML
	private void elementsListViewClick() throws IOException {
		int index = elementsListView.getSelectionModel().getSelectedIndex();
		Element cElm = this.modDevice.includedElements.get(index);
		try {
			verifications = cElm.getListOfVerifications();
			this.verificationDateList.clear();
			for (int i = 0; i < verifications.size(); i++) {
				verificationDateList.add(verifications.get(i).get(1));
			}						
			this.verificationDateComboBox.setItems(verificationDateList);
			
			//Установим измеряемые велечины
			ArrayList<String> items = new ArrayList<String>();
			String path = new File(".").getAbsolutePath();
			if (cElm.getMeasUnit().equals("vswr")) {
				path += "\\files\\vswr.txt";			
			}
			else {
				path += "\\files\\gamma.txt";
			}
			FileManager.LinesToItems(path, items);
			
			measUnitsList.add(items.get(0));			
			if (cElm.getPoleCount() == 4) {
				measUnitsList.add(items.get(1));
				measUnitsList.add(items.get(2));
				measUnitsList.add(items.get(3));
			}	
			this.currentMeasUnitComboBox.setItems(measUnitsList);
		}
		catch(SQLException exp) {
			AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Ошибка доступа к БД\nпри получении списка проведенных поверок");
			msgWin.show();
		}		
	}
	
	@FXML
	private void verificationDataComboBoxClick() throws IOException {
		try {
			int resIndex = Integer.parseInt(this.verifications.get(
					this.verificationDateComboBox.getSelectionModel().getSelectedIndex()).get(0));
			this.currentMeasUnitComboBox.setValue(measUnitsList.get(0));
			this.currentResult = new MeasResult(this.modDevice.includedElements.get(currentElementIndex), resIndex);
			showResult();			
		}
		catch(SQLException sqlExp) {
			AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Ошибка доступа к БД\nпри получении результатов измерения");
			msgWin.show();
		}
	}
		
	@Override
	public void setDevice(Device device) {
		modDevice = device;	
	}

	@Override
	public void representRequestedInfo() {
		this.nameTextField.setText(modDevice.getName());
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
	
	private void clearGUI() {
		this.nameTextField.setText("");
		this.typeTextFiel.setText("");
		this.ownerTextField.setText("");
		this.serNumTextField.setText("");
		this.gosNumTextField.setText("");
		
		this.elementsList.clear();
		this.elementsListView.setItems(elementsList);
		this.verificationDateList.clear();
		this.verificationDateComboBox.setItems(verificationDateList);
	}
	

	private void showResult() throws SQLException {		
		int necessaryRowCount = this.currentResult.freqs.size();
		if (this.resultTable.getRowCount() < necessaryRowCount) {
			while(this.resultTable.getRowCount() < necessaryRowCount) {
				this.resultTable.addRow();
			}
		}
		else if (this.resultTable.getRowCount() > necessaryRowCount) {
			while(this.resultTable.getRowCount() > necessaryRowCount) {
				this.resultTable.deleteRow(this.resultTable.getRowCount());
			}				
		}
		this.resultTable.setColumnFromDouble(0, this.currentResult.freqs);
		String keys[] = {"m_S11", "sko_m_S11", "err_m_S11", "p_S11", "sko_p_S11", "err_p_S11", 
				 "m_S12", "sko_m_S12", "err_m_S12", "p_S12", "sko_p_S12", "err_p_S12",
				 "m_S21", "sko_m_S21", "err_m_S21", "p_S21", "sko_p_S21", "err_p_S21", 
				 "m_S22", "sko_m_S22", "err_m_S22", "p_S22", "sko_p_S22", "err_p_S22"};
		for (int i = 1; i < 7; i++) {
			int currentMeasUnitIndex = this.currentMeasUnitComboBox.getSelectionModel().getSelectedIndex();
			String key = keys[i-1 + currentMeasUnitIndex*6];
			this.resultTable.setColumnFromDouble(i, Adapter.HashMapToArrayList(this.currentResult.freqs, this.currentResult.values.get(key)));
		}
		
	}
}
