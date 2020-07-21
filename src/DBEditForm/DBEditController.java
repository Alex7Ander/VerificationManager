package DBEditForm;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import AboutMessageForm.AboutMessageWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Device;
import DevicePack.Element;
import Exceptions.NoMeasResultDataForThisVerificationException;
import Exceptions.SavingException;
import FileManagePack.FileManager;
import GUIpack.InfoRequestable;
import GUIpack.Tables.ResultsTable;
import NewElementPack.NewElementController;
import NewElementPack.NewElementWindow;
import NewProtocolResultsSearchPack.NewProtocolResultsSearchWindow;
import ProtocolCreatePack.ProtocolCreateWindow;
import SearchDevicePack.SearchDeviceWindow;
import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationPack.MeasResult;
import VerificationPack.VerificationProcedure;
import YesNoDialogPack.YesNoWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
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
	
	@FXML
	private ScrollPane mainPane;
	//����� ����� ����
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
	private Button createNewProtocolBtn;
	
	@FXML
	private ListView<String> elementsListView;
	private ContextMenu elemtnsListViewContextMenu;
	private ContextMenu verificationsDatesListViewContextMenu;
	private ObservableList<String> elementsList;
	private List<VerificationProcedure> modDeviceVerificationProcedures;
	//������ ����� ����
	//�������� ����������� ���������
	@FXML
	private Label deviceInfoLabel;
	@FXML
	private Label elementInfoLabel;
	@FXML
	private Label dateInfoLabel;
	@FXML
	private Label unitInfoLabel;
	@FXML
	private Label measUnitLabel;
	@FXML
	private Label dateLabel;
	
	@FXML
	private ListView<String> verificationDateListView;
	private ObservableList<String> verificationDateList;
	private ObservableList<String> verificationDateList2;
	@FXML
	private ListView<String> currentMeasUnitListView;
	private ObservableList<String> measUnitsList;	
	//������� ��� �����������
	@FXML
	private ScrollPane resultsScrollPane;
	@FXML
	private AnchorPane resultsTablePane;
	private ResultsTable resultsTable;

//---------------------------------------------	
	private Device modDevice;  //������������� �������� ���������
	private MeasResult currentResult;  //������������ ��������� ���������
	private Integer currentElementIndex = null;
	private Integer currentDateIndex = null;
	private Integer currentMeasUnitIndex = null;	
	private Element currentElement = null;
	private List<List<String>> verifications;
	
//---------------------------------------------------------
	@FXML
	private void initialize() {						
		this.resultsTable = new ResultsTable(resultsTablePane);		
		this.elementsList = FXCollections.observableArrayList();
		
		
		this.verificationDateList = FXCollections.observableArrayList();
		this.verificationDateList2 = FXCollections.observableArrayList();
		
		
		this.measUnitsList = FXCollections.observableArrayList();		
		this.devNamesList = FXCollections.observableArrayList();
		try {
			FileManager.LinesToItems(new File(".").getAbsolutePath() + "//files//sitypes.txt", this.devNamesList);
		} catch (Exception exp) {
			this.devNamesList.add("������� ������ �����");
			this.devNamesList.add("����� �������� �����������");
			this.devNamesList.add("�������� ����������� �������������");
			this.devNamesList.add("�������� ����������");
			this.devNamesList.add("������������� � ���������� ��������� ���");
			this.devNamesList.add("�������� ����������� �� ���������");
		} 
		this.nameComboBox.setItems(devNamesList);
		
		//this.verificationDateListView.setItems(this.verificationDateList);
		this.verificationDateListView.setItems(this.verificationDateList2);
		this.currentMeasUnitListView.setItems(measUnitsList);

		createElemtnsListViewContextMenu();		
		createVerificationsDatesContextMenu();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();		
		this.mainPane.setMaxHeight(dim.height);
		this.mainPane.setMaxWidth(dim.width);		
	}
	
	private void createElemtnsListViewContextMenu() {
		this.elemtnsListViewContextMenu = new ContextMenu();
		MenuItem deleteElementItem = new MenuItem("�������");
		MenuItem editElementItem = new MenuItem("�������������");
		MenuItem addElementItem = new MenuItem("��������");
		this.elemtnsListViewContextMenu.getItems().addAll(deleteElementItem, editElementItem, addElementItem);
		
		//deleting element
		deleteElementItem.setOnAction(event->{
			int answer = YesNoWindow.createYesNoWindow("�������?", "������� ��������� �������:\n" + 
					this.elementsListView.getSelectionModel().getSelectedItem().toString()).showAndWait();
			if (answer == 0) {
				int deletedElementIndex = this.elementsListView.getSelectionModel().getSelectedIndex();
				if (deletedElementIndex < 0) return;
				try {
					DataBaseManager.getDB().BeginTransaction();
					this.modDevice.includedElements.get(deletedElementIndex).deleteFromDB();
					int currentModdeviceId = this.modDevice.getId();
					//refresh this device 
					this.modDevice = new Device(currentModdeviceId);					
					DataBaseManager.getDB().Commit();
					this.elementsList.remove(deletedElementIndex);
				}
				catch(SQLException sqlExp) {
					DataBaseManager.getDB().RollBack();
					AboutMessageWindow.createWindow("������", "������ ������� � ��\n��� ������� ��������").show();
				}				
			}				
		});
		
		//editing element
		editElementItem.setOnAction(event -> {
			int index = this.elementsListView.getSelectionModel().getSelectedIndex();
			Element elm = this.modDevice.includedElements.get(index);
			try {
				NewElementWindow elementWin = new NewElementWindow(elm);
				elementWin.showAndWait();
				resetDevice();			
			} catch (IOException ioExp) {
				ioExp.printStackTrace();
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
		MenuItem setAsNominalResultItem = new MenuItem("������������ ��� ��������� ��� ��������� �������");
		MenuItem createProtocolItem = new MenuItem("�������� �������� �������");
		verificationsDatesListViewContextMenu.getItems().addAll(setAsNominalResultItem); //deleteResultItem, createProtocolItem
		
		setAsNominalResultItem.setOnAction(event -> {
			int checkedVerificationId = this.verificationDateListView.getSelectionModel().getSelectedIndex() - 1;
			if(checkedVerificationId > -1) {
				VerificationProcedure checkedProcedure = this.modDeviceVerificationProcedures.get(checkedVerificationId);			
				List<MeasResult> currentVerificationResults = checkedProcedure.getMeasResults();
				for (MeasResult result : currentVerificationResults) {
					try {
						result.setLastVerificationStatus();								
					} catch (SQLException sqlExp) {
						System.err.println("������ ��� ������� ���������� ��������� � id = " + result.getId() + " ������ ���������� ���������� �������. " + sqlExp.getMessage());
					}
				}	
				resetDevice();
			}
			else { 
				for (Element elm : this.modDevice.includedElements) {
					try {
						elm.getNominal().setLastVerificationStatus();
						resetDevice();
					} catch (SQLException sqlExp) {
						System.err.println("������ ��� ������� �������� �������� � id = " + elm.getId() + " ������ ���������� ���������� �������. " + sqlExp.getMessage());
					}
				}
			}			
		});
		
		
		deleteResultItem.setOnAction(event -> {						
			int resIndex = Integer.parseInt(verifications.get(currentDateIndex).get(0));
			if (resIndex == currentElement.getNominalId()) {
				AboutMessageWindow.createWindow("������", "��������, �������������� � ��������\n����������� �� ����� ���� ������� �� ��").show();
				return;
			}
			int answer = YesNoWindow.createYesNoWindow("�������?", "�� �������, ��� ������ ������� ����������\n��������� �� " + verifications.get(currentDateIndex).get(1)).showAndWait();
			if (answer == 1) {
				return;
			}
			MeasResult deletedResult = null;
			try {
				deletedResult = new MeasResult(modDevice.includedElements.get(currentElementIndex), resIndex);
				deletedResult.deleteFromDB();
				AboutMessageWindow.createWindow("�������", "��������� ��������� ������").show();
				verifications.remove((int)currentDateIndex);
				verificationDateList.remove((int)currentDateIndex);
			} catch (SQLException sqlExp) {
				sqlExp.printStackTrace();
				AboutMessageWindow.createWindow("������", "������ ������� � ��\n��� ������� ��������").show();
			}
		});
				
		createProtocolItem.setOnAction(event->{			
			System.out.println("���������� � ������� ���������");
			int resIndex = Integer.parseInt(verifications.get(currentDateIndex).get(0));
			String[] docTypes = new String[] {"C������������ � �������", "��������� � �������������"};
											  
			List<MeasResult> results = new ArrayList<>();
			List<MeasResult> nominals = new ArrayList<>();
			List<ToleranceParametrs> protocoledModuleToleranceParams = new ArrayList<>();
			List<ToleranceParametrs> protocoledPhaseToleranceParams = new ArrayList<>();
			VerificationProcedure verification = null;
			String statusString = null;
			StringBuilder noResMessage = new StringBuilder();
			try {
				statusString = "- ��������� ���������� � ����������� �������";
				System.out.println(statusString);
				
				MeasResult checkedResult = new MeasResult(modDevice.includedElements.get(currentElementIndex), resIndex);
				int verificationId = checkedResult.getVerificationId();
				if(verificationId == 0) {
					verification = new VerificationProcedure();
					verification.setPeriodic();
					verification.setStrTemperature("-");
					verification.setStrAtmPreasure("-");
					verification.setStrAirHumidity("-");
					//AboutMessageWindow.createWindow("���������� ������� ��������", "��� ������ ��������� ������ �������\n�������� �������, ��������� �� ���� �������\n�� �� ��-24-20 ��� ��\n� ������ ��������� ����� 1.0.4.").show();
					return;
				}
				else {
					verification = new VerificationProcedure(verificationId);
				}
							
				for (Element elm : this.modDevice.includedElements) {	
					statusString = "- ��������� ���������� ��:\n" + elm.getType() + " �" + elm.getSerialNumber() + "\n";
					System.out.println(statusString + " ����������� �������");
					try {
						int currentResultId = MeasResult.getResultIdsByVerificationForElement(verificationId, elm);
						MeasResult result = new MeasResult(elm, currentResultId);
						
						if (verification.isPrimary()) {
							elm.getPrimaryModuleToleranceParams().checkResult(elm.getNominal(), result);
							elm.getPrimaryPhaseToleranceParams().checkResult(elm.getNominal(), result);
						}
						else {
							elm.getPeriodicModuleToleranceParams().checkResult(elm.getLastVerificationResult(), result);
							elm.getPeriodicPhaseToleranceParams().checkResult(elm.getLastVerificationResult(), result);
						}
						
						results.add(result);
					}					
					catch(NoMeasResultDataForThisVerificationException ndExp) {
						noResMessage.append(ndExp.getMessage() + "\n");
					}
					System.out.println(statusString + " ����������� ��������");
					nominals.add(elm.getNominal());  
										
					if (verification.isPrimary()) {
						System.out.println(statusString + " ��������� �������� ��� ��������� �������");
						protocoledModuleToleranceParams.add(elm.getPrimaryModuleToleranceParams()); 
						protocoledPhaseToleranceParams.add(elm.getPrimaryPhaseToleranceParams());
						
					}
					else {
						System.out.println(statusString + " ��������� �������� ��� ������������� �������");
						protocoledModuleToleranceParams.add(elm.getPeriodicModuleToleranceParams());
						protocoledPhaseToleranceParams.add(elm.getPeriodicPhaseToleranceParams());				
					}
				}
			} 
			catch(SQLException sqlExp) {
				sqlExp.printStackTrace();
				AboutMessageWindow.createWindow("������", "������ ������� � �� �� �����:\n" + statusString).show();
				return;
			}
			
			if(noResMessage.length() != 0) {
				AboutMessageWindow.createWindow("�� �������", "�� ������� ���������� ��������� ��� ��������� ���������: " + noResMessage.toString()).show();
			}
			
			try {
				ProtocolCreateWindow.getProtocolCreateWindow(this.modDevice, docTypes, results, nominals, protocoledModuleToleranceParams, protocoledPhaseToleranceParams, verification).show();
			} catch (IOException ioExp) {
				ioExp.printStackTrace();
				return;
			}
		});
		
		verificationDateListView.setOnContextMenuRequested(event->{
			double x = event.getScreenX();
			double y = event.getScreenY();
			verificationsDatesListViewContextMenu.show(verificationDateListView, x, y);
		});
	}
//InfoRequestable
	@Override
	public void setDevice(Device device) {
		modDevice = device;	
		try {
			verificationDateList2.clear();
			modDeviceVerificationProcedures = VerificationProcedure.getVerificationsProceduresWithMeasResults(modDevice);			
			if(this.modDevice.includedElements.get(0).getNominalId() == this.modDevice.includedElements.get(0).getLastVerificationId()) {
				verificationDateList2.add("����������� �������� - ���. ��� ��������� ��� ����. �������");
			}
			else{
				verificationDateList2.add("����������� ��������");
			}			
			for (VerificationProcedure procedure : modDeviceVerificationProcedures) {
				StringBuilder item = new StringBuilder("������� (���� ���������� " + procedure.getDate() + ")");
				
				MeasResult measResultFirstElement = procedure.getMeasResultForElement(this.modDevice.includedElements.get(0));
				if(measResultFirstElement == null){
					continue;
				}
				if(measResultFirstElement.getId() == this.modDevice.includedElements.get(0).getLastVerificationId()){
					item.append(" - ���. ��� ��������� ��� ����. �������");
				}				
				verificationDateList2.add(item.toString());
			}
		} catch (SQLException sqlExp) {
			System.err.println("������ ��� ��������� ���������� � ����������� �������� ��� ���������� � id = " + modDevice.getId() + ".  " + sqlExp.getMessage());
		}
	}
	public void removeDevice() {
		modDevice = null;
	}
	public void resetDevice() {
		try {
			int currentModdeviceId = this.modDevice.getId();
			this.modDevice = new Device(currentModdeviceId);
			this.setDevice(modDevice);
		} catch (SQLException sqlExp) {
			System.err.println("������ ��� ���������� ������������� ����������, ����� �������� ��������� ���������� �������. " + sqlExp.getMessage());
		}
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
		verificationDateList2.clear();
		measUnitsList.clear();
		resultsTable.clear();
	}
	
//--- ������ � ����������� ---
//���������� ��������� � ����������
	@FXML
	private void saveDeviceModificationBtnClick() {
		HashMap<String, String> editingValues = new HashMap<String, String>();
		editingValues.put("name", nameComboBox.getSelectionModel().getSelectedItem().toString()); 
		editingValues.put("type", typeTextField.getText());
		editingValues.put("serialNumber", serNumTextField.getText());
		editingValues.put("owner", ownerTextField.getText());
		editingValues.put("gosNumber", gosNumTextField.getText());
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
//--- �����
	@FXML
	private void searchDeviceBtnClick() {
		SearchDeviceWindow.getSearchDeviceWindow(modDevice, this).show();
	}
		
	@FXML
	private void verificationDateListViewClick() {
		/*
		if (currentElement == null) {
			return;
		}
		currentDateIndex = verificationDateListView.getSelectionModel().getSelectedIndex();
		try {
			List<String> items = new ArrayList<String>();
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
		*/
	}
	
	@FXML
	private void elementsListViewClick() {
		if (this.modDevice == null) {
			return;
		}
		
		this.resultsTable.clear();
		this.measUnitsList.clear();
		this.currentElementIndex = this.elementsListView.getSelectionModel().getSelectedIndex();
		this.currentElement = this.modDevice.includedElements.get(currentElementIndex);
		
		this.currentDateIndex = this.verificationDateListView.getSelectionModel().getSelectedIndex() - 1;
		if(this.currentDateIndex > -1) {
			VerificationProcedure checkedProcedure = this.modDeviceVerificationProcedures.get(this.currentDateIndex);
			this.currentResult = checkedProcedure.getMeasResultForElement(this.currentElement);
		}
		else {
			this.currentResult = this.currentElement.getNominal();
		}
		
		try {
			List<String> items = new ArrayList<String>();
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
		resultsTable.showResult(currentResult, S_Parametr.values()[currentMeasUnitIndex]);
		//set table column headers
		String newPhaseErrorHeader = null;
		if (modDevice.includedElements.get(currentElementIndex).getPhaseToleranceType().equals("percent")) {
			newPhaseErrorHeader = "�����������, %";
		} 
		else {
			newPhaseErrorHeader = "�����������, \u00B0";
		}
		resultsTable.setHead(4, newPhaseErrorHeader);
		
		String newModuleErrorHeader = null;
		if (modDevice.includedElements.get(currentElementIndex).getModuleToleranceType().equals("percent")) {
			newModuleErrorHeader = "�����������, %";
		} 
		else {
			newModuleErrorHeader = "�����������";
		}
		resultsTable.setHead(2, newModuleErrorHeader);
		
		String date = currentResult.getDateOfMeasByString();
		date = date.split(" ")[0];
		String unit = currentMeasUnitListView.getSelectionModel().getSelectedItem();
		
		ObservableList<Data<Number, Number>> dataModule = FXCollections.observableArrayList();
		ObservableList<Data<Number, Number>> dataPhase = FXCollections.observableArrayList();
		String key = MeasUnitPart.MODULE + "_" + S_Parametr.values()[currentMeasUnitIndex];
		for (int i=0; i < currentResult.values.get(key).size(); i++) {
			//size of Collection with module should has same size as with phase 
			double cFreq = currentResult.freqs.get(i);
			double cModule = currentResult.values.get(key).get(cFreq);
			double cPhase = currentResult.values.get(MeasUnitPart.PHASE + "_" + S_Parametr.values()[currentMeasUnitIndex]).get(cFreq);
			dataModule.add(new XYChart.Data<Number, Number>(cFreq, cModule));
			dataPhase.add(new XYChart.Data<Number, Number>(cFreq, cPhase));
		}		
		
		this.deviceInfoLabel.setText("�������� ���������:  " + currentResult.getMyOwner().getMyOwner().getName() + " �" +currentResult.getMyOwner().getMyOwner().getSerialNumber());
		this.elementInfoLabel.setText(currentResult.getMyOwner().getType() + " " + currentResult.getMyOwner().getSerialNumber());
		this.dateInfoLabel.setText("���� ���������� ���������:" + date);
		this.unitInfoLabel.setText("���������� ��������: "+ unit);
	}
	
	@FXML
	private void createNewProtocolBtnClick() {
		if(this.modDevice == null) {
			AboutMessageWindow.createWindow("������", "�� �� ������� �������� ���������").show();
			return;
		}
		try {
			NewProtocolResultsSearchWindow.getProtocolCreateWindow(this.modDevice).show();
		} catch (IOException ioExp) {
			System.err.println("������ ��� �������� ���� NewProtocolResultsSearchWindow");
			ioExp.printStackTrace();
		}
	}
}