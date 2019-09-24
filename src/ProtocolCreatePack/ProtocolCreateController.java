package ProtocolCreatePack;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import AboutMessageForm.AboutMessageWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Device;
import FileManagePack.FileManager;
import VerificationForm.VerificationWindow;
import VerificationPack.MeasResult;
import VerificationPack.VerificationProcedure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProtocolCreateController {

	@FXML
	private ComboBox<String> docTypeComboBox;
	@FXML 
	private ComboBox<String> militaryStatusComboBox;
	
	@FXML
	private TextField docNumberTextField;	
	@FXML
	private TextField militaryBaseName;
	@FXML 
	private TextField bossNameTextFiled;
	@FXML
	private TextField workerNameTextField;
	
	@FXML
	private DatePicker verificationDate;
	
	@FXML
	private TextArea reasonTextArea;
	
	@FXML
	private Label devNameLabel;
	@FXML
	private Label devSerNLabel;
	@FXML
	private Label devOwnerLabel;
	@FXML
	private Label verTypeLabel;
	@FXML
	private Label decisionLabel;
	
	@FXML
	private RadioButton etalonRB;
	@FXML
	private RadioButton siRB;
	private ToggleGroup siGroup;
	@FXML
	private RadioButton printRB;
	
	@FXML
	private Button createBtn;
		
	//------------------------------------
	@FXML
	private StackPane stackPane;
	@FXML
	private ProgressIndicator progress;
	@FXML
	private Label infoLabel;
	@FXML
	private VBox infoBox;
	@FXML
	private AnchorPane progressPane;	
	//------------------------------------
	
	private ObservableList<String> militaryRanks;
	private ObservableList<String> docTypes;
	private String newProtocolName;
	private String newDocumentName;
	private ArrayList<MeasResult> protocoledResult;
	private VerificationProcedure verification;
	
	@FXML
	private void initialize() throws IOException {
		infoBox.toFront();
		infoBox.setOpacity(1.0);
		progressPane.setVisible(false);
		
		militaryRanks = FXCollections.observableArrayList();
		try {
			FileManager.LinesToItems(new File(".").getAbsolutePath() + "//files//ranks.txt", militaryRanks);
		}
		catch(IOException ioExp) {
			militaryRanks.add("���������");
			militaryRanks.add("������������");
			militaryRanks.add("�����");
		}
		this.militaryStatusComboBox.setItems(militaryRanks);
		
		docTypes = FXCollections.observableArrayList();	
		this.reasonTextArea.setVisible(false);
		this.decisionLabel.setVisible(false);
		
		Device device = VerificationWindow.getVerificationWindow().getController().verificatedDevice;
		this.devNameLabel.setText(device.getName() + " " + device.getType());
		this.devSerNLabel.setText(device.getSerialNumber());
		this.devOwnerLabel.setText(device.getOwner());
		
		siGroup = new ToggleGroup();
		etalonRB.setToggleGroup(siGroup);
		siRB.setToggleGroup(siGroup);
		siRB.setSelected(true);
	}
	
	@FXML
	private void docTypeComboBoxChange() {
		if (!this.decisionLabel.isVisible()) {
			this.decisionLabel.setVisible(true);
		}
		if (docTypeComboBox.getSelectionModel().getSelectedItem().toString().equals("C������������ � �������")) {
			this.decisionLabel.setText("������� ��������� � ����������");
			this.reasonTextArea.setVisible(false);
		}
		else {
			this.decisionLabel.setText("������� �� ��������� � ���������� �� ��������� ��������");
			this.reasonTextArea.setVisible(true);
		}
	}
	
	@SuppressWarnings("finally")
	@FXML
	private void createBtnClick() {
		
		if (!checkDocType()) {
			try {
				AboutMessageWindow msgWin = new AboutMessageWindow("������", "�� �� ������� ��� ������������ ���������");
				msgWin.show();
			}
			finally {
				return;
			}
		}		
		Date dt = protocoledResult.get(0).getDateOfMeas();
		String strDt = new SimpleDateFormat("DD-mm-yy HH-mm-ss").format(dt);
		String addStr = protocoledResult.get(0).getMyOwner().getMyOwner().getName() + " " +
				   		protocoledResult.get(0).getMyOwner().getMyOwner().getType() + " " +
				   		protocoledResult.get(0).getMyOwner().getMyOwner().getSerialNumber() + " �� " + strDt;
		newProtocolName = "�������� ������� ��� " + addStr + ".xls";
		newDocumentName = docTypeComboBox.getSelectionModel().getSelectedItem().toString() + " " + addStr + ".doc";		
		//�������� �������������� ���� � ���������� �������� ���������
		infoBox.toBack();
		infoBox.setOpacity(0.1);
		progressPane.setVisible(true);		
		//������� ���������
		creteDocuments();	
	}
	
	private void creteDocuments() {
		//������� ����� �������� ���������
		DocumetnsCreateService docService = new DocumetnsCreateService(this.newProtocolName, protocoledResult, verification);
		//������������� �������� ��� �������� ����������
		docService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				infoBox.toFront();
				infoBox.setOpacity(1.0);
				progressPane.setVisible(false);
				AboutMessageWindow goodResMsg;
				try {
					//������ ������ � �� � ��������� ��������� � ���������
					try {
						makeRecordInDB();
					}
					catch(SQLException sqlExp) {
						try {
							AboutMessageWindow msgWin = new AboutMessageWindow("������", "������ �������� ������ � ��������� ��������� � ��");
							msgWin.show();
						}
						catch(IOException ioExp) {
							ioExp.getStackTrace();
						}
					}
					//��������� ��������� ����, ���� ���������
					if (printRB.isSelected()) {
						String path = new File(".").getAbsoluteFile() + "//Protocols//" + newProtocolName;
						File protocol = new File(path);
						try {
							Desktop.getDesktop().open(protocol);
						}
						catch(IOException ioExp) {
							ioExp.getStackTrace();
						}			
					}
					//���������� ��������� �� �������� ��������
					String docType = docTypeComboBox.getSelectionModel().getSelectedItem().toString();
					goodResMsg = new AboutMessageWindow("�������", "�������� � " + docType + "\n������� �������.");
					goodResMsg.show();
				} catch (IOException ioExp) {
					ioExp.printStackTrace();
				}
				finally {
					ProtocolCreateWindow.closeInstanceWindow();
					ProtocolCreateWindow.deleteProtocolCreateWindow();
				}
			}				
		});
		//������������� �������� ��� �������
		docService.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@SuppressWarnings("finally")
			@Override
			public void handle(WorkerStateEvent event) {
				infoBox.toFront();
				infoBox.setOpacity(1.0);
				progressPane.setVisible(false);  
				AboutMessageWindow aboutWin;
				try {
					aboutWin = new AboutMessageWindow("������", "��������� ������ ��� �������� ���������.\n��������� �������.");
					aboutWin.show();
				} catch (IOException ioExp) {
					ioExp.printStackTrace();
				}
				finally {
					return;
				}					
			}				
		});
		//��������� ����� �������� ����������
		docService.start();
	}
	
	private void makeRecordInDB() throws SQLException {
		String date = protocoledResult.get(0).getDateOfMeasByString();
		String devName = protocoledResult.get(0).getMyOwner().getMyOwner().getName();
		String devType = protocoledResult.get(0).getMyOwner().getMyOwner().getType();
		String devSerN = protocoledResult.get(0).getMyOwner().getMyOwner().getSerialNumber();
		String pathToProtocol = "//Protocols//" + newProtocolName;
		String pathToDocument = "//Documents//" + newDocumentName;
		String docT = docTypeComboBox.getSelectionModel().getSelectedItem().toString();
		if (docT.equals("C������������ � �������")) {
			docT = "Certificate";
		}
		else {
			docT = "Notice";
		}
		String sqlQuery = "INSERT INTO Verifications (data, deviceName, deviceType, deviceSerNum, pathOfDoc, pathOfProtocol, typeOFDoc) VALUES "
											  + "('"+date+"','"+devName+"','"+devType+"','"+devSerN+"','"+pathToDocument+"','"+pathToProtocol+"','"+docT+"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	}
	
	public void setDocTypes(String[] types) {
		for (String str : types) docTypes.add(str);
		docTypeComboBox.setItems(docTypes);
	}
	
	public void setResults(ArrayList<MeasResult> results) {
		this.protocoledResult = results;
	}
	
	private boolean checkDocType() {
		if (this.docTypeComboBox.getSelectionModel().getSelectedIndex() < 0) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public void setVerificationProcedure(VerificationProcedure verificationProc) {
		this.verification = verificationProc;
	}
	
	public String getWorkerName() {return this.workerNameTextField.getText();}
	public String getBossName() {return this.bossNameTextFiled.getText();}
	public String getBossStatus() {return this.militaryStatusComboBox.getSelectionModel().getSelectedItem().toString();}
	public String getResultDecision() {
		if (this.docTypeComboBox.getSelectionModel().getSelectedItem().toString().equals("C������������ � �������")) {
			return "������";
		}
		else {
			return "�� ������";
		}
	}
	public String getProtocolNumber() {return this.docNumberTextField.getText();}
	public String getDocumentNumber() {return this.docNumberTextField.getText();}
	
}
