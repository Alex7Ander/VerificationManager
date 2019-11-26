package ProtocolCreatePack;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
			militaryRanks.add("Полковник");
			militaryRanks.add("Подполковник");
			militaryRanks.add("Майор");
		}
		militaryStatusComboBox.setItems(militaryRanks);
		
		docTypes = FXCollections.observableArrayList();	
		reasonTextArea.setVisible(false);
		decisionLabel.setVisible(false);
		
		Device device = VerificationWindow.getVerificationWindow().getController().verificatedDevice;
		devNameLabel.setText(device.getName() + " " + device.getType());
		devSerNLabel.setText(device.getSerialNumber());
		devOwnerLabel.setText(device.getOwner());
		
		siGroup = new ToggleGroup();
		etalonRB.setToggleGroup(siGroup);
		siRB.setToggleGroup(siGroup);
		siRB.setSelected(true);
	}
	
	@FXML
	private void docTypeComboBoxChange() {
		if (!decisionLabel.isVisible()) {
			decisionLabel.setVisible(true);
		}
		if (docTypeComboBox.getSelectionModel().getSelectedItem().toString().equals("Cвидетельство о поверке")) {
			decisionLabel.setText("признан пригодным к применению");
			reasonTextArea.setVisible(false);
		}
		else {
			decisionLabel.setText("признан не пригодным к применению по следующим причинам");
			reasonTextArea.setVisible(true);
		}
	}
	
	@FXML
	private void createBtnClick() throws IOException{
		if (!checkDocType()) {
			AboutMessageWindow.createWindow("Ошибка", "Вы не выбрали тип создаваемого документа").show();
			return;
		}		
		Date dt = protocoledResult.get(0).getDateOfMeas();
		String strDt = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(dt); //
		String addStr = protocoledResult.get(0).getMyOwner().getMyOwner().getName() + " " +
				   		protocoledResult.get(0).getMyOwner().getMyOwner().getType() + " " +
				   		protocoledResult.get(0).getMyOwner().getMyOwner().getSerialNumber() + " от " + strDt;
		newProtocolName = "Протокол поверки для " + addStr + ".xls";
		newDocumentName = docTypeComboBox.getSelectionModel().getSelectedItem().toString() + " " + addStr + ".doc";		
		//Скрываем информационные поля и показываем прогресс индикатор
		infoBox.toBack();
		infoBox.setOpacity(0.1);
		progressPane.setVisible(true);	
		verification.setFinallyInformation(this);
		//Создаем документы
		creteDocuments();	
	}
	
	private void creteDocuments() {
		//Создаем поток создания протокола
		DocumetnsCreateService docService = new DocumetnsCreateService(newProtocolName, newDocumentName, protocoledResult, verification);
		//Устанавливаем действие при успешном завершении
		docService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				infoBox.toFront();
				infoBox.setOpacity(1.0);
				progressPane.setVisible(false);
				try {
					//Вносим запись в БД о созданных протоколе и документе
					try {
						makeRecordInDB();
					}
					catch(SQLException sqlExp) {
						AboutMessageWindow.createWindow("Ошибка", "Ошибка внесения данных о созданном протоколе в БД").show();
					}
					//Открываем созданный файл, если требуется
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
					//Показываем сообщение об успешном создании
					String docType = docTypeComboBox.getSelectionModel().getSelectedItem();
					AboutMessageWindow.createWindow("Успешно", "Протокол и " + docType + "\nуспешно созданы.").show();
				}
				finally {
					ProtocolCreateWindow.closeInstanceWindow();
					ProtocolCreateWindow.deleteProtocolCreateWindow();
				}
			}				
		});
		//Устанавливаем действие при провале
		docService.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				infoBox.toFront();
				infoBox.setOpacity(1.0);
				progressPane.setVisible(false);  
				AboutMessageWindow.createWindow("Ошибка", "Произошла ошибка при создании протокола.\nПовторите попытку.").show();
				return;
			}				
		});
		//Запускаем поток создания документов
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
		if (docT.equals("Cвидетельство о поверке")) {
			docT = "Certificate";
		}
		else {
			docT = "Notice";
		}
		String sqlQuery = "INSERT INTO Verifications (Date, NameOfDevice, TypeOfDevice, SerialNumber, PathOfDoc, PathOfProtocol, TypeOfDoc) VALUES "
											  + "('"+date+"','"+devName+"','"+devType+"','"+devSerN+"','"+pathToDocument+"','"+pathToProtocol+"','"+docT+"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	}
	
	public void setDocTypes(String[] types) {
		for (String str : types) docTypes.add(str);
		docTypeComboBox.setItems(docTypes);
	}
	
	public void setResults(ArrayList<MeasResult> results) {
		protocoledResult = results;
	}
	
	private boolean checkDocType() {
		if (docTypeComboBox.getSelectionModel().getSelectedIndex() < 0) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public void setVerificationProcedure(VerificationProcedure verificationProc) {
		verification = verificationProc;
	}
	
	public String getWorkerName() {return workerNameTextField.getText();}
	public String getBossName() {return bossNameTextFiled.getText();}
	public String getBossStatus() {
		String status = null;
		try {
			status = militaryStatusComboBox.getSelectionModel().getSelectedItem().toString();
		} catch (NullPointerException npExp) {
			status = "Генерал-завхоз";
		}
		return status;
	}
	public String getResultDecision() {
		if (docTypeComboBox.getSelectionModel().getSelectedItem().toString().equals("Cвидетельство о поверке")) {
			return "годным";
		}
		else {
			return "не годным";
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
			return "Эталон";
		} else {
			return "Средство измерения ";
		}
	}
	public String getDocType() { 
		return docTypeComboBox.getSelectionModel().getSelectedItem().toString();
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
		} catch (ParseException pExp) {
			pExp.getStackTrace();
			calend.setTime(new Date());
		}		
		calend.add(Calendar.YEAR, 1);
		String strDate = new SimpleDateFormat("dd-MM-yyyy").format(calend.getTime());		
		return strDate;
	}
	public String getMilitryBaseName() {
		return militaryBaseName.getText();
	}
}
