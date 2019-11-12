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
import GUIpack.StringGridFXPack.ResultsStringGridFX;
import GUIpack.StringGridFXPack.StringGridPosition;
import NewElementPack.NewElementWindow;
import SearchDevicePack.SearchDeviceWindow;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import VerificationPack.MeasResult;
import YesNoDialogPack.YesNoWindow;
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

public class DBEditController implements InfoRequestable {
	
//����� ����� ����
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
	private ContextMenu verificationsDatesListViewContextMenu;
	private ObservableList<String> elementsList;
//---------------------------------------------

//������ ����� ����
	//------------------------------
	//�������� ����������� ���������
	@FXML
	private ListView<String> verificationDateListView;
	private ObservableList<String> verificationDateList;	
	@FXML
	private ListView<String> currentMeasUnitListView;
	private ObservableList<String> measUnitsList;	
	//������� ��� �����������
	@FXML
	private ScrollPane resultsScrollPane;
	@FXML
	private AnchorPane resultsTablePane;
	private ResultsStringGridFX resultsTable;
	@FXML
	private Label measUnitLabel;
	@FXML
	private Label dateLabel;
	
//---------------------------------------------	
	private Device modDevice;  //������������� �������� ���������
	private MeasResult currentResult;  //������������ ��������� ���������
	private Integer currentElementIndex = null;
	private Integer currentDateIndex = null;
	private Integer currentMeasUnitIndex = null;	
	private Element currentElement = null;
	private ArrayList<ArrayList<String>> verifications;
	
//---------------------------------------------------------
	@FXML
	private void initialize() {				
		StringGridPosition position = new StringGridPosition(800, 100, resultsScrollPane, resultsTablePane); 
		resultsTable = new ResultsStringGridFX(position);		
		elementsList = FXCollections.observableArrayList();
		verificationDateList = FXCollections.observableArrayList();
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
		
		this.verificationDateListView.setItems(this.verificationDateList);				
		this.currentMeasUnitListView.setItems(measUnitsList);
		
		createElemtnsListViewContextMenu();		
		createVerificationsDatesContextMenu();
	}
	
	private void createElemtnsListViewContextMenu() {
		elemtnsListViewContextMenu = new ContextMenu();
		MenuItem deleteElementItem = new MenuItem("�������");
		MenuItem editElementItem = new MenuItem("�������������");
		elemtnsListViewContextMenu.getItems().addAll(deleteElementItem, editElementItem);
		
		deleteElementItem.setOnAction(event->{
			try {
				YesNoWindow qWin = new YesNoWindow("�������?", "������� ��������� �������:\n" + 
								this.elementsListView.getSelectionModel().getSelectedItem().toString());
				int answer = qWin.showAndWait();
				if (answer == 0) {
					deleteElement(this.elementsListView.getSelectionModel().getSelectedIndex());
				}	
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		editElementItem.setOnAction(event->{
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
			elemtnsListViewContextMenu.show(elementsListView, x, y);
		});
	}
	
	private void createVerificationsDatesContextMenu() {
		verificationsDatesListViewContextMenu = new ContextMenu();
		MenuItem deleteResultItem = new MenuItem("������� ���������");
		verificationsDatesListViewContextMenu.getItems().addAll(deleteResultItem);
		
		deleteResultItem.setOnAction(event -> {
			int resIndex = Integer.parseInt(verifications.get(currentDateIndex).get(0));
			MeasResult deletedResult;
			try {
				deletedResult = new MeasResult(modDevice.includedElements.get(currentElementIndex), resIndex);
				deletedResult.deleteFromDB();
			} catch (SQLException sqlExp) {
				try {
					AboutMessageWindow msgWin = new AboutMessageWindow("������", "������ ������� � ��\n��� ������� ��������");
					msgWin.show();
				} catch (IOException ioExp) {
					ioExp.printStackTrace();
				}	
			}
		});
		
		this.verificationDateListView.setOnContextMenuRequested(event->{
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
		this.verificationDateList.clear();
		this.verificationDateListView.setItems(verificationDateList);
	}
//---------------------------------------------------------
	@FXML
	private void errorParamsBtnClick() throws IOException {
		ErrorParamsWindow.getErrorParamsWindow().show();
	}	
	
//---������ � �����������---
//���������� ��������� � ����������
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
//----------------------��������---------------------------
//-----------------�������� ����������---------------------	
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
			AboutMessageWindow msgWin = new AboutMessageWindow("������", "������ ������� � ��\n��� ������� ��������");
			msgWin.show();
		}
	}
//---------------------------------------------------------
//--------------------�������� ��������--------------------
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
			AboutMessageWindow msgWin = new AboutMessageWindow("������", "������ ������� � ��\n��� ������� ��������");
			msgWin.show();
		}
	}	
//---------------------------------------------------------
//---------------------------------------------------------
	@FXML
	private void deleteDeviceBtnClick() throws IOException {
		deleteDevice();
	}	
//--- ������ � ������������ ��������� ---

//�����
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
	private void elementsListViewClick() throws IOException {
		if (modDevice == null) {
			return;
		}		
		resultsTable.clear();
		verificationDateList.clear();
		measUnitsList.clear();
		
		currentDateIndex = null;
		currentMeasUnitIndex = null;		
		currentElementIndex = elementsListView.getSelectionModel().getSelectedIndex();
		currentElement = modDevice.includedElements.get(currentElementIndex);
		
		try {
			verifications = currentElement.getListOfVerifications();			
			for (int i = 0; i < verifications.size(); i++) {
				verificationDateList.add(verifications.get(i).get(1));
			}						
			verificationDateListView.setItems(verificationDateList);			
		}
		catch(SQLException exp) {
			AboutMessageWindow msgWin = new AboutMessageWindow("������", "������ ������� � ��\n��� ��������� ������ ����������� �������");
			msgWin.show();
		}		
	}	
		
	@FXML
	private void verificationDateListViewClick() throws IOException {
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
	
	private void showResult() throws IOException {
		try {
			int resIndex = Integer.parseInt(verifications.get(currentDateIndex).get(0));
			currentResult = new MeasResult(modDevice.includedElements.get(currentElementIndex), resIndex);
			resultsTable.showResult(currentResult, S_Parametr.values()[currentMeasUnitIndex]);
		}
		catch(SQLException sqlExp) {
			AboutMessageWindow msgWin = new AboutMessageWindow("������", "������ ������� � ��\n��� ��������� ����������� ���������");
			msgWin.show();
		}
	}
}