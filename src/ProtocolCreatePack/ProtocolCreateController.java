package ProtocolCreatePack;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import AboutMessageForm.AboutMessageWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Device;
import FileManagePack.FileManager;
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationForm.VerificationWindow;
import VerificationPack.MeasResult;
import VerificationPack.VerificationProcedure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
	private TextField verificatonMethodologyNameTextField;
	
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
	private Label decisionLabel;
	@FXML
	private Label workTillLabel;
	
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
	private List<MeasResult> protocoledResult;
	private List<MeasResult> nominals;
	private List<ToleranceParametrs> protocoledModuleToleranceParams;
	private List<ToleranceParametrs> protocoledPhaseToleranceParams;
	private VerificationProcedure verification;
	
	@FXML
	private void initialize() throws IOException {
		this.infoBox.toFront();
		this.infoBox.setOpacity(1.0);
		this.progressPane.setVisible(false);
		
		this.militaryRanks = FXCollections.observableArrayList();
		try {
			FileManager.LinesToItems(new File(".").getAbsolutePath() + "//files//ranks.txt", militaryRanks);
		}
		catch(IOException ioExp) {
			this.militaryRanks.add("���������");
			this.militaryRanks.add("������������");
			this.militaryRanks.add("�����");
		}
		this.militaryStatusComboBox.setItems(militaryRanks);
		
		this.docTypes = FXCollections.observableArrayList();	
		this.reasonTextArea.setVisible(false);
		this.decisionLabel.setVisible(false);
		
		Device device = VerificationWindow.getVerificationWindow().getController().verificatedDevice;
		this.devNameLabel.setText(device.getName() + " " + device.getType());
		this.devSerNLabel.setText(device.getSerialNumber());
		this.devOwnerLabel.setText(device.getOwner());
		
		this.siGroup = new ToggleGroup();
		this.etalonRB.setToggleGroup(siGroup);
		this.siRB.setToggleGroup(siGroup);
		this.siRB.setSelected(true);
	}
	
	@FXML
	private void docTypeComboBoxChange() {
		if (!this.decisionLabel.isVisible()) {
			this.decisionLabel.setVisible(true);
		}
		if (this.docTypeComboBox.getSelectionModel().getSelectedItem().toString().equals("C������������ � �������")) {
			this.decisionLabel.setText("������� ��������� � ����������");
			this.reasonTextArea.setVisible(false);
			this.verificationDate.setVisible(true);
			this.workTillLabel.setVisible(true);
		}
		else {
			this.decisionLabel.setText("������� �� ��������� � ���������� �� ��������� ��������");
			this.reasonTextArea.setVisible(true);
			this.verificationDate.setVisible(false);
			this.workTillLabel.setVisible(false);
		}
	}
	
	@FXML
	private void createBtnClick() throws IOException{
		if (!checkDocType()) {
			AboutMessageWindow.createWindow("������", "�� �� ������� ��� ������������ ���������").show();
			return;
		}		
		Date dt = this.protocoledResult.get(0).getDateOfMeas();
		String strDt = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(dt); //
		String addStr = this.protocoledResult.get(0).getMyOwner().getMyOwner().getName() + " " +
				this.protocoledResult.get(0).getMyOwner().getMyOwner().getType() + " " +
				this.protocoledResult.get(0).getMyOwner().getMyOwner().getSerialNumber() + " �� " + strDt;
		this.newProtocolName = "�������� ������� ��� " + addStr + ".xls";
		this.newDocumentName = docTypeComboBox.getSelectionModel().getSelectedItem().toString() + " " + addStr + ".doc";		
		//�������� �������������� ���� � ���������� �������� ���������
		this.infoBox.toBack();
		this.infoBox.setOpacity(0.1);
		this.progressPane.setVisible(true);	
		this.verification.setFinallyInformation(this);
		//������� ���������
		creteDocuments();	
	}
	
	private void creteDocuments() {	
		//������� ����� �������� ���������
		DocumetnsCreateService docService = new DocumetnsCreateService(this.newProtocolName, 
																		this.newDocumentName, 
																		this.protocoledResult, 
																		this.nominals, 
																		this.protocoledModuleToleranceParams,
																		this.protocoledPhaseToleranceParams,
																		this.verification);
		//������������� �������� ��� �������� ����������
		docService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				infoBox.toFront();
				infoBox.setOpacity(1.0);
				progressPane.setVisible(false);
				try {
					//������ ������ � �� � ��������� ��������� � ���������
					try {
						makeRecordInDB();
					}
					catch(SQLException sqlExp) {
						AboutMessageWindow.createWindow("������", "������ � ��������� ���������\n � �� �� �������.\n").show();
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
					String docType = docTypeComboBox.getSelectionModel().getSelectedItem();
					AboutMessageWindow.createWindow("�������", "�������� � " + docType + "\n������� �������.").show();
				}
				finally {
					ProtocolCreateWindow.closeInstanceWindow();
					ProtocolCreateWindow.deleteProtocolCreateWindow();
				}
			}				
		});
		//������������� �������� ��� �������
		docService.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				infoBox.toFront();
				infoBox.setOpacity(1.0);
				progressPane.setVisible(false); 
				AboutMessageWindow.createWindow("������", "��������� ������ ���\n�������� ���������.\n��������� �������.").show();
				return;
			}
		});
		//��������� ����� �������� ����������
		docService.start();
	}
	
	private void makeRecordInDB() throws SQLException {
		String date = this.protocoledResult.get(0).getDateOfMeasByString();
		String devId = Integer.toString(protocoledResult.get(0).getMyOwner().getMyOwner().getId());
		String pathToProtocol = "//Protocols//" + newProtocolName;
		String pathToDocument = "//Documents//" + newDocumentName;
		String docT = docTypeComboBox.getSelectionModel().getSelectedItem().toString();
		if (docT.equals("C������������ � �������")) {
			docT = "Certificate";
		}
		else {
			docT = "Notice";
		}		
		String sqlQuery = "INSERT INTO Verifications (DeviceId, VerificationDate, PathOfDoc, PathOfProtocol, TypeOfDoc) VALUES "
		+ "('"+devId+"','"+date+"','"+pathToDocument+"','"+pathToProtocol+"','"+docT+"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	}
	
	public void setDocTypes(String[] types) {
		for (String str : types) docTypes.add(str);
		this.docTypeComboBox.setItems(docTypes);
	}
	
	public void setResults(List<MeasResult> results) {
		this.protocoledResult = results;
	}
	
	public void setNominals(List<MeasResult> nominals) {
		this.nominals = nominals;
	}
	
	public void setModuleToleranceParams(List<ToleranceParametrs> protocoledModuleToleranceParams) {
		this.protocoledModuleToleranceParams = protocoledModuleToleranceParams;		
	}
	
	public void setPhaseToleranceParams(List<ToleranceParametrs> protocoledPhaseToleranceParams) {
		this.protocoledPhaseToleranceParams = protocoledPhaseToleranceParams;
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
	
	public String getWorkerName() {
		return this.workerNameTextField.getText();
	}
	public String getBossName() {
		return this.bossNameTextFiled.getText();
	}
	public String getBossStatus() {
		String status = null;
		try {
			status = this.militaryStatusComboBox.getSelectionModel().getSelectedItem().toString();
		}
		catch (NullPointerException npExp) {
			status = "��������� ";
		}
		return status;
	}
	public String getResultDecision() {
		if (docTypeComboBox.getSelectionModel().getSelectedItem().toString().equals("C������������ � �������")) {
			return "������";
		}
		else {
			return "�� ������";
		}
	}
	public String getProtocolNumber() {
		return docNumberTextField.getText();
	}
	public String getDocumentNumber() {
		return docNumberTextField.getText();
	}
	public String getEtalonString() { 
		if (etalonRB.isSelected()) {
			return "������";
		} else {
			return "�������� ��������� ";
		}
	}
	public String getVerificatonMethodologyName() {
		return this.verificatonMethodologyNameTextField.getText();
	}
	public String getDocType() { 
		return this.docTypeComboBox.getSelectionModel().getSelectedItem().toString();
	}
	public String getDateOfCreation() {		
		Date dt = new Date();
		String strDate = new SimpleDateFormat("dd-MM-yyyy").format(dt);
		return strDate;
	}
	public String getFinishDate() {
		Date dt = new Date();
		String sdt = new SimpleDateFormat("dd-MM-yyyy").format(dt);
		Calendar calend = Calendar.getInstance();
		try {
			calend.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(sdt));
		}
		catch (ParseException pExp) {
			pExp.getStackTrace();
			calend.setTime(new Date());
		}
		calend.add(Calendar.YEAR, 1);
		String strDate = new SimpleDateFormat("dd-MM-yyyy").format(calend.getTime());		
		return strDate;
	}
	public String getMilitryBaseName() {
		return this.militaryBaseName.getText();
	}


}