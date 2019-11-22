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
import Exceptions.SavingException;
import FileManagePack.FileManager;
import GUIpack.InfoRequestable;
import GUIpack.StringGridFXPack.ResultsStringGridFX;
import GUIpack.StringGridFXPack.StringGridPosition;
import NewElementPack.NewElementController;
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
	
	//������ ����� ����
	//�������� ����������� ���������
	@FXML
	private Label measInfoLabel;
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
		MenuItem addElementItem = new MenuItem("��������");
		elemtnsListViewContextMenu.getItems().addAll(deleteElementItem, editElementItem, addElementItem);
		
		//deleting element
		deleteElementItem.setOnAction(event->{
			int answer = YesNoWindow.createYesNoWindow("�������?", "������� ��������� �������:\n" + 
							elementsListView.getSelectionModel().getSelectedItem().toString()).showAndWait();
			if (answer == 0) {
				int deletedElementIndex = elementsListView.getSelectionModel().getSelectedIndex();
				deleteElement(deletedElementIndex);
			}	
		});
		
		//editing element
		editElementItem.setOnAction(event -> {
			int index = elementsListView.getSelectionModel().getSelectedIndex();
			Element elm = modDevice.includedElements.get(index);
			try {
				NewElementWindow elementWin = new NewElementWindow(elm);
				elementWin.show();
			} catch (IOException e) {
				e.printStackTrace();
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
					String item = newElement.getType() + " �" + newElement.getSerialNumber();
					elementsList.add(item);
					AboutMessageWindow.createWindow("�������� ����������", "����� ������� ������� ��������").show();
				} 
				catch (IOException ioExp) {
					AboutMessageWindow.createWindow("������ �������� ����", "������ �������� ���� �������� ������ ��������.").show();
					ioExp.printStackTrace();
				} 
				catch (SavingException sExp) {
					AboutMessageWindow.createWindow("������ ����������", sExp.getMessage()).show();
					sExp.printStackTrace();
				} 
				catch (SQLException sqlExp) {
					AboutMessageWindow.createWindow("������ ����������", "���� ������ ���������� ��� ����������").show();
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
		MenuItem deleteResultItem = new MenuItem("������� ���������");
		verificationsDatesListViewContextMenu.getItems().addAll(deleteResultItem);
		
		deleteResultItem.setOnAction(event -> {
			int resIndex = Integer.parseInt(verifications.get(currentDateIndex).get(0));
			if (resIndex == currentElement.getNominalIndex()) {
				AboutMessageWindow.createWindow("������", "��������, �������������� � ��������\n����������� �� ����� ���� ������� �� ��").show();
				return;
			}
			MeasResult deletedResult;
			try {
				deletedResult = new MeasResult(modDevice.includedElements.get(currentElementIndex), resIndex);
				deletedResult.deleteFromDB();
				AboutMessageWindow.createWindow("�������", "��������� ��������� ������").show();
			} catch (SQLException sqlExp) {
				AboutMessageWindow.createWindow("������", "������ ������� � ��\n��� ������� ��������").show();
			}
		});
		
		this.verificationDateListView.setOnContextMenuRequested(event->{
			double x = event.getScreenX();
			double y = event.getScreenY();
			verificationsDatesListViewContextMenu.show(verificationDateListView, x, y);
		});
	}
//--------------------�������� ��������--------------------
	private void deleteElement(int index) {
		if (index < 0) return;
		try {
			DataBaseManager.getDB().BeginTransaction();
			modDevice.includedElements.get(index).deleteFromDB();
			DataBaseManager.getDB().Commit();
			modDevice.removeElement(index);
			elementsList.remove(index);
		}
		catch(SQLException sqlExp) {
			DataBaseManager.getDB().RollBack();
			AboutMessageWindow.createWindow("������", "������ ������� � ��\n��� ������� ��������").show();
		}
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
			String item = elm.getType() + " �" + elm.getSerialNumber();
			elementsList.add(item);
		}
		elementsListView.setItems(elementsList);		
	}
//---------------------------------------------------------
	//������� ����
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
//---------------------------------------------------------
	@FXML
	private void errorParamsBtnClick() throws IOException {
		ErrorParamsWindow.getErrorParamsWindow().show();
	}	
	
//---������ � �����������---
//���������� ��������� � ����������
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
			AboutMessageWindow.createWindow("�������", "��������� ���������").show();
		}
		catch(SQLException sqlExp) {
			DataBaseManager.getDB().RollBack();
			AboutMessageWindow.createWindow("������", "�� ������� ��������� ���������").show();
		}
	}	
//---------------------------------------------------------
	@FXML
	private void deleteDeviceBtnClick() {
		int answer = YesNoWindow.createYesNoWindow("�������?", "�� �������, ��� ������ ������� ���� ������?").showAndWait();
		if (answer == 0) {
			try {
				DataBaseManager.getDB().BeginTransaction();
				modDevice.deleteFromDB();
				DataBaseManager.getDB().Commit();
				clearGUI(); 
				modDevice = null;
				AboutMessageWindow.createWindow("�������", "������ ������").show();
			}
			catch(SQLException sqlExp) {
				DataBaseManager.getDB().RollBack();
				AboutMessageWindow.createWindow("������", "������ ������� � ��\n��� ������� ��������").show();
			}
		}		
	}	
//--- ������ � ������������ ��������� ---

//�����
	@FXML
	private void searchDeviceBtnClick() {
		try {
			SearchDeviceWindow.getSearchDeviceWindow(modDevice, this).show();
		}
		catch(IOException ioExp) {
			System.out.println("������ ��� �������� ���� ������ �������. " + ioExp.getStackTrace());
		}
	}
			
	@FXML
	private void elementsListViewClick() {
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
			int nominalIndex = currentElement.getNominalIndex();
			for (int i = 0; i < verifications.size(); i++) {
				String item = null;
				int resIndex = Integer.parseInt(verifications.get(i).get(0));
				if (resIndex != nominalIndex)
					item = "�������, ����������� " + verifications.get(i).get(1);
				else 
					item = "����������� ��������, ���������� " + verifications.get(i).get(1);
				verificationDateList.add(item);
			}						
			verificationDateListView.setItems(verificationDateList);			
		}
		catch(SQLException exp) {
			AboutMessageWindow.createWindow("������", "������ ������� � ��\n��� ��������� ������ ����������� �������").show();
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
			String date = currentResult.getDateOfMeasByString();
			date = date.split(" ")[0];
			String unit = currentMeasUnitListView.getSelectionModel().getSelectedItem();
			measInfoLabel.setText("���������� ��������� " + unit + " ������������ " + date + " ��� \"" + currentResult.getMyOwner().getType() + " " +
					currentResult.getMyOwner().getSerialNumber() + "\" �� ������� \"" + currentResult.getMyOwner().getMyOwner().getName() + " " +
					currentResult.getMyOwner().getMyOwner().getType() + " �" + currentResult.getMyOwner().getMyOwner().getSerialNumber() + "\".");
		}
		catch(SQLException sqlExp) {
			AboutMessageWindow.createWindow("������", "������ ������� � ��\n��� ��������� ����������� ���������").show();
		}
	}
}