package ProtocolCreatePack;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import AboutMessageForm.AboutMessageWindow;
import DataBasePack.DataBaseManager;
import FileManagePack.FileManager;
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
	private CheckBox printCheckBox;
	
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
			FileManager.ItemsToLines(new File(".").getAbsolutePath() + "//files//ranks.txt", militaryRanks);
		}
		catch(IOException ioExp) {
			militaryRanks.add("Полковник");
			militaryRanks.add("Подполковник");
			militaryRanks.add("Майор");
		}
		this.militaryStatusComboBox.setItems(militaryRanks);
		
		docTypes = FXCollections.observableArrayList();	
		this.reasonTextArea.setVisible(false);
		this.decisionLabel.setVisible(false);
		
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
		if (docTypeComboBox.getSelectionModel().getSelectedItem().toString().equals("Свидетельство о поверке")) {
			this.decisionLabel.setText("признан пригодным к применению");
			this.reasonTextArea.setVisible(false);
		}
		else {
			this.decisionLabel.setText("признан не пригодным к применению по следующим причинам");
			this.reasonTextArea.setVisible(true);
		}
	}
	
	@SuppressWarnings("finally")
	@FXML
	private void createBtnClick() {
		
		if (!checkDocType()) {
			try {
				AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Вы не выбрали тип создаваемого документа");
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
				   		protocoledResult.get(0).getMyOwner().getMyOwner().getSerialNumber() + " от " + strDt;
		newProtocolName = "Протокол поверки для " + addStr + ".xls";
		newDocumentName = docTypeComboBox.getSelectionModel().getSelectedItem().toString() + " " + addStr + ".doc";
		
		//Скрываем информационные поля и показываем прогресс индикатор
		infoBox.toBack();
		infoBox.setOpacity(0.1);
		progressPane.setVisible(true);
		
		//Запускаем поток создания протокола, если были проведены измерения
		if (protocoledResult != null) {
			ProtocolCreateService protocolService = new ProtocolCreateService(this.newProtocolName, protocoledResult, verification);
			protocolService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent arg0) {
					infoBox.toFront();
					infoBox.setOpacity(1.0);
					progressPane.setVisible(false);
				}				
			});
			protocolService.setOnFailed(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					infoBox.toFront();
					infoBox.setOpacity(1.0);
					progressPane.setVisible(false);  
					AboutMessageWindow aboutWin;
					try {
						aboutWin = new AboutMessageWindow("Ошибка", "Произошла ошибка при создании протокола.\nПовторите попытку.");
						aboutWin.show();
					} catch (IOException ioExp) {
						ioExp.printStackTrace();
					}
					finally {
						return;
					}					
				}				
			});
			protocolService.start();
		}
		
		//Запускаем поток создания документа (извещения о непригодности или свидетельства о поверке)
		
		
		//Вносим запись в БД о созданных протоколе и документе
		try {
			makeRecordInDB();
		}
		catch(SQLException sqlExp) {
			try {
				AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Ошибка внесения данных о созданном протоколе в БД");
				msgWin.show();
			}
			catch(IOException ioExp) {
				ioExp.getStackTrace();
			}
		}		
	}
	
	private void makeRecordInDB() throws SQLException {
		String date = protocoledResult.get(0).getDateOfMeasByString();
		String devName = protocoledResult.get(0).getMyOwner().getMyOwner().getName();
		String devType = protocoledResult.get(0).getMyOwner().getMyOwner().getType();
		String devSerN = protocoledResult.get(0).getMyOwner().getMyOwner().getSerialNumber();
		String pathToProtocol = new File(".").getAbsolutePath() + "//Protocols//" + newProtocolName;
		String pathToDocument = new File(".").getAbsolutePath() + "//Protocols//" + newDocumentName;
		String docT = docTypeComboBox.getSelectionModel().getSelectedItem().toString();
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
		if (this.docTypeComboBox.getSelectionModel().getSelectedItem().toString().equals("Свидетельство о поверке")) {
			return "годным";
		}
		else {
			return "не годным";
		}
	}
	public String getProtocolNumber() {return "";}
	public String getDocumentNumber() {return this.docNumberTextField.getText();}
	
}
